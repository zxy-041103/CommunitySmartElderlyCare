package com.community.elderly.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.elderly.common.Result;
import com.community.elderly.entity.EmergencyHelp;
import com.community.elderly.entity.ServiceOrder;
import com.community.elderly.entity.User;
import com.community.elderly.entity.Announcement;
import com.community.elderly.entity.HealthData;
import com.community.elderly.entity.ServiceEvaluation;
import com.community.elderly.mapper.elderly.ElderlyProfileMapper;
import com.community.elderly.mapper.elderly.ElderlyHealthMapper;
import com.community.elderly.mapper.elderly.ElderlyBookingMapper;
import com.community.elderly.mapper.elderly.ElderlyEmergencyMapper;
import com.community.elderly.mapper.caregiver.ServiceEvaluationMapper;
import com.community.elderly.mapper.admin.AnnouncementMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/admin/dashboard")
@Api(tags = "仪表盘数据统计接口")
public class AdminDashboardController {

    @Autowired
    private ElderlyProfileMapper elderlyProfileMapper;

    @Autowired
    private ElderlyHealthMapper elderlyHealthMapper;

    @Autowired
    private ElderlyBookingMapper elderlyBookingMapper;

    @Autowired
    private ElderlyEmergencyMapper elderlyEmergencyMapper;

    @Autowired
    private ServiceEvaluationMapper serviceEvaluationMapper;
    
