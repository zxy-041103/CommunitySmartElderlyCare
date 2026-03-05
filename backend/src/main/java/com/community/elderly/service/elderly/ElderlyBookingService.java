package com.community.elderly.service.elderly;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.elderly.dto.elderly.CreateServiceOrderRequest;
import com.community.elderly.dto.caregiver.SubmitEvaluationRequest;
import com.community.elderly.dto.caregiver.VerifyServiceRequest;
import com.community.elderly.entity.ServiceOrder;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface ElderlyBookingService extends IService<ServiceOrder> {

    /**
     * 创建服务预约订单
     * 事务要求：创建订单和生成核销码必须在同一事务中完成，确保数据一致性
     *
     * @param userId 用户ID（老人）
     * @param request 创建订单请求
     * @return 创建的订单信息，包含核销码
     */
    Map<String, Object> createServiceOrder(Long userId, CreateServiceOrderRequest request);

    /**
     * 查询预约进度
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页订单列表
     */
    IPage<ServiceOrder> getServiceOrderProgress(Long userId, Integer page, Integer size);

    /**
     * 查询所有订单（管理员/护工用）
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页订单列表
     */
    IPage<ServiceOrder> getAllOrders(Integer page, Integer size);

    /**
     * 查询待处理的订单（护工用）
     *
     * @param caregiverId 护工ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页订单列表
     */
    IPage<ServiceOrder> getPendingOrdersForCaregiver(Long caregiverId, Integer page, Integer size);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    ServiceOrder getOrderDetail(Long orderId);

    /**
     * 取消订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelOrder(Long userId, Long orderId);

    /**
     * 服务核销（扫码验证）
     * 事务要求：更新订单状态和创建核销记录必须在同一事务中完成
     *
     * @param caregiverId 护工ID
     * @param request 核销请求
     * @return 核销结果
     */
    Map<String, Object> verifyService(Long caregiverId, VerifyServiceRequest request);

    /**
     * 提交服务评价
     * 事务要求：更新订单评价状态和保存评价记录必须在同一事务中完成
     *
     * @param userId 用户ID（老人）
     * @param request 评价请求
     * @return 是否成功
     */
    boolean submitEvaluation(Long userId, SubmitEvaluationRequest request);

    /**
     * 根据核销码查询订单
     *
     * @param verificationCode 核销码
     * @return 订单信息
     */
    ServiceOrder getOrderByVerificationCode(String verificationCode);
}
