package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.Result;
import com.eldercare.entity.*;
import com.eldercare.mapper.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private HealthAlertMapper healthAlertMapper;
    @Resource
    private EmergencyRecordMapper emergencyRecordMapper;
    @Resource
    private ServiceAppointmentMapper appointmentMapper;
    @Resource
    private HealthDataMapper healthDataMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private FeedbackMapper feedbackMapper;

    @GetMapping("/stats")
    public Result<?> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "USER")));
        data.put("totalCaregivers", userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "CAREGIVER")));
        data.put("totalCommunity", userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "COMMUNITY")));
        data.put("pendingAlerts", healthAlertMapper.selectCount(new LambdaQueryWrapper<HealthAlert>().eq(HealthAlert::getStatus, "PENDING")));
        data.put("pendingEmergencies", emergencyRecordMapper.selectCount(new LambdaQueryWrapper<EmergencyRecord>().eq(EmergencyRecord::getStatus, "PENDING")));
        data.put("pendingAppointments", appointmentMapper.selectCount(new LambdaQueryWrapper<ServiceAppointment>().eq(ServiceAppointment::getStatus, "PENDING")));
        data.put("completedAppointments", appointmentMapper.selectCount(new LambdaQueryWrapper<ServiceAppointment>().eq(ServiceAppointment::getStatus, "COMPLETED")));
        data.put("totalActivities", activityMapper.selectCount(null));
        data.put("pendingFeedback", feedbackMapper.selectCount(new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, "PENDING")));
        // 预约状态分布
        Map<String, Long> appointmentStats = new LinkedHashMap<>();
        appointmentStats.put("待确认", appointmentMapper.selectCount(new LambdaQueryWrapper<ServiceAppointment>().eq(ServiceAppointment::getStatus, "PENDING")));
        appointmentStats.put("已确认", appointmentMapper.selectCount(new LambdaQueryWrapper<ServiceAppointment>().eq(ServiceAppointment::getStatus, "CONFIRMED")));
        appointmentStats.put("进行中", appointmentMapper.selectCount(new LambdaQueryWrapper<ServiceAppointment>().eq(ServiceAppointment::getStatus, "IN_PROGRESS")));
        appointmentStats.put("已完成", appointmentMapper.selectCount(new LambdaQueryWrapper<ServiceAppointment>().eq(ServiceAppointment::getStatus, "COMPLETED")));
        appointmentStats.put("已取消", appointmentMapper.selectCount(new LambdaQueryWrapper<ServiceAppointment>().eq(ServiceAppointment::getStatus, "CANCELLED")));
        data.put("appointmentStats", appointmentStats);
        // 预警级别分布
        Map<String, Long> alertStats = new LinkedHashMap<>();
        alertStats.put("提示", healthAlertMapper.selectCount(new LambdaQueryWrapper<HealthAlert>().eq(HealthAlert::getAlertLevel, "INFO")));
        alertStats.put("警告", healthAlertMapper.selectCount(new LambdaQueryWrapper<HealthAlert>().eq(HealthAlert::getAlertLevel, "WARNING")));
        alertStats.put("危险", healthAlertMapper.selectCount(new LambdaQueryWrapper<HealthAlert>().eq(HealthAlert::getAlertLevel, "DANGER")));
        data.put("alertStats", alertStats);
        return Result.success(data);
    }
}
