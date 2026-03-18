package com.feng.graduationproject.controller;

import com.feng.graduationproject.common.Result;
import com.feng.graduationproject.entity.DetectionRecord;
import com.feng.graduationproject.mapper.DetectionRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DetectionController {
    
    private final RestTemplate restTemplate;
    
    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;
    
    @Autowired
    private DetectionRecordMapper detectionRecordMapper;
    
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
            
            // 插入检测记录到数据库
            if (responseData != null && responseData.get("code").equals(200)) {
                DetectionRecord record = new DetectionRecord();
                record.setImageName(file.getOriginalFilename());
                
                // 解析缺陷数据
                Map<String, Object> data = (Map<String, Object>) responseData.get("data");
                if (data != null) {
                    // 获取缺陷数量
                    Integer defectCount = (Integer) data.get("defect_count");
                    record.setDefectCount(defectCount != null ? defectCount : 0);
                    
                    // 获取缺陷类型
                    List<Map<String, Object>> defects = (List<Map<String, Object>>) data.get("defects");
                    if (defects != null && !defects.isEmpty()) {
                        String defectTypes = defects.stream()
                                .map(defect -> (String) defect.get("type"))
                                .collect(Collectors.joining(","));
                        record.setDefectTypes(defectTypes);
                    }
                }
                
                record.setDetectionTime(LocalDateTime.now());
                record.setStatus(1); // 检测成功
                
                // 保存记录
                detectionRecordMapper.insert(record);
            }
            
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