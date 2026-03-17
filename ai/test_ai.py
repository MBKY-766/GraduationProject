import torch
print(torch.__version__)  # 输出版本号，比如 2.2.0
print(torch.cuda.is_available())  # 有显卡输出 True，无则 False
exit()