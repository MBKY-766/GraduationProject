// src/main/java/com/feng/graduationproject/controller/StatisticsController.java
package com.feng.graduationproject.controller;

import com.feng.graduationproject.common.Result;
import com.feng.graduationproject.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @GetMapping("/statistics/overview")
    public Result<Object> getOverview() {
        Map<String, Object> data = statisticsService.getOverview();
        return Result.success(data);
    }

    @GetMapping("/statistics/defect-type")
    public Result<Object> getDefectTypeDistribution() {
        return Result.success(statisticsService.getDefectTypeDistribution());
    }

    @GetMapping("/statistics/detection-result")
    public Result<Object> getDetectionResult() {
        return Result.success(statisticsService.getDetectionResult());
    }

    @GetMapping("/statistics/detection-trend")
    public Result<Object> getDetectionTrend() {
        return Result.success(statisticsService.getDetectionTrend());
    }
}