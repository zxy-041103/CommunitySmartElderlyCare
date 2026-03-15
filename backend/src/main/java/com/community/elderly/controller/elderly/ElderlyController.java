package com.community.elderly.controller.elderly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.elderly.dto.elderly.CreateServiceOrderRequest;
import com.community.elderly.dto.elderly.FamilyMemberDTO;
import com.community.elderly.dto.elderly.HealthDataInputRequest;
import com.community.elderly.dto.elderly.HealthDataQueryRequest;
import com.community.elderly.entity.Announcement;
import com.community.elderly.entity.HealthData;
import com.community.elderly.entity.ServiceOrder;
import com.community.elderly.service.elderly.ElderlyHomeService;
import com.community.elderly.service.elderly.ElderlyBookingService;
import com.community.elderly.service.elderly.ElderlyHealthService;
import com.community.elderly.service.elderly.ElderlyEmergencyService;
import com.community.elderly.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/elderly")
@Api(tags = "老人端接口")
public class ElderlyController {

    @Autowired
    private ElderlyHomeService elderlyHomeService;

    @Autowired
    private ElderlyBookingService elderlyBookingService;

    @GetMapping("/announcements")
    @ApiOperation(value = "获取社区公告", notes = "老人获取社区公告列表")
    public Result<List<Announcement>> getAnnouncements() {
        try {
            List<Announcement> announcements = elderlyHomeService.getAnnouncements();
            return Result.success(announcements);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/family-members")
    @ApiOperation(value = "获取家属信息", notes = "老人获取已审核通过的家属信息")
    public Result<List<FamilyMemberDTO>> getFamilyMembers(Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            List<FamilyMemberDTO> familyMembers = elderlyHomeService.getFamilyMembers(elderlyId);
            return Result.success(familyMembers);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/health-overview")
    @ApiOperation(value = "获取健康概览", notes = "老人获取最新的健康数据概览")
    public Result<Map<String, Object>> getHealthOverview(Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            Map<String, Object> overview = elderlyHomeService.getHealthOverview(elderlyId);
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/health-trend")
    @ApiOperation(value = "获取健康趋势", notes = "老人获取健康数据趋势")
    public Result<Map<String, Object>> getHealthTrend(
            @RequestParam(defaultValue = "7") Integer days,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            Map<String, Object> trend = elderlyHomeService.getHealthTrend(elderlyId, days);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/service-order")
    @ApiOperation(value = "创建服务预约", notes = "老人创建服务预约")
    public Result<Map<String, Object>> createServiceOrder(
            @Valid @RequestBody CreateServiceOrderRequest request,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            Map<String, Object> result = elderlyBookingService.createServiceOrder(elderlyId, request);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/service-orders")
    @ApiOperation(value = "获取服务预约记录", notes = "老人获取自己的服务预约记录")
    public Result<IPage<ServiceOrder>> getServiceOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            IPage<ServiceOrder> orders = elderlyBookingService.getServiceOrderProgress(elderlyId, page, size);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private ElderlyHealthService elderlyHealthService;

    @GetMapping("/health-data")
    @ApiOperation(value = "查询健康数据", notes = "老人查询自己的健康数据")
    public Result<Map<String, Object>> queryHealthData(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String healthStatus,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            Map<String, Object> result = elderlyHealthService.queryHealthData(
                elderlyId, pageNum, pageSize, healthStatus, startTime, endTime
            );
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/health-data")
    @ApiOperation(value = "输入健康数据", notes = "老人输入自己的健康数据")
    public Result<HealthData> inputHealthData(
            @Valid @RequestBody HealthDataInputRequest request,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            HealthData healthData = elderlyHealthService.inputHealthData(elderlyId, request);
            return Result.success(healthData);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/health-data/{id}")
    @ApiOperation(value = "删除健康数据", notes = "老人删除自己的健康数据")
    public Result<Void> deleteHealthData(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            elderlyHealthService.deleteHealthData(elderlyId, id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private ElderlyEmergencyService elderlyEmergencyService;

    @GetMapping("/emergency")
    @ApiOperation(value = "查询紧急求助记录", notes = "老人查询自己的紧急求助记录")
    public Result<Map<String, Object>> getEmergencyList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            Map<String, Object> result = elderlyEmergencyService.getEmergencyList(
                elderlyId, pageNum, pageSize, type, status, startTime, endTime
            );
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/emergency")
    @ApiOperation(value = "创建紧急求助", notes = "老人创建紧急求助")
    public Result<Void> createEmergency(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            elderlyEmergencyService.createEmergency(elderlyId, request.get("type"), request.get("description"));
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/emergency/{id}/cancel")
    @ApiOperation(value = "取消紧急求助", notes = "老人取消自己的紧急求助")
    public Result<Void> cancelEmergency(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            elderlyEmergencyService.cancelEmergency(elderlyId, id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}