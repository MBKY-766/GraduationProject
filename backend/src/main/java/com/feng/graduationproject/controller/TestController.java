package com.feng.graduationproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feng.graduationproject.entity.User;
import com.feng.graduationproject.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController  // 标识为控制器，返回JSON
public class TestController {

    @Autowired  // 自动注入UserMapper
    private UserMapper userMapper;

    // 测试接口：http://localhost:8080/api/test
    @GetMapping("/test")
    public String test() {
        // 测试数据库查询（先往sys_user表插入一条数据）
        List<User> userList = userMapper.selectList(new QueryWrapper<User>());
        return "后端环境搭建成功！查询到用户数：" + userList.size();
    }
}
