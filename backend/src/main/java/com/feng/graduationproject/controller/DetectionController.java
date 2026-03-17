package com.feng.graduationproject.controller;

import com.feng.graduationproject.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DetectionController {
    
    private final RestTemplate restTemplate;
    
    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;
    
    public DetectionController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Object> detect(@RequestParam("file") MultipartFile file) {
        try {
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // 创建请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // 调用Python后端的检测接口
            ResponseEntity<Map> response = restTemplate.postForEntity(
                aiServiceUrl + "/api/detect", 
                requestEntity, 
                Map.class
            );
            
            Map<String, Object> responseData = response.getBody();
            
            return Result.success(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("检测失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Spring Boot服务运行正常");
    }
}