from flask import Flask, request, render_template, redirect, url_for, send_from_directory, jsonify
from flask_cors import CORS
from ultralytics import YOLO
import cv2
import os
import uuid
import torch
import numpy as np
from concurrent.futures import ThreadPoolExecutor

app = Flask(__name__)
app.config['CORS_HEADERS'] = 'Content-Type'
CORS(app, resources={r"/api/*": {"origins": "*"}})
UPLOAD_FOLDER = "uploads"
RESULT_FOLDER = "results"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(RESULT_FOLDER, exist_ok=True)

# 模型加载优化 - 仅加载一次
# model = YOLO("runs/detect/train1/weights/best.pt")
# 可选：使用FP16精度加速推理
model = YOLO("runs/detect/train1/weights/best.pt").to('cuda') if torch.cuda.is_available() else YOLO("runs/detect/train1/weights/best.pt")

# 线程池用于并行处理
executor = ThreadPoolExecutor(max_workers=4)

# 在模型加载后添加
class_map = {
    0: "裂纹",
    1: "切割缺陷",
    2: "挤压缺陷",
    3: "侧边压痕"
}
@app.route("/", methods=["GET", "POST"])
def index():
    if request.method == "POST":
        if "file" not in request.files:
            return "请选择图片文件！"
        file = request.files["file"]
        if file.filename == "":
            return "请选择图片文件！"
        img_name = str(uuid.uuid4()) + "." + file.filename.split(".")[-1]
        img_path = os.path.join(UPLOAD_FOLDER, img_name)
        file.save(img_path)
        img = cv2.imread(img_path)
        results = model(img, conf=0.3)
        res_plotted = results[0].plot(labels=True, conf=True, font_size=12)
        res_name = "result_" + img_name
        res_path = os.path.join(RESULT_FOLDER, res_name)
        cv2.imwrite(res_path, res_plotted)
        result_image_url = f"/result/{res_name}"
        print(f"=== 结果图URL路径 ===: {result_image_url}")
        return redirect(url_for("show_result", filename=res_name))
    return render_template("index.html")

# 静态文件路由：提供检测结果图像的访问
# 当前端需要显示检测结果图像时，通过此路由访问
@app.route("/results/<filename>")
def get_result_image(filename):
    # 使用send_from_directory从RESULT_FOLDER目录发送文件
    # 这使得前端可以直接通过URL访问生成的结果图像
    return send_from_directory(RESULT_FOLDER, filename)

# 结果页面路由：显示检测结果的HTML页面
# 当用户通过表单提交检测后，会重定向到这个页面
@app.route("/result/<filename>")
def show_result(filename):
    # 渲染result.html模板，并传递image_filename参数
    # 模板会使用这个参数构建结果图像的URL
    return render_template("result.html", image_filename=filename)
# ========== 新增：供Vue调用的API接口 ==========
@app.route("/api/detect", methods=["POST"])
def api_detect():
    try:
        # 1. 接收前端上传的文件
        if "file" not in request.files:
            return jsonify({
                "code": 400,
                "msg": "未选择图片文件",
                "data": None
            }), 400
        
        file = request.files["file"]
        if file.filename == "":
            return jsonify({
                "code": 400,
                "msg": "未选择图片文件",
                "data": None
            }), 400
        
        # 2. 保存文件（用uuid避免重名）
        img_ext = file.filename.split(".")[-1]
        img_name = f"{str(uuid.uuid4())}.{img_ext}"
        os.path.join(UPLOAD_FOLDER, img_name)
        
        # 文件保存优化 - 直接读取文件内容而不保存到磁盘
        # 注意：如果需要保留原始文件，可以注释掉下面的代码并使用file.save(img_path)

        img_bytes = file.read()
        img = cv2.imdecode(np.frombuffer(img_bytes, np.uint8), cv2.IMREAD_COLOR)
        # 可选：如果需要保存原始文件
        # file.save(img_path)
        
        # 3. 图像预处理和模型检测优化
        # img = cv2.imread(img_path)
        
        # 图像尺寸优化 - 调整到合适大小以加快推理速度
        h, w = img.shape[:2]
        max_size = 640  # YOLO默认输入大小
        if max(h, w) > max_size:
            scale = max_size / max(h, w)
            new_w, new_h = int(w * scale), int(h * scale)
            img = cv2.resize(img, (new_w, new_h))
        
        # 模型推理优化 - 批处理和置信度设置
        results = model(img, conf=0.3, imgsz=640, half=False)  # half=True如果支持FP16
        result = results[0]
        
        # 4. 提取检测结果（类别+置信度+位置）
        defects = []
        # 批量处理检测框
        # 确保张量在CPU上再转换为numpy数组
        boxes = result.boxes.cpu().numpy()  # 先移到CPU再转换为numpy数组
        for box in boxes:
            cls_id = int(box.cls[0])
            defects.append({
                "type": class_map.get(cls_id, f"未知类别{cls_id}"),  # 中文类别
                "confidence": f"{box.conf[0]:.2%}",  # 置信度（百分比）
                "x1": float(box.xyxy[0][0]),         # 检测框左上角x
                "y1": float(box.xyxy[0][1]),         # 检测框左上角y
                "x2": float(box.xyxy[0][2]),         # 检测框右下角x
                "y2": float(box.xyxy[0][3])          # 检测框右下角y
            })
        
        # 5. 保存检测结果图（可选：异步保存以加快响应）
        def save_result_image():
            # 确保结果在CPU上
            result_cpu = result.cpu()
            res_plotted = result_cpu.plot(labels=True, conf=True, font_size=12)
            res_name = f"result_{img_name}"
            res_path = os.path.join(RESULT_FOLDER, res_name)
            cv2.imwrite(res_path, res_plotted)
            return res_name
        
        # 异步保存结果图
        future = executor.submit(save_result_image)
        res_name = future.result()

        result_image_path = f"/results/{res_name}"
        full_url = f"http://localhost:5000{result_image_path}"
        print(f"=== 结果图URL路径 ===: {full_url}")

        # 6. 返回API结果（供Vue前端解析）
        return jsonify({
            "code": 200,
            "msg": "检测成功",
            "data": {
                "result_image": full_url,  # 结果图访问路径
                "defects": defects,                      # 缺陷详情列表
                "defect_count": len(defects)             # 缺陷总数
            }
        })
    
    except Exception as e:
        # 异常捕获，返回友好提示
        print(f"【错误】检测失败: {str(e)}")  # 错误也打印到控制台
        return jsonify({
            "code": 500,
            "msg": f"检测失败：{str(e)}",
            "data": None
        }), 500
if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)