package com.feng.graduationproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.feng.graduationproject.mapper")
public class GraduationProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduationProjectApplication.class, args);
    }

}
