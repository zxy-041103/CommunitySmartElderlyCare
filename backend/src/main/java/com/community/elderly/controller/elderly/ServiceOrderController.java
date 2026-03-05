package com.community.elderly.controller.elderly;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.elderly.dto.elderly.CreateServiceOrderRequest;
import com.community.elderly.dto.caregiver.SubmitEvaluationRequest;
import com.community.elderly.dto.caregiver.VerifyServiceRequest;
import com.community.elderly.entity.ServiceOrder;
import com.community.elderly.service.elderly.ElderlyBookingService;
import com.community.elderly.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/service-order")
@Api(tags = "服务订单接口")
public class ServiceOrderController {

    @Autowired
    private ElderlyBookingService elderlyBookingService;

    /**
     * 创建服务预约
     * 服务预约接口：创建预约
     *
     * @param request 创建订单请求
     * @param authentication 认证信息
     * @return 创建结果
     */
    @PostMapping
    @ApiOperation(value = "创建服务预约", notes = "老人用户创建服务预约，返回订单信息和核销码")
    public Result<Map<String, Object>> createServiceOrder(
            @Valid @RequestBody CreateServiceOrderRequest request,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            Map<String, Object> result = elderlyBookingService.createServiceOrder(userId, request);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询预约进度
     * 服务预约接口：预约进度查询
     *
     * @param page 页码
     * @param size 每页大小
     * @param authentication 认证信息
     * @return 预约订单列表
     */
    @GetMapping("/progress")
    @ApiOperation(value = "查询预约进度", notes = "查询用户的服务预约进度，分页返回")
    public Result<IPage<ServiceOrder>> getServiceOrderProgress(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            IPage<ServiceOrder> orders = elderlyBookingService.getServiceOrderProgress(userId, page, size);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 服务核销（扫码验证）
     * 服务预约接口：服务核销（扫码验证）
     *
     * @param request 核销请求
     * @param authentication 认证信息
     * @return 核销结果
     */
    @PostMapping("/verify")
    @ApiOperation(value = "服务核销", notes = "护工通过核销码验证服务完成情况")
    public Result<Map<String, Object>> verifyService(
            @Valid @RequestBody VerifyServiceRequest request,
            Authentication authentication) {
        try {
            Long caregiverId = Long.parseLong(authentication.getName());
            Map<String, Object> result = elderlyBookingService.verifyService(caregiverId, request);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 服务评价提交
     * 服务预约接口：服务评价提交
     *
     * @param request 评价请求
     * @param authentication 认证信息
     * @return 评价结果
     */
    @PostMapping("/evaluation")
    @ApiOperation(value = "提交服务评价", notes = "老人用户对已完成的服务进行评价")
    public Result<Boolean> submitEvaluation(
            @Valid @RequestBody SubmitEvaluationRequest request,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            boolean result = elderlyBookingService.submitEvaluation(userId, request);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    @ApiOperation(value = "获取订单详情", notes = "获取指定订单的详细信息")
    public Result<ServiceOrder> getOrderDetail(
            @ApiParam(value = "订单ID", required = true) @PathVariable Long orderId) {
        try {
            ServiceOrder order = elderlyBookingService.getOrderDetail(orderId);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param authentication 认证信息
     * @return 取消结果
     */
    @PutMapping("/{orderId}/cancel")
    @ApiOperation(value = "取消订单", notes = "老人用户取消待确认的服务订单")
    public Result<Boolean> cancelOrder(
            @ApiParam(value = "订单ID", required = true) @PathVariable Long orderId,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            boolean result = elderlyBookingService.cancelOrder(userId, orderId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询所有订单（管理员用）
     *
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    @GetMapping("/admin/all")
    @ApiOperation(value = "查询所有订单", notes = "管理员查询所有服务订单")
    public Result<IPage<ServiceOrder>> getAllOrders(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size) {
        try {
            IPage<ServiceOrder> orders = elderlyBookingService.getAllOrders(page, size);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询护工待处理订单
     *
     * @param page 页码
     * @param size 每页大小
     * @param authentication 认证信息
     * @return 订单列表
     */
    @GetMapping("/caregiver/pending")
    @ApiOperation(value = "查询待处理订单", notes = "护工查询待处理的服务订单")
    public Result<IPage<ServiceOrder>> getPendingOrders(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        try {
            Long caregiverId = Long.parseLong(authentication.getName());
            IPage<ServiceOrder> orders = elderlyBookingService.getPendingOrdersForCaregiver(caregiverId, page, size);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
