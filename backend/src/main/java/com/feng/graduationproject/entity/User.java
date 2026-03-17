package com.feng.graduationproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data  // Lombok注解，自动生成get/set/toString
@TableName("sys_user")  // 对应数据库表名
public class User {
    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;
    private String username;  // 用户名
    private String password;  // 密码
    private String realName;  // 真实姓名（如：冯智鑫）
    private Integer status;   // 状态：1-正常，0-禁用
}
