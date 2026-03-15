package com.community.elderly.service.elderly;

import com.community.elderly.dto.elderly.HealthDataInputRequest;
import com.community.elderly.dto.elderly.HealthDataQueryRequest;
import com.community.elderly.dto.elderly.HealthWarningThresholdRequest;
import com.community.elderly.entity.HealthData;
import com.community.elderly.entity.HealthWarning;
import com.community.elderly.entity.HealthWarningThreshold;
import java.util.List;
import java.util.Map;

public interface ElderlyHealthService {
    /**
     * 录入健康数据
     */
    HealthData inputHealthData(HealthDataInputRequest request);
    
    /**
     * 按时间范围查询健康数据
     */
    Map<String, Object> queryHealthDataByTimeRange(HealthDataQueryRequest request);
    
    /**
     * 判定健康数据是否异常
     */
    boolean judgeHealthDataAbnormal(HealthData healthData);
    
    /**
     * 推送健康预警通知
     */
    void pushHealthWarningNotification(HealthWarning warning);
    
    /**
     * 配置健康预警阈值
     */
    HealthWarningThreshold configureWarningThreshold(HealthWarningThresholdRequest request);
    
    /**
     * 获取当前健康预警阈值配置
     */
    List<HealthWarningThreshold> getCurrentWarningThresholds();
    
    /**
     * 实现供需智能匹配（协同过滤算法）
     */
    Map<Long, List<Long>> intelligentMatch();

    /**
     * 获取健康数据统计
     */
    Map<String, Object> getStatistics();
    
    /**
     * 获取健康数据列表
     */
    Map<String, Object> getHealthDataList(Integer page, Integer size, String name, String status, String startDate, String endDate);

    /**
     * 获取健康数据趋势（指定用户）
     */
    Map<String, Object> getHealthTrend(Long userId, Integer days, String startDate, String endDate);

    /**
     * 获取所有用户的健康数据趋势
     */
    Map<String, Object> getAllHealthTrend(Integer days, String startDate, String endDate);
    
    /**
     * 老人查询自己的健康数据
     */
    Map<String, Object> queryHealthData(Long elderlyId, Integer pageNum, Integer pageSize, String healthStatus, String startTime, String endTime);
    
    /**
     * 老人输入自己的健康数据
     */
    HealthData inputHealthData(Long elderlyId, HealthDataInputRequest request);
    
    /**
     * 老人删除自己的健康数据
     */
    void deleteHealthData(Long elderlyId, Long id);
}
