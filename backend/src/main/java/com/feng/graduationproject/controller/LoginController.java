// src/main/java/com/feng/graduationproject/controller/LoginController.java
package com.feng.graduationproject.controller;

import com.feng.graduationproject.common.Result;
import com.feng.graduationproject.entity.User;
import com.feng.graduationproject.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    @Resource
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        // 从数据库查询用户
        User user = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("username", username)
                .eq("password", password));

        if (user != null) {
            // 检查用户状态
            if (user.getStatus() != 1) {
                return Result.fail("用户已被禁用");
            }
            // 登录成功，生成token（实际项目中应该使用JWT等方式生成）
            String token = "mock-token-" + System.currentTimeMillis();
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            return Result.success(data);
        } else {
            // 登录失败
            return Result.fail("用户名或密码错误");
        }
    }
}