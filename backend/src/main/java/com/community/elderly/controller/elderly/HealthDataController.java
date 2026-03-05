package com.community.elderly.controller.elderly;

import com.community.elderly.common.Result;
import com.community.elderly.dto.elderly.HealthDataInputRequest;
import com.community.elderly.dto.elderly.HealthDataQueryRequest;
import com.community.elderly.dto.elderly.HealthWarningThresholdRequest;
import com.community.elderly.entity.HealthData;
import com.community.elderly.entity.HealthWarningThreshold;
import com.community.elderly.service.elderly.ElderlyHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthDataController {

    @Autowired
    private ElderlyHealthService elderlyHealthService;

    /**
     * 录入健康数据（人工录入）
     */
    @PostMapping("/input")
    public Result<?> inputHealthData(@RequestBody HealthDataInputRequest request) {
        HealthData healthData = elderlyHealthService.inputHealthData(request);
        return Result.success(healthData);
    }

    /**
     * 按时间范围查询健康数据
     */
    @PostMapping("/query")
    public Result<?> queryHealthDataByTimeRange(@RequestBody HealthDataQueryRequest request) {
        Map<String, Object> result = elderlyHealthService.queryHealthDataByTimeRange(request);
        return Result.success(result);
    }

    /**
     * 配置健康预警阈值
     */
    @PostMapping("/threshold/configure")
    public Result<?> configureWarningThreshold(@RequestBody HealthWarningThresholdRequest request) {
        HealthWarningThreshold threshold = elderlyHealthService.configureWarningThreshold(request);
        return Result.success(threshold);
    }

    /**
     * 获取当前健康预警阈值配置
     */
    @GetMapping("/threshold/current")
    public Result<?> getCurrentWarningThresholds() {
        List<HealthWarningThreshold> thresholds = elderlyHealthService.getCurrentWarningThresholds();
        return Result.success(thresholds);
    }

    /**
     * 执行供需智能匹配
     */
    @GetMapping("/match/intelligent")
    public Result<?> intelligentMatch() {
        Map<Long, List<Long>> matches = elderlyHealthService.intelligentMatch();
        return Result.success(matches);
    }

    /**
     * 获取健康数据统计
     */
    @GetMapping("/statistics")
    public Result<?> getStatistics() {
        Map<String, Object> statistics = elderlyHealthService.getStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取健康数据列表
     */
    @GetMapping("/list")
    public Result<?> getHealthDataList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        Map<String, Object> result = elderlyHealthService.getHealthDataList(page, size, name, status);
        return Result.success(result);
    }

    /**
     * 获取健康数据趋势（指定用户）
     */
    @GetMapping("/trend/{userId}")
    public Result<?> getHealthTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") Integer days,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> trend = elderlyHealthService.getHealthTrend(userId, days, startDate, endDate);
        return Result.success(trend);
    }

    /**
     * 获取所有用户的健康数据趋势
     */
    @GetMapping("/trend/all")
    public Result<?> getAllHealthTrend(
            @RequestParam(defaultValue = "7") Integer days,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> trend = elderlyHealthService.getAllHealthTrend(days, startDate, endDate);
        return Result.success(trend);
    }
}