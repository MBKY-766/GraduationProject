// src/main/java/com/feng/graduationproject/service/StatisticsService.java
package com.feng.graduationproject.service;

import com.feng.graduationproject.entity.DetectionRecord;
import com.feng.graduationproject.mapper.DetectionRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Resource
    private DetectionRecordMapper detectionRecordMapper;

    // 获取统计概览
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 总检测次数
        long totalTests = detectionRecordMapper.selectCount(null);

        // 总缺陷数
        List<DetectionRecord> records = detectionRecordMapper.selectList(null);
        int totalDefects = 0;
        for (DetectionRecord record : records) {
            totalDefects += record.getDefectCount();
        }

        // 合格率
        long passCount = detectionRecordMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<DetectionRecord>()
                .eq("defect_count", 0));
        double passRate = totalTests > 0 ? (double) passCount / totalTests * 100 : 0;

        // 平均检测时间（这里需要根据实际情况计算，暂时返回模拟值）
        double avgTime = 2.3;

        result.put("totalTests", totalTests);
        result.put("totalDefects", totalDefects);
        result.put("passRate", passRate);
        result.put("avgTime", avgTime);

        return result;
    }

    // 获取缺陷类型分布
    public List<Map<String, Object>> getDefectTypeDistribution() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 从数据库中查询所有检测记录
        List<DetectionRecord> records = detectionRecordMapper.selectList(null);

        // 统计每种缺陷类型的数量
        Map<String, Integer> defectTypeCount = new HashMap<>();
        for (DetectionRecord record : records) {
            if (record.getDefectTypes() != null && !record.getDefectTypes().isEmpty()) {
                String[] defectTypes = record.getDefectTypes().split(",");
                for (String defectType : defectTypes) {
                    defectTypeCount.put(defectType, defectTypeCount.getOrDefault(defectType, 0) + 1);
                }
            }
        }

        // 转换为图表所需的数据格式
        for (Map.Entry<String, Integer> entry : defectTypeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }

        // 如果没有数据，返回默认数据
        if (result.isEmpty()) {
            result.add(Map.of("name", "裂纹", "value", 0));
            result.add(Map.of("name", "切割缺陷", "value", 0));
            result.add(Map.of("name", "挤压缺陷", "value", 0));
            result.add(Map.of("name", "侧边压痕", "value", 0));
        }

        return result;
    }

    // 获取检测结果统计
    public Map<String, Object> getDetectionResult() {
        Map<String, Object> result = new HashMap<>();

        // 从数据库中查询最近7天的检测记录
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<DetectionRecord> records = detectionRecordMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<DetectionRecord>()
                .ge("detection_time", sevenDaysAgo));

        // 按日期分组统计
        Map<String, Integer> detectionCountByDate = new HashMap<>();
        Map<String, Integer> defectCountByDate = new HashMap<>();

        // 初始化最近7天的日期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<String> dates = new ArrayList<>();
        List<Integer> detectionCounts = new ArrayList<>();
        List<Integer> defectCounts = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            String dateStr = date.format(formatter);
            dates.add(dateStr);
            detectionCountByDate.put(dateStr, 0);
            defectCountByDate.put(dateStr, 0);
        }

        // 统计每天的检测次数和缺陷数量
        for (DetectionRecord record : records) {
            String dateStr = record.getDetectionTime().format(formatter);
            if (detectionCountByDate.containsKey(dateStr)) {
                detectionCountByDate.put(dateStr, detectionCountByDate.get(dateStr) + 1);
                defectCountByDate.put(dateStr, defectCountByDate.get(dateStr) + record.getDefectCount());
            }
        }

        // 转换为列表
        for (String date : dates) {
            detectionCounts.add(detectionCountByDate.get(date));
            defectCounts.add(defectCountByDate.get(date));
        }

        result.put("dates", dates);
        result.put("detectionCounts", detectionCounts);
        result.put("defectCounts", defectCounts);

        return result;
    }

    // 获取检测趋势
    public Map<String, Object> getDetectionTrend() {
        Map<String, Object> result = new HashMap<>();

        // 从数据库中查询最近6个月的检测记录
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<DetectionRecord> records = detectionRecordMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<DetectionRecord>()
                .ge("detection_time", sixMonthsAgo));

        // 按月份分组统计
        Map<String, Integer> detectionCountByMonth = new HashMap<>();
        Map<String, Integer> defectCountByMonth = new HashMap<>();
        Map<String, Integer> passCountByMonth = new HashMap<>();

        // 初始化最近6个月的月份
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM月");
        List<String> months = new ArrayList<>();
        List<Integer> detectionCounts = new ArrayList<>();
        List<Integer> defectCounts = new ArrayList<>();
        List<Double> passRates = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDateTime month = LocalDateTime.now().minusMonths(i);
            String monthStr = month.format(formatter);
            months.add(monthStr);
            detectionCountByMonth.put(monthStr, 0);
            defectCountByMonth.put(monthStr, 0);
            passCountByMonth.put(monthStr, 0);
        }

        // 统计每月的检测次数、缺陷数量和合格次数
        for (DetectionRecord record : records) {
            String monthStr = record.getDetectionTime().format(formatter);
            if (detectionCountByMonth.containsKey(monthStr)) {
                detectionCountByMonth.put(monthStr, detectionCountByMonth.get(monthStr) + 1);
                defectCountByMonth.put(monthStr, defectCountByMonth.get(monthStr) + record.getDefectCount());
                if (record.getDefectCount() == 0) {
                    passCountByMonth.put(monthStr, passCountByMonth.get(monthStr) + 1);
                }
            }
        }

        // 转换为列表
        for (String month : months) {
            int detectionCount = detectionCountByMonth.get(month);
            detectionCounts.add(detectionCount);
            defectCounts.add(defectCountByMonth.get(month));
            double passRate = detectionCount > 0 ? (double) passCountByMonth.get(month) / detectionCount * 100 : 0;
            passRates.add(passRate);
        }

        result.put("months", months);
        result.put("detectionCounts", detectionCounts);
        result.put("defectCounts", defectCounts);
        result.put("passRates", passRates);

        return result;
    }
}