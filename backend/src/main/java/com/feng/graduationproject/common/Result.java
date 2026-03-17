package com.feng.graduationproject.common;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Result <T>{
    // 响应状态码：200成功，500失败，400参数错误，401未登录
    private Integer code;
    // 响应提示信息
    private String msg;
    // 响应数据
    private T data;



    // ========== 静态构造方法（简化调用） ==========
    // 成功响应（带数据+默认提示）
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功响应（自定义提示+数据）
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    // 成功响应（无数据+自定义提示）
    public static <T> Result<T> success(String msg) {
        return new Result<>(200, msg, null);
    }

    // 失败响应（自定义提示）
    public static <T> Result<T> fail(String msg) {
        return new Result<>(500, msg, null);
    }

    // 失败响应（自定义状态码+提示）
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
