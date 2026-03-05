package com.community.elderly.service.elderly.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.elderly.dto.elderly.CreateServiceOrderRequest;
import com.community.elderly.dto.caregiver.SubmitEvaluationRequest;
import com.community.elderly.dto.caregiver.VerifyServiceRequest;
import com.community.elderly.entity.ServiceEvaluation;
import com.community.elderly.entity.ServiceOrder;
import com.community.elderly.entity.ServiceVerification;
import com.community.elderly.entity.User;
import com.community.elderly.mapper.caregiver.ServiceEvaluationMapper;
import com.community.elderly.mapper.elderly.ElderlyBookingMapper;
import com.community.elderly.mapper.caregiver.ServiceVerificationMapper;
import com.community.elderly.mapper.elderly.ElderlyProfileMapper;
import com.community.elderly.service.elderly.ElderlyBookingService;
import com.community.elderly.utils.VerificationCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ServiceOrderServiceImpl extends ServiceImpl<ElderlyBookingMapper, ServiceOrder> implements ElderlyBookingService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ElderlyBookingMapper serviceOrderMapper;

    @Autowired
    private ServiceVerificationMapper serviceVerificationMapper;

    @Autowired
    private ServiceEvaluationMapper serviceEvaluationMapper;

    @Autowired
    private ElderlyProfileMapper userMapper;

    @Autowired
    private VerificationCodeGenerator verificationCodeGenerator;

    /**
     * 创建服务预约订单
     * 事务要求：创建订单和生成核销码必须在同一事务中完成，确保数据一致性
     *
     * @param userId 用户ID（老人）
     * @param request 创建订单请求
     * @return 创建的订单信息，包含核销码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createServiceOrder(Long userId, CreateServiceOrderRequest request) {
        log.info("创建服务预约订单，用户ID：{}，服务类型：{}", userId, request.getServiceType());

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!"elderly".equals(user.getRoleType())) {
            throw new RuntimeException("只有老人用户可以预约服务");
        }

        ServiceOrder order = new ServiceOrder();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setServiceType(request.getServiceType());
        order.setServiceName(request.getServiceName());
        order.setServiceDate(request.getServiceDate());
        order.setServiceTime(request.getServiceTime());
        order.setServiceAddress(request.getServiceAddress());
        order.setServiceDescription(request.getServiceDescription());
        order.setOrderStatus("pending");
        order.setIsPaid(0);

        serviceOrderMapper.insert(order);
        log.info("订单创建成功，订单ID：{}，订单号：{}", order.getId(), order.getOrderNo());

        String verificationCode = verificationCodeGenerator.generateVerificationCode(order.getId());
        ServiceVerification verification = new ServiceVerification();
        verification.setOrderId(order.getId());
        verification.setVerificationCode(verificationCode);
        verification.setVerificationStatus("pending");
        serviceVerificationMapper.insert(verification);
        log.info("核销码生成成功，核销码：{}，关联订单ID：{}", verificationCode, order.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("serviceType", order.getServiceType());
        result.put("serviceName", order.getServiceName());
        result.put("serviceDate", order.getServiceDate());
        result.put("serviceTime", order.getServiceTime());
        result.put("orderStatus", order.getOrderStatus());
        result.put("verificationCode", verificationCode);
        result.put("createTime", order.getCreateTime().format(DATE_TIME_FORMATTER));

        return result;
    }

    /**
     * 生成订单号
     * 格式：SV + 时间戳 + 随机数
     */
    private String generateOrderNo() {
        return "SV" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 查询预约进度
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页订单列表
     */
    @Override
    public IPage<ServiceOrder> getServiceOrderProgress(Long userId, Integer page, Integer size) {
        Page<ServiceOrder> pageParam = new Page<>(page, size);
        return serviceOrderMapper.selectPageByUserId(pageParam, userId);
    }

    /**
     * 查询所有订单（管理员/护工用）
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页订单列表
     */
    @Override
    public IPage<ServiceOrder> getAllOrders(Integer page, Integer size) {
        Page<ServiceOrder> pageParam = new Page<>(page, size);
        return serviceOrderMapper.selectPage(pageParam, null);
    }

    /**
     * 查询待处理的订单（护工用）
     *
     * @param caregiverId 护工ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页订单列表
     */
    @Override
    public IPage<ServiceOrder> getPendingOrdersForCaregiver(Long caregiverId, Integer page, Integer size) {
        Page<ServiceOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ServiceOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceOrder::getCaregiverId, caregiverId)
                   .eq(ServiceOrder::getOrderStatus, "in_progress")
                   .orderByDesc(ServiceOrder::getServiceDate);
        return serviceOrderMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Override
    public ServiceOrder getOrderDetail(Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order != null) {
            ServiceVerification verification = serviceVerificationMapper.selectByOrderId(orderId);
            if (verification != null) {
                // 不设置remark，因为ServiceOrder类没有这个字段
                // 使用serviceDescription字段来存储核销码信息
                order.setServiceDescription("核销码：" + verification.getVerificationCode() + "，状态：" + verification.getVerificationStatus());
            }
        }
        return order;
    }

    /**
     * 取消订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long userId, Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (!"pending".equals(order.getOrderStatus())) {
            throw new RuntimeException("当前状态不能取消订单");
        }

        order.setOrderStatus("cancelled");
        return serviceOrderMapper.updateById(order) > 0;
    }

    /**
     * 服务核销（扫码验证）
     * 事务要求：更新订单状态和创建核销记录必须在同一事务中完成
     *
     * @param caregiverId 护工ID
     * @param request 核销请求
     * @return 核销结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> verifyService(Long caregiverId, VerifyServiceRequest request) {
        log.info("服务核销，护工ID：{}，核销码：{}", caregiverId, request.getVerificationCode());

        ServiceVerification verification = serviceVerificationMapper.selectByVerificationCode(request.getVerificationCode());
        if (verification == null) {
            throw new RuntimeException("核销码无效");
        }

        if ("verified".equals(verification.getVerificationStatus())) {
            throw new RuntimeException("该核销码已被使用");
        }

        ServiceOrder order = serviceOrderMapper.selectById(verification.getOrderId());
        if (order == null) {
            throw new RuntimeException("关联订单不存在");
        }

        if (!"confirmed".equals(order.getOrderStatus()) && !"in_progress".equals(order.getOrderStatus())) {
            throw new RuntimeException("当前订单状态不能进行核销");
        }

        User caregiver = userMapper.selectById(caregiverId);
        if (caregiver == null || !"caregiver".equals(caregiver.getRoleType())) {
            throw new RuntimeException("护工不存在或角色不正确");
        }

        verification.setVerifierId(caregiverId);
        verification.setVerificationStatus("verified");
        verification.setVerificationTime(LocalDateTime.now());
        verification.setServiceContent(request.getServiceContent());
        verification.setElderlyFeedback(request.getElderlyFeedback());
        serviceVerificationMapper.updateById(verification);
        log.info("核销记录更新成功，核销ID：{}", verification.getId());

        order.setOrderStatus("completed");
        order.setCaregiverId(caregiverId);
        serviceOrderMapper.updateById(order);
        log.info("订单状态更新成功，订单ID：{}", order.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("verificationId", verification.getId());
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("serviceType", order.getServiceType());
        result.put("serviceName", order.getServiceName());
        result.put("serviceDate", order.getServiceDate());
        result.put("serviceTime", order.getServiceTime());
        result.put("verificationTime", verification.getVerificationTime().format(DATE_TIME_FORMATTER));
        result.put("elderlyName", userMapper.selectById(order.getUserId()).getRealName());

        return result;
    }

    /**
     * 提交服务评价
     * 事务要求：更新订单评价状态和保存评价记录必须在同一事务中完成
     *
     * @param userId 用户ID（老人）
     * @param request 评价请求
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitEvaluation(Long userId, SubmitEvaluationRequest request) {
        log.info("提交服务评价，用户ID：{}，订单ID：{}", userId, request.getOrderId());

        ServiceOrder order = serviceOrderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权评价此订单");
        }

        if (!"completed".equals(order.getOrderStatus())) {
            throw new RuntimeException("只能在服务完成后评价");
        }

        ServiceEvaluation existingEvaluation = serviceEvaluationMapper.selectByOrderId(request.getOrderId());
        if (existingEvaluation != null) {
            throw new RuntimeException("该订单已评价");
        }

        ServiceEvaluation evaluation = new ServiceEvaluation();
        evaluation.setOrderId(request.getOrderId());
        evaluation.setUserId(userId);
        evaluation.setCaregiverId(order.getCaregiverId());
        evaluation.setRating(request.getRating());
        evaluation.setEvaluation(request.getEvaluation());
        serviceEvaluationMapper.insert(evaluation);
        log.info("评价记录创建成功，评价ID：{}", evaluation.getId());

        order.setOrderStatus("evaluated");
        serviceOrderMapper.updateById(order);
        log.info("订单评价状态更新成功，订单ID：{}", order.getId());

        return true;
    }

    /**
     * 根据核销码查询订单
     *
     * @param verificationCode 核销码
     * @return 订单信息
     */
    @Override
    public ServiceOrder getOrderByVerificationCode(String verificationCode) {
        ServiceVerification verification = serviceVerificationMapper.selectByVerificationCode(verificationCode);
        if (verification != null) {
            return serviceOrderMapper.selectById(verification.getOrderId());
        }
        return null;
    }
}