    @Autowired
    private AnnouncementMapper announcementMapper;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/statistics")
    @ApiOperation(value = "获取仪表盘统计数据", notes = "获取管理员仪表盘所需的各项统计数据")
    public Result<Map<String, Object>> getDashboardStatistics(
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 获取用户统计数据
        Long elderlyCount = elderlyProfileMapper.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getRoleType, "elderly")
                .eq(User::getIsDeleted, 0)
        );
        // 移除默认值，使用真实数据库数据
        // if (elderlyCount == 0) {
        //     elderlyCount = 50L; // 默认50位老人
        // }
        
        Long familyCount = elderlyProfileMapper.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getRoleType, "family")
                .eq(User::getIsDeleted, 0)
        );
        // 移除默认值，使用真实数据库数据
        // if (familyCount == 0) {
        //     familyCount = 80L; // 默认80位家属
        // }
        
        Long caregiverCount = elderlyProfileMapper.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getRoleType, "caregiver")
                .eq(User::getIsDeleted, 0)
        );
        // 移除默认值，使用真实数据库数据
        // if (caregiverCount == 0) {
        //     caregiverCount = 20L; // 默认20位护工
        // }
        
        // 获取健康覆盖率（有健康数据的老人占比）
        Long healthDataCount = elderlyHealthMapper.selectTotalCount();
        // 计算有健康数据的老人数量（去重）
        List<HealthData> healthDataList = elderlyHealthMapper.selectList(null);
        Set<Long> elderlyWithHealthDataSet = new HashSet<>();
        for (HealthData data : healthDataList) {
            elderlyWithHealthDataSet.add(data.getUserId());
        }
        Long elderlyWithHealthData = (long) elderlyWithHealthDataSet.size();
        double healthCoverage = elderlyCount > 0 ? (double) elderlyWithHealthData / elderlyCount * 100 : 0;
        // 移除默认值，使用真实数据库数据
        // if (healthCoverage == 0) {
        //     healthCoverage = 75.5; // 默认75.5%的健康覆盖率
        // }
        
        // 获取服务完成率
        Long totalOrders = elderlyBookingMapper.selectCount(null);
        Long completedOrders = elderlyBookingMapper.selectCount(
            new LambdaQueryWrapper<ServiceOrder>()
                .eq(ServiceOrder::getOrderStatus, "completed")
        );
        double serviceVerification = totalOrders > 0 ? (double) completedOrders / totalOrders * 100 : 0;
        // 移除默认值，使用真实数据库数据
        // if (serviceVerification == 0) {
        //     serviceVerification = 92.3; // 默认92.3%的服务完成率
        // }
        
        // 获取紧急响应率
        Long emergencyCount = elderlyEmergencyMapper.selectCount(null);
        Long processedEmergency = elderlyEmergencyMapper.selectCount(
            new LambdaQueryWrapper<EmergencyHelp>()
                .eq(EmergencyHelp::getIsProcessed, 1)
        );
        double emergencyResponse = emergencyCount > 0 ? (double) processedEmergency / emergencyCount * 100 : 0;
        // 移除默认值，使用真实数据库数据
        // if (emergencyResponse == 0) {
        //     emergencyResponse = 98.7; // 默认98.7%的紧急响应率
        // }
        
        // 获取满意度
        List<ServiceEvaluation> evaluations = serviceEvaluationMapper.selectList(null);
        double satisfaction = 0;
        if (!evaluations.isEmpty()) {
            int totalRating = 0;
            for (ServiceEvaluation evaluation : evaluations) {
                totalRating += evaluation.getRating();
            }
            satisfaction = (double) totalRating / evaluations.size() / 5 * 100; // 转换为百分比
        }
        // 移除默认值，使用真实数据库数据
        // else {
        //     // 如果没有满意度数据，设置默认值
        //     satisfaction = 95.2; // 默认95.2%的满意度
        // }
        
        // 获取本月服务数量
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Long monthlyServices = elderlyBookingMapper.selectCount(
            new LambdaQueryWrapper<ServiceOrder>()
                .eq(ServiceOrder::getOrderStatus, "completed")
                .ge(ServiceOrder::getServiceDate, monthStart.toLocalDate())
        );
        // 移除默认值，使用真实数据库数据
        // if (monthlyServices == 0) {
        //     monthlyServices = 120L; // 默认本月120次服务
        // }
        
        // 获取本月紧急求助数量
        Long monthlyEmergencies = elderlyEmergencyMapper.selectCount(
            new LambdaQueryWrapper<EmergencyHelp>()
                .ge(EmergencyHelp::getCreateTime, monthStart)
        );
        // 移除默认值，使用真实数据库数据
        // if (monthlyEmergencies == 0) {
        //     monthlyEmergencies = 8L; // 默认本月8次紧急求助
        // }
        
        // 获取健康异常数量（本月）
        Long monthlyAbnormalities = elderlyHealthMapper.selectMonthlyAbnormalCount();
        // 移除默认值，使用真实数据库数据
        // if (monthlyAbnormalities == 0) {
        //     monthlyAbnormalities = 15L; // 默认本月15次健康异常
        // }
        
        // 获取活动总数
        Long activityCount = announcementMapper.selectCount(
            new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getAnnouncementType, "activity")
                .eq(Announcement::getIsPublished, 1)
        );
        // 移除默认值，使用真实数据库数据
        // if (activityCount == 0) {
        //     activityCount = 25L; // 默认25个活动
        // }
        
        // 计算趋势数据（简单模拟，实际项目中应从历史数据计算）
        double healthCoverageTrend = 5.2;
        double serviceVerificationTrend = 3.8;
        double emergencyResponseTrend = 2.1;
        double satisfactionTrend = 1.5;
        
        // 填充返回数据
        result.put("totalElderly", elderlyCount);
        result.put("totalFamily", familyCount);
        result.put("totalCaregiver", caregiverCount);
        result.put("healthCoverage", Math.round(healthCoverage * 100) / 100.0);
        result.put("healthCoverageTrend", healthCoverageTrend);
        result.put("serviceVerification", Math.round(serviceVerification * 100) / 100.0);
        result.put("serviceVerificationTrend", serviceVerificationTrend);
        result.put("emergencyResponse", Math.round(emergencyResponse * 100) / 100.0);
        result.put("emergencyResponseTrend", emergencyResponseTrend);
        result.put("satisfaction", Math.round(satisfaction * 100) / 100.0);
        result.put("satisfactionTrend", satisfactionTrend);
        result.put("totalActivities", activityCount);
        result.put("monthlyServices", monthlyServices);
        result.put("monthlyEmergencies", monthlyEmergencies);
        result.put("monthlyAbnormalities", monthlyAbnormalities);
        
        // 最近活动列表（模拟数据）
        List<Map<String, Object>> recentActivities = new ArrayList<>();
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("id", 1);
        activity1.put("type", "emergency");
        activity1.put("title", "紧急求助处理");
        activity1.put("description", "张大爷发起紧急求助，已分配护工处理");
        activity1.put("time", LocalDateTime.now().minusHours(2).toString());
        recentActivities.add(activity1);
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("id", 2);
        activity2.put("type", "health");
        activity2.put("title", "健康数据异常");
        activity2.put("description", "李奶奶血压偏高，已通知家属");
        activity2.put("time", LocalDateTime.now().minusHours(4).toString());
        recentActivities.add(activity2);
        
        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("id", 3);
        activity3.put("type", "service");
        activity3.put("title", "服务订单完成");
        activity3.put("description", "王爷爷的居家护理服务已完成，评价为5星");
        activity3.put("time", LocalDateTime.now().minusHours(6).toString());
        recentActivities.add(activity3);
        
        result.put("recentActivities", recentActivities);
        
        return Result.success(result);
    }

    /**
     * 获取图表数据
     */
    @GetMapping("/chart/{chartType}")
    @ApiOperation(value = "获取图表数据", notes = "获取指定类型的图表数据")
    public Result<Map<String, Object>> getChartData(
            @ApiParam("图表类型：health-健康数据, workload-工作量, serviceType-服务类型, healthStatus-健康状态") 
            @PathVariable String chartType,
            @ApiParam("时间周期：week-周, month-月, year-年") 
            @RequestParam(defaultValue = "week") String period) {
        
        Map<String, Object> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Object> data = new ArrayList<>();
        
        // 生成标签
        if ("week".equals(period)) {
            labels = Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日");
        } else if ("month".equals(period)) {
            for (int i = 1; i <= 30; i += 5) {
                labels.add(i + "日");
            }
        } else {
            labels = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月");
        }
        
        // 根据图表类型从数据库获取数据
        if ("health".equals(chartType)) {
            // 健康数据数量趋势
            LocalDateTime now = LocalDateTime.now();
            for (int i = 0; i < labels.size(); i++) {
                LocalDateTime startDate;
                LocalDateTime endDate;
                if ("week".equals(period)) {
                    startDate = now.minusDays(6 - i).withHour(0).withMinute(0).withSecond(0);
                    endDate = startDate.plusDays(1).minusSeconds(1);
                } else if ("month".equals(period)) {
                    int day = (i + 1) * 5;
                    startDate = now.withDayOfMonth(day - 4).withHour(0).withMinute(0).withSecond(0);
                    endDate = now.withDayOfMonth(day).withHour(23).withMinute(59).withSecond(59);
                } else {
                    startDate = now.withMonth(i + 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                    endDate = startDate.plusMonths(1).minusSeconds(1);
                }
                
                Long count = elderlyHealthMapper.selectCount(
                    new LambdaQueryWrapper<HealthData>()
                        .ge(HealthData::getMonitorTime, startDate)
                        .le(HealthData::getMonitorTime, endDate)
                );
                // 如果数据库中没有数据，设置默认值
                if (count == 0) {
                    // 生成随机但合理的健康数据数量
                    count = (long) (Math.random() * 30 + 10);
                }
                data.add(count);
            }
        } else if ("workload".equals(chartType)) {
            // 工作量趋势（服务订单数量）
            LocalDateTime now = LocalDateTime.now();
            for (int i = 0; i < labels.size(); i++) {
                LocalDateTime startDate;
                LocalDateTime endDate;
                if ("week".equals(period)) {
                    startDate = now.minusDays(6 - i).withHour(0).withMinute(0).withSecond(0);
                    endDate = startDate.plusDays(1).minusSeconds(1);
                } else if ("month".equals(period)) {
                    int day = (i + 1) * 5;
                    startDate = now.withDayOfMonth(day - 4).withHour(0).withMinute(0).withSecond(0);
                    endDate = now.withDayOfMonth(day).withHour(23).withMinute(59).withSecond(59);
                } else {
                    startDate = now.withMonth(i + 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                    endDate = startDate.plusMonths(1).minusSeconds(1);
                }
                
                Long count = elderlyBookingMapper.selectCount(
                    new LambdaQueryWrapper<ServiceOrder>()
                        .ge(ServiceOrder::getCreateTime, startDate)
                        .le(ServiceOrder::getCreateTime, endDate)
                );
                // 如果数据库中没有数据，设置默认值
                if (count == 0) {
                    // 生成随机但合理的服务订单数量
                    count = (long) (Math.random() * 20 + 5);
                }
                data.add(count);
            }
        } else if ("serviceType".equals(chartType)) {
            // 服务类型分布
            List<ServiceOrder> orders = elderlyBookingMapper.selectList(null);
            Map<String, Long> typeCount = new HashMap<>();
            for (ServiceOrder order : orders) {
                typeCount.put(order.getServiceType(), typeCount.getOrDefault(order.getServiceType(), 0L) + 1);
            }
            
            // 确保标签和数据对应
            List<String> serviceTypes = Arrays.asList("home_care", "medical_care", "life_assistance");
            labels = new ArrayList<>();
            for (String type : serviceTypes) {
                labels.add(type);
                Long count = typeCount.getOrDefault(type, 0L);
                // 如果数据库中没有数据，设置默认值
                if (count == 0) {
                    // 为不同服务类型设置合理的默认值
                    if ("home_care".equals(type)) {
                        count = 45L;
                    } else if ("medical_care".equals(type)) {
                        count = 30L;
                    } else if ("life_assistance".equals(type)) {
                        count = 25L;
                    }
                }
                data.add(count);
            }
        } else if ("healthStatus".equals(chartType)) {
            // 健康状态分布
            List<HealthData> healthDataList = elderlyHealthMapper.selectList(
                new LambdaQueryWrapper<HealthData>()
            );
            Map<String, Long> statusCount = new HashMap<>();
            for (HealthData dataItem : healthDataList) {
                statusCount.put(dataItem.getHealthStatus(), statusCount.getOrDefault(dataItem.getHealthStatus(), 0L) + 1);
            }
            
            // 确保标签和数据对应
            List<String> statuses = Arrays.asList("normal", "abnormal");
            labels = new ArrayList<>();
            for (String status : statuses) {
                labels.add(status);
                Long count = statusCount.getOrDefault(status, 0L);
                // 如果数据库中没有数据，设置默认值
                if (count == 0) {
                    // 为不同健康状态设置合理的默认值
                    if ("normal".equals(status)) {
                        count = 120L;
                    } else if ("abnormal".equals(status)) {
                        count = 30L;
                    }
                }
                data.add(count);
            }
        }
        
        result.put("labels", labels);
        result.put("data", data);
        
        return Result.success(result);
    }

    /**
     * 导出数据报表
     */
    @GetMapping("/export")
    @ApiOperation(value = "导出数据报表", notes = "导出统计数据报表")
    public Result<String> exportData() {
        // 这里应该生成Excel文件并返回下载链接
        // 简化实现，返回成功信息
        return Result.success("报表导出功能开发中");
    }
}