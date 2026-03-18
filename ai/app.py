from flask import Flask, request, render_template, redirect, url_for, send_from_directory, jsonify
from flask_cors import CORS
from ultralytics import YOLO
import cv2
import os
import uuid

app = Flask(__name__)
app.config['CORS_HEADERS'] = 'Content-Type'
CORS(app, resources={r"/api/*": {"origins": "*"}})
UPLOAD_FOLDER = "uploads"
RESULT_FOLDER = "results"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(RESULT_FOLDER, exist_ok=True)

model = YOLO("runs/detect/train1/weights/best.pt")
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

@app.route("/results/<filename>")
def get_result_image(filename):
    return send_from_directory(RESULT_FOLDER, filename)

@app.route("/result/<filename>")
def show_result(filename):
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
        img_path = os.path.join(UPLOAD_FOLDER, img_name)
        file.save(img_path)
        
        # 3. 模型检测
        img = cv2.imread(img_path)
        results = model(img, conf=0.3)
        result = results[0]
        
        # 4. 提取检测结果（类别+置信度+位置）
        defects = []
        for box in result.boxes:
            cls_id = int(box.cls[0])
            defects.append({
                "type": class_map.get(cls_id, f"未知类别{cls_id}"),  # 中文类别
                "confidence": f"{box.conf[0]:.2%}",  # 置信度（百分比）
                "x1": float(box.xyxy[0][0]),         # 检测框左上角x
                "y1": float(box.xyxy[0][1]),         # 检测框左上角y
                "x2": float(box.xyxy[0][2]),         # 检测框右下角x
                "y2": float(box.xyxy[0][3])          # 检测框右下角y
            })
        
        # 5. 保存检测结果图
        res_plotted = result.plot(labels=True, conf=True, font_size=12)
        res_name = f"result_{img_name}"
        res_path = os.path.join(RESULT_FOLDER, res_name)
        cv2.imwrite(res_path, res_plotted)

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