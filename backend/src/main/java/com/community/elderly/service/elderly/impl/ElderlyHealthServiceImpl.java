package com.community.elderly.service.elderly.impl;

import com.community.elderly.dto.elderly.HealthDataInputRequest;
import com.community.elderly.dto.elderly.HealthDataQueryRequest;
import com.community.elderly.dto.elderly.HealthWarningThresholdRequest;
import com.community.elderly.entity.HealthData;
import com.community.elderly.entity.HealthWarning;
import com.community.elderly.entity.HealthWarningThreshold;
import com.community.elderly.mapper.elderly.ElderlyHealthMapper;
import com.community.elderly.service.elderly.ElderlyHealthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ElderlyHealthServiceImpl implements ElderlyHealthService {

    @Autowired
    private ElderlyHealthMapper healthDataMapper;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private static final String HEALTH_DATA_PREFIX = "health:data:";
    private static final String HEALTH_WARNING_PREFIX = "health:warning:";
    private static final String HEALTH_THRESHOLD_PREFIX = "health:threshold:";
    private static final long CACHE_DURATION = 10; // 缓存时长，单位分钟
    private boolean redisEnabled = false;

    @PostConstruct
    public void init() {
        // 检查Redis连接是否可用
        if (redisTemplate != null) {
            try {
                redisTemplate.getConnectionFactory().getConnection().ping();
                redisEnabled = true;
                log.info("Redis连接成功，已启用缓存功能");
            } catch (Exception e) {
                redisEnabled = false;
                log.warn("Redis连接失败，已禁用缓存功能: {}", e.getMessage());
            }
        } else {
            redisEnabled = false;
            log.warn("RedisTemplate未配置，已禁用缓存功能");
        }
    }

    @Override
    public HealthData inputHealthData(HealthDataInputRequest request) {
        HealthData healthData = new HealthData();
        healthData.setUserId(request.getUserId());
        healthData.setSystolicPressure(request.getBloodPressureSystolic());
        healthData.setDiastolicPressure(request.getBloodPressureDiastolic());
        healthData.setHeartRate(request.getHeartRate());
        healthData.setBloodSugar(request.getBloodSugar());
        healthData.setTemperature(request.getBodyTemperature());
        healthData.setWeight(request.getWeight());
        healthData.setDescription(request.getNote());
        healthData.setMonitorTime(LocalDateTime.now());
        healthData.setCreateTime(LocalDateTime.now());
        healthData.setUpdateTime(LocalDateTime.now());

        // 判定是否异常
        boolean isAbnormal = judgeHealthDataAbnormal(healthData);
        healthData.setHealthStatus(isAbnormal ? "abnormal" : "normal");

        // 保存健康数据
        healthDataMapper.insertHealthData(healthData);

        // 如果异常，创建预警记录并推送通知
        if (isAbnormal) {
            createAndPushWarning(healthData);
        }

        // 缓存健康数据
        cacheHealthData(healthData);

        return healthData;
    }

    @Override
    public Map<String, Object> queryHealthDataByTimeRange(HealthDataQueryRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 尝试从缓存获取
        String cacheKey = HEALTH_DATA_PREFIX + "query:" + request.getUserId() + ":" + request.getStartTime() + ":" + request.getEndTime();
        if (redisEnabled) {
            try {
                Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
                if (cachedResult != null && cachedResult instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resultMap = (Map<String, Object>) cachedResult;
                    log.debug("从Redis缓存获取健康数据查询结果");
                    return resultMap;
                }
            } catch (Exception e) {
                log.warn("从Redis获取缓存失败，将从数据库查询: {}", e.getMessage());
            }
        }

        // 如果没有传入结束时间，默认设置为当前时间，避免查询未来数据
        LocalDateTime endTime = request.getEndTime();
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        // 从数据库查询
        List<HealthData> healthDataList = healthDataMapper.selectHealthDataByUserId(
                request.getUserId(),
                request.getStartTime(),
                endTime,
                (request.getPageNum() - 1) * request.getPageSize(),
                request.getPageSize()
        );

        int total = healthDataMapper.countHealthDataByUserId(
                request.getUserId(),
                request.getStartTime(),
                endTime
        );

        result.put("list", healthDataList);
        result.put("total", total);
        result.put("pageNum", request.getPageNum());
        result.put("pageSize", request.getPageSize());

        // 缓存查询结果
        if (redisEnabled) {
            try {
                redisTemplate.opsForValue().set(cacheKey, result, CACHE_DURATION, TimeUnit.MINUTES);
                log.debug("健康数据查询结果已缓存到Redis");
            } catch (Exception e) {
                log.warn("缓存健康数据查询结果到Redis失败: {}", e.getMessage());
            }
        }

        return result;
    }

    @Override
    public boolean judgeHealthDataAbnormal(HealthData healthData) {
        // 获取预警阈值配置
        List<HealthWarningThreshold> thresholds = getCurrentWarningThresholds();
        if (CollectionUtils.isEmpty(thresholds)) {
            // 使用默认阈值
            return isAbnormalWithDefaultThresholds(healthData);
        }

        // 使用配置的阈值
        for (HealthWarningThreshold threshold : thresholds) {
            switch (threshold.getIndicatorType()) {
                case "systolicPressure":
                    if (healthData.getSystolicPressure() != null && 
                            (healthData.getSystolicPressure() < threshold.getMinValue() || 
                             healthData.getSystolicPressure() > threshold.getMaxValue())) {
                        return true;
                    }
                    break;
                case "diastolicPressure":
                    if (healthData.getDiastolicPressure() != null && 
                            (healthData.getDiastolicPressure() < threshold.getMinValue() || 
                             healthData.getDiastolicPressure() > threshold.getMaxValue())) {
                        return true;
                    }
                    break;
                case "heartRate":
                    if (healthData.getHeartRate() != null && 
                            (healthData.getHeartRate() < threshold.getMinValue() || 
                             healthData.getHeartRate() > threshold.getMaxValue())) {
                        return true;
                    }
                    break;
                case "bloodSugar":
                    if (healthData.getBloodSugar() != null && 
                            (healthData.getBloodSugar() < threshold.getMinValue() || 
                             healthData.getBloodSugar() > threshold.getMaxValue())) {
                        return true;
                    }
                    break;
                case "temperature":
                    if (healthData.getTemperature() != null && 
                            (healthData.getTemperature() < threshold.getMinValue() || 
                             healthData.getTemperature() > threshold.getMaxValue())) {
                        return true;
                    }
                    break;
            }
        }

        return false;
    }

    @Override
    public void pushHealthWarningNotification(HealthWarning warning) {
        // 这里实现推送逻辑，实际项目中可能需要集成消息队列或推送服务
        log.info("推送健康预警通知: 用户ID={}, 预警类型={}, 预警信息={}", 
                warning.getUserId(), warning.getWarningType(), warning.getWarningMessage());
        
        // 模拟推送通知给家属和护工
        // 1. 通知家属
        log.info("通知家属: 用户ID={}的健康数据异常，请及时关注", warning.getUserId());
        // 2. 通知护工
        log.info("通知护工: 用户ID={}的健康数据异常，请及时处理", warning.getUserId());
    }

    @Override
    public HealthWarningThreshold configureWarningThreshold(HealthWarningThresholdRequest request) {
        // 先根据指标类型查询是否已存在记录
        HealthWarningThreshold existingThreshold = healthDataMapper.selectHealthWarningThresholdByType(request.getIndicatorType());
        
        HealthWarningThreshold threshold = new HealthWarningThreshold();
        if (existingThreshold != null) {
            // 已存在，更新记录
            threshold.setId(existingThreshold.getId());
            threshold.setCreateTime(existingThreshold.getCreateTime());
        } else {
            // 不存在，新建记录
            threshold.setCreateTime(LocalDateTime.now());
        }
        threshold.setIndicatorType(request.getIndicatorType());
        threshold.setMinValue(request.getMinValue());
        threshold.setMaxValue(request.getMaxValue());
        threshold.setIsActive(request.getIsActive());
        threshold.setUpdateTime(LocalDateTime.now());

        if (existingThreshold != null) {
            healthDataMapper.updateHealthWarningThreshold(threshold);
        } else {
            healthDataMapper.insertHealthWarningThreshold(threshold);
        }

        // 更新缓存
        if (redisEnabled) {
            redisTemplate.opsForValue().set(
                    HEALTH_THRESHOLD_PREFIX + threshold.getIndicatorType(), 
                    threshold, 
                    CACHE_DURATION, 
                    TimeUnit.MINUTES
            );

            // 清除阈值列表缓存
            redisTemplate.delete(HEALTH_THRESHOLD_PREFIX + "list");
        }

        return threshold;
    }

    @Override
    public List<HealthWarningThreshold> getCurrentWarningThresholds() {
        // 尝试从缓存获取
        String cacheKey = HEALTH_THRESHOLD_PREFIX + "list";
        if (redisEnabled) {
            try {
                Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
                if (cachedValue != null && cachedValue instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<HealthWarningThreshold> cachedThresholds = (List<HealthWarningThreshold>) cachedValue;
                    if (!CollectionUtils.isEmpty(cachedThresholds)) {
                        log.debug("从Redis缓存获取健康预警阈值配置");
                        return cachedThresholds;
                    }
                }
            } catch (Exception e) {
                log.warn("从Redis获取缓存失败，将从数据库查询: {}", e.getMessage());
            }
        }

        // 从数据库获取
        List<HealthWarningThreshold> thresholds = healthDataMapper.selectActiveHealthWarningThresholds();
        if (CollectionUtils.isEmpty(thresholds)) {
            // 初始化默认阈值
            thresholds = initDefaultThresholds();
        }

        // 缓存阈值配置
        if (redisEnabled) {
            try {
                redisTemplate.opsForValue().set(cacheKey, thresholds, CACHE_DURATION, TimeUnit.MINUTES);
                log.debug("健康预警阈值配置已缓存到Redis");
            } catch (Exception e) {
                log.warn("缓存健康预警阈值配置到Redis失败: {}", e.getMessage());
            }
        }

        return thresholds;
    }

    @Override
    public Map<Long, List<Long>> intelligentMatch() {
        // 实现协同过滤算法，用于供需智能匹配
        // 这里是一个简化的实现，实际项目中可能需要更复杂的算法
        log.info("执行供需智能匹配");

        // 模拟数据：用户需求和护工技能
        Map<Long, List<String>> userNeeds = new HashMap<>();
        userNeeds.put(1L, Arrays.asList("血压监测", "心率监测", "血糖监测"));
        userNeeds.put(2L, Arrays.asList("血压监测", "体温监测"));
        userNeeds.put(3L, Arrays.asList("心率监测", "血糖监测", "体温监测"));

        Map<Long, List<String>> caregiverSkills = new HashMap<>();
        caregiverSkills.put(101L, Arrays.asList("血压监测", "心率监测"));
        caregiverSkills.put(102L, Arrays.asList("血糖监测", "体温监测"));
        caregiverSkills.put(103L, Arrays.asList("血压监测", "心率监测", "血糖监测", "体温监测"));

        // 基于内容的协同过滤
        Map<Long, List<Long>> matches = new HashMap<>();
        for (Map.Entry<Long, List<String>> userEntry : userNeeds.entrySet()) {
            Long userId = userEntry.getKey();
            List<String> needs = userEntry.getValue();

            List<Long> matchedCaregivers = new ArrayList<>();
            for (Map.Entry<Long, List<String>> caregiverEntry : caregiverSkills.entrySet()) {
                Long caregiverId = caregiverEntry.getKey();
                List<String> skills = caregiverEntry.getValue();

                // 计算匹配度
                int matchCount = 0;
                for (String need : needs) {
                    if (skills.contains(need)) {
                        matchCount++;
                    }
                }

                // 匹配度超过50%则认为匹配
                if (matchCount >= needs.size() * 0.5) {
                    matchedCaregivers.add(caregiverId);
                }
            }

            matches.put(userId, matchedCaregivers);
        }

        return matches;
    }

    // 辅助方法：创建并推送预警
    private void createAndPushWarning(HealthData healthData) {
        HealthWarning warning = new HealthWarning();
        warning.setHealthDataId(healthData.getId());
        warning.setUserId(healthData.getUserId());
        warning.setCreateTime(LocalDateTime.now());
        warning.setUpdateTime(LocalDateTime.now());
        warning.setNotified(0);

        // 确定预警类型和信息
        StringBuilder warningMessage = new StringBuilder();
        List<String> abnormalIndicators = new ArrayList<>();

        if (healthData.getHeartRate() != null && healthData.getHeartRate() > 100) {
            abnormalIndicators.add("心率");
        }
        if (healthData.getSystolicPressure() != null && healthData.getSystolicPressure() >= 160) {
            abnormalIndicators.add("收缩压");
        }
        if (healthData.getDiastolicPressure() != null && healthData.getDiastolicPressure() >= 100) {
            abnormalIndicators.add("舒张压");
        }
        if (healthData.getBloodSugar() != null && (healthData.getBloodSugar() < 3.9 || healthData.getBloodSugar() > 7.0)) {
            abnormalIndicators.add("血糖");
        }
        if (healthData.getTemperature() != null && (healthData.getTemperature() < 36.0 || healthData.getTemperature() > 37.3)) {
            abnormalIndicators.add("体温");
        }

        if (!abnormalIndicators.isEmpty()) {
            warningMessage.append("异常指标：").append(String.join(", ", abnormalIndicators));
            warning.setWarningType(String.join(",", abnormalIndicators));
            warning.setWarningLevel("high");
        } else {
            warningMessage.append("健康数据异常");
            warning.setWarningType("unknown");
            warning.setWarningLevel("medium");
        }

        warning.setWarningMessage(warningMessage.toString());

        // 保存预警记录
        healthDataMapper.insertHealthWarning(warning);

        // 推送预警通知
        pushHealthWarningNotification(warning);

        // 缓存预警信息
        if (redisEnabled) {
            try {
                redisTemplate.opsForValue().set(
                        HEALTH_WARNING_PREFIX + warning.getId(), 
                        warning, 
                        CACHE_DURATION, 
                        TimeUnit.MINUTES
                );
            } catch (Exception e) {
                log.warn("缓存预警信息到Redis失败: {}", e.getMessage());
            }
        }
    }

    // 辅助方法：缓存健康数据
    private void cacheHealthData(HealthData healthData) {
        if (redisEnabled) {
            try {
                redisTemplate.opsForValue().set(
                        HEALTH_DATA_PREFIX + healthData.getId(), 
                        healthData, 
                        CACHE_DURATION, 
                        TimeUnit.MINUTES
                );

                // 更新用户最新健康数据缓存
                redisTemplate.opsForValue().set(
                        HEALTH_DATA_PREFIX + "latest:" + healthData.getUserId(), 
                        healthData, 
                        CACHE_DURATION, 
                        TimeUnit.MINUTES
                );
            } catch (Exception e) {
                log.warn("缓存健康数据到Redis失败: {}", e.getMessage());
            }
        }
    }

    // 辅助方法：使用默认阈值判定异常
    private boolean isAbnormalWithDefaultThresholds(HealthData healthData) {
        // 心率 > 100 触发预警
        if (healthData.getHeartRate() != null && healthData.getHeartRate() > 100) {
            return true;
        }
        // 收缩压 ≥ 160 触发预警
        if (healthData.getSystolicPressure() != null && healthData.getSystolicPressure() >= 160) {
            return true;
        }
        // 舒张压 ≥ 100 触发预警
        if (healthData.getDiastolicPressure() != null && healthData.getDiastolicPressure() >= 100) {
            return true;
        }
        // 血糖异常：< 3.9 或 > 7.0
        if (healthData.getBloodSugar() != null && (healthData.getBloodSugar() < 3.9 || healthData.getBloodSugar() > 7.0)) {
            return true;
        }
        // 体温异常：< 36.0 或 > 37.3
        if (healthData.getTemperature() != null && (healthData.getTemperature() < 36.0 || healthData.getTemperature() > 37.3)) {
            return true;
        }
        return false;
    }

    // 辅助方法：初始化默认阈值
    private List<HealthWarningThreshold> initDefaultThresholds() {
        List<HealthWarningThreshold> defaultThresholds = new ArrayList<>();

        HealthWarningThreshold systolicThreshold = new HealthWarningThreshold();
        systolicThreshold.setIndicatorType("systolicPressure");
        systolicThreshold.setMinValue(90.0);
        systolicThreshold.setMaxValue(140.0);
        systolicThreshold.setIsActive(1);
        systolicThreshold.setCreateTime(LocalDateTime.now());
        systolicThreshold.setUpdateTime(LocalDateTime.now());
        defaultThresholds.add(systolicThreshold);

        HealthWarningThreshold diastolicThreshold = new HealthWarningThreshold();
        diastolicThreshold.setIndicatorType("diastolicPressure");
        diastolicThreshold.setMinValue(60.0);
        diastolicThreshold.setMaxValue(90.0);
        diastolicThreshold.setIsActive(1);
        diastolicThreshold.setCreateTime(LocalDateTime.now());
        diastolicThreshold.setUpdateTime(LocalDateTime.now());
        defaultThresholds.add(diastolicThreshold);

        HealthWarningThreshold heartRateThreshold = new HealthWarningThreshold();
        heartRateThreshold.setIndicatorType("heartRate");
        heartRateThreshold.setMinValue(60.0);
        heartRateThreshold.setMaxValue(100.0);
        heartRateThreshold.setIsActive(1);
        heartRateThreshold.setCreateTime(LocalDateTime.now());
        heartRateThreshold.setUpdateTime(LocalDateTime.now());
        defaultThresholds.add(heartRateThreshold);

        HealthWarningThreshold bloodSugarThreshold = new HealthWarningThreshold();
        bloodSugarThreshold.setIndicatorType("bloodSugar");
        bloodSugarThreshold.setMinValue(3.9);
        bloodSugarThreshold.setMaxValue(7.0);
        bloodSugarThreshold.setIsActive(1);
        bloodSugarThreshold.setCreateTime(LocalDateTime.now());
        bloodSugarThreshold.setUpdateTime(LocalDateTime.now());
        defaultThresholds.add(bloodSugarThreshold);

        HealthWarningThreshold temperatureThreshold = new HealthWarningThreshold();
        temperatureThreshold.setIndicatorType("temperature");
        temperatureThreshold.setMinValue(36.0);
        temperatureThreshold.setMaxValue(37.3);
        temperatureThreshold.setIsActive(1);
        temperatureThreshold.setCreateTime(LocalDateTime.now());
        temperatureThreshold.setUpdateTime(LocalDateTime.now());
        defaultThresholds.add(temperatureThreshold);

        // 保存默认阈值到数据库
        for (HealthWarningThreshold threshold : defaultThresholds) {
            healthDataMapper.insertHealthWarningThreshold(threshold);
        }

        return defaultThresholds;
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        long totalCount = healthDataMapper.selectTotalCount();
        long abnormalCount = healthDataMapper.selectAbnormalCount();
        long normalCount = totalCount - abnormalCount;
        long todayCount = healthDataMapper.selectTodayCount();
        double coverageRate = totalCount > 0 ? (double) normalCount / totalCount * 100 : 0;

        statistics.put("totalCount", totalCount);
        statistics.put("abnormalCount", abnormalCount);
        statistics.put("normalCount", normalCount);
        statistics.put("todayCount", todayCount);
        statistics.put("coverageRate", String.format("%.2f", coverageRate));

        return statistics;
    }

    @Override
    public Map<String, Object> getHealthDataList(Integer page, Integer size, String name, String status, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();

        int offset = (page - 1) * size;
        List<HealthData> healthDataList = healthDataMapper.selectHealthDataList(offset, size, name, status, startDate, endDate);
        long total = healthDataMapper.selectHealthDataCount(name, status, startDate, endDate);

        result.put("list", healthDataList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (total + size - 1) / size);

        return result;
    }

    @Override
    public Map<String, Object> getHealthTrend(Long userId, Integer days, String startDate, String endDate) {
        Map<String, Object> trend = new HashMap<>();

        LocalDateTime endTime;
        LocalDateTime startTime;

        // 如果提供了日期范围，使用日期范围；否则使用最近days天
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            startTime = LocalDateTime.parse(startDate + " 00:00:00", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endTime = LocalDateTime.parse(endDate + " 23:59:59", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(days);
        }

        List<HealthData> healthDataList = healthDataMapper.selectHealthDataByUserIdAndTimeRange(userId, startTime, endTime);

        List<String> dates = new ArrayList<>();
        List<Double> heartRates = new ArrayList<>();
        List<Double> systolicPressures = new ArrayList<>();
        List<Double> diastolicPressures = new ArrayList<>();
        List<Double> bloodSugars = new ArrayList<>();

        for (HealthData data : healthDataList) {
            dates.add(data.getMonitorTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            if (data.getHeartRate() != null) {
                heartRates.add(data.getHeartRate());
            }
            if (data.getSystolicPressure() != null) {
                systolicPressures.add(data.getSystolicPressure());
            }
            if (data.getDiastolicPressure() != null) {
                diastolicPressures.add(data.getDiastolicPressure());
            }
            if (data.getBloodSugar() != null) {
                bloodSugars.add(data.getBloodSugar());
            }
        }

        trend.put("dates", dates);
        trend.put("heartRates", heartRates);
        trend.put("systolicPressures", systolicPressures);
        trend.put("diastolicPressures", diastolicPressures);
        trend.put("bloodSugars", bloodSugars);

        return trend;
    }

    @Override
    public Map<String, Object> getAllHealthTrend(Integer days, String startDate, String endDate) {
        Map<String, Object> trend = new HashMap<>();

        LocalDateTime endTime;
        LocalDateTime startTime;

        // 如果提供了日期范围，使用日期范围；否则使用最近days天
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            startTime = LocalDateTime.parse(startDate + " 00:00:00", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endTime = LocalDateTime.parse(endDate + " 23:59:59", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(days);
        }

        // 查询所有用户的健康数据
        List<HealthData> healthDataList = healthDataMapper.selectAllHealthDataByTimeRange(startTime, endTime);

        List<String> dates = new ArrayList<>();
        List<Double> heartRates = new ArrayList<>();
        List<Double> systolicPressures = new ArrayList<>();
        List<Double> diastolicPressures = new ArrayList<>();
        List<Double> bloodSugars = new ArrayList<>();

        for (HealthData data : healthDataList) {
            dates.add(data.getMonitorTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            if (data.getHeartRate() != null) {
                heartRates.add(data.getHeartRate());
            }
            if (data.getSystolicPressure() != null) {
                systolicPressures.add(data.getSystolicPressure());
            }
            if (data.getDiastolicPressure() != null) {
                diastolicPressures.add(data.getDiastolicPressure());
            }
            if (data.getBloodSugar() != null) {
                bloodSugars.add(data.getBloodSugar());
            }
        }

        trend.put("dates", dates);
        trend.put("heartRates", heartRates);
        trend.put("systolicPressures", systolicPressures);
        trend.put("diastolicPressures", diastolicPressures);
        trend.put("bloodSugars", bloodSugars);
        
        return trend;
    }

    @Override
    public Map<String, Object> queryHealthData(Long elderlyId, Integer pageNum, Integer pageSize, String healthStatus, String startTime, String endTime) {
        Map<String, Object> result = new HashMap<>();
        
        List<HealthData> healthDataList = healthDataMapper.selectHealthDataByUserId(
            elderlyId, 
            startTime != null ? LocalDateTime.parse(startTime) : null,
            endTime != null ? LocalDateTime.parse(endTime) : null,
            (pageNum - 1) * pageSize,
            pageSize
        );
        
        // 过滤健康状态
        if (healthStatus != null && !healthStatus.isEmpty()) {
            healthDataList = healthDataList.stream()
                .filter(data -> healthStatus.equals(data.getHealthStatus()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // 计算总数
        int total = healthDataMapper.countHealthDataByUserId(
            elderlyId,
            startTime != null ? LocalDateTime.parse(startTime) : null,
            endTime != null ? LocalDateTime.parse(endTime) : null
        );
        
        result.put("list", healthDataList);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }

    @Override
    public HealthData inputHealthData(Long elderlyId, HealthDataInputRequest request) {
        HealthData healthData = new HealthData();
        healthData.setUserId(elderlyId);
        healthData.setHeartRate(request.getHeartRate());
        healthData.setSystolicPressure(request.getBloodPressureSystolic());
        healthData.setDiastolicPressure(request.getBloodPressureDiastolic());
        healthData.setBloodSugar(request.getBloodSugar());
        healthData.setTemperature(request.getBodyTemperature());
        healthData.setWeight(request.getWeight());
        healthData.setDescription(request.getNote());
        healthData.setMonitorTime(LocalDateTime.now());
        healthData.setCreateTime(LocalDateTime.now());
        healthData.setUpdateTime(LocalDateTime.now());
        
        // 判定是否异常
        boolean isAbnormal = judgeHealthDataAbnormal(healthData);
        healthData.setHealthStatus(isAbnormal ? "abnormal" : "normal");
        
        // 保存健康数据
        healthDataMapper.insertHealthData(healthData);
        
        // 如果异常，创建预警记录并推送通知
        if (isAbnormal) {
            createAndPushWarning(healthData);
        }
        
        // 缓存健康数据
        cacheHealthData(healthData);
        
        return healthData;
    }

    @Override
    public void deleteHealthData(Long elderlyId, Long id) {
        // 验证数据是否属于当前老人
        HealthData healthData = healthDataMapper.selectHealthDataById(id);
        if (healthData == null || !healthData.getUserId().equals(elderlyId)) {
            throw new RuntimeException("无权删除此数据");
        }
        
        healthDataMapper.deleteHealthDataById(id);
    }
}
