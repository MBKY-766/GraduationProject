from fastapi import FastAPI, UploadFile, File
from ultralytics import YOLO
import cv2
import numpy as np
import io
from PIL import Image

# 初始化FastAPI应用（AI推理服务）
app = FastAPI(title="密封圈缺陷检测AI接口")

# 加载训练好的YOLOv8模型（AI环境中的权重路径）
model = YOLO("D:/seal_defect_detection/runs/detect/train1/weights/best.pt")

# 定义推理接口（接收图片，返回检测结果+带框图片）
@app.post("/detect")
async def detect_defect(file: UploadFile = File(...)):
    # 1. 读取上传的图片
    contents = await file.read()
    img = Image.open(io.BytesIO(contents))
    img_cv = cv2.cvtColor(np.array(img), cv2.COLOR_RGB2BGR)

    # 2. 执行YOLOv8推理
    results = model(img_cv, conf=0.3)

    # 3. 提取检测结果（类别、置信度、坐标）
    defect_results = []
    for box in results[0].boxes:
        defect_results.append({
            "class": model.names[int(box.cls)],  # 缺陷类别
            "confidence": float(box.conf),       # 置信度
            "bbox": box.xyxy.tolist()[0]         # 检测框坐标 [x1,y1,x2,y2]
        })

    # 4. 生成带检测框的图片（转成字节流返回）
    res_plotted = results[0].plot()
    res_rgb = cv2.cvtColor(res_plotted, cv2.COLOR_BGR2RGB)
    img_pil = Image.fromarray(res_rgb)
    img_byte_arr = io.BytesIO()
    img_pil.save(img_byte_arr, format='JPEG')
    img_byte_arr = img_byte_arr.getvalue()

    # 5. 返回结果（JSON格式）
    return {
        "defects": defect_results,  # 缺陷详情
        "has_defect": len(defect_results) > 0,  # 是否有缺陷
        "image": img_byte_arr       # 带检测框的图片字节流
    }

# 启动AI API服务（端口8000，仅限本地访问）
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)