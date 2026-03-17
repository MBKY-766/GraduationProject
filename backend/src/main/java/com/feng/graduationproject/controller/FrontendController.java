package com.feng.graduationproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
    
    @RequestMapping(value = {"/", "/login", "/home", "/detection", "/data-analysis"})
    public String index() {
        return "index.html";
    }
}