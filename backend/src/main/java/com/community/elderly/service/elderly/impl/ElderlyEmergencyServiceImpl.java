package com.community.elderly.service.elderly.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.elderly.dto.elderly.CreateEmergencyHelpRequest;
import com.community.elderly.dto.elderly.UpdateHelpStatusRequest;
import com.community.elderly.entity.EmergencyHelp;
import com.community.elderly.entity.User;

import com.community.elderly.mapper.elderly.ElderlyEmergencyMapper;

import com.community.elderly.mapper.elderly.ElderlyProfileMapper;
import com.community.elderly.service.elderly.ElderlyEmergencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

import java.util.*;


@Slf4j
@Service
public class ElderlyEmergencyServiceImpl extends ServiceImpl<ElderlyEmergencyMapper, EmergencyHelp> implements ElderlyEmergencyService {



    @Autowired
    private ElderlyEmergencyMapper elderlyEmergencyMapper;

    @Autowired
    private ElderlyProfileMapper elderlyProfileMapper;

    

    

    

    

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitEmergencyHelp(Long userId, CreateEmergencyHelpRequest request) {
        log.info("提交紧急求助，用户ID：{}，求助类型：{}", userId, request.getHelpType());

        User user = elderlyProfileMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!"elderly".equals(user.getRoleType())) {
            throw new RuntimeException("只有老人用户可以提交求助");
        }

        EmergencyHelp help = new EmergencyHelp();
        help.setUserId(userId);
        help.setHelpType(request.getHelpType());
        help.setUrgency(request.getUrgency() != null ? request.getUrgency() : "high");
        help.setDescription(request.getDescription());
        help.setPhone(request.getPhone());
        help.setLocation(request.getLocation());
        help.setHelpStatus("pending");
        help.setIsProcessed(0);

        elderlyEmergencyMapper.insert(help);
        log.info("求助记录创建成功，求助ID：{}", help.getId());

        sendEmergencyNotification(help, user);
        log.info("紧急求助通知发送成功，求助ID：{}", help.getId());

        return help.getId();
    }

    private void sendEmergencyNotification(EmergencyHelp help, User user) {
        // 发送通知逻辑
    }

    @Override
    public boolean assignCaregiver(Long helpId, Long caregiverId) {
        EmergencyHelp help = elderlyEmergencyMapper.selectById(helpId);
        if (help == null) {
            throw new RuntimeException("求助记录不存在");
        }
        help.setHandlerId(caregiverId);
        help.setHelpStatus("assigned");
        return elderlyEmergencyMapper.updateById(help) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHelpStatus(Long helpId, UpdateHelpStatusRequest request) {
        EmergencyHelp help = elderlyEmergencyMapper.selectById(helpId);
        if (help == null) {
            throw new RuntimeException("求助记录不存在");
        }

        help.setHelpStatus(request.getHelpStatus());
        help.setHandleResult(request.getHandleResult());
        help.setHandleTime(LocalDateTime.now());

        return elderlyEmergencyMapper.updateById(help) > 0;
    }

    @Override
    public IPage<EmergencyHelp> getHelpRecordsByUserId(Long userId, Integer page, Integer size) {
        Page<EmergencyHelp> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<EmergencyHelp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyHelp::getUserId, userId);
        wrapper.orderByDesc(EmergencyHelp::getCreateTime);
        return elderlyEmergencyMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public EmergencyHelp getHelpDetail(Long helpId) {
        return elderlyEmergencyMapper.selectById(helpId);
    }

    @Override
    public IPage<EmergencyHelp> getPendingHelpsForCaregiver(Long caregiverId, Integer page, Integer size) {
        Page<EmergencyHelp> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<EmergencyHelp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyHelp::getHandlerId, caregiverId);
        wrapper.eq(EmergencyHelp::getHelpStatus, "pending");
        wrapper.orderByDesc(EmergencyHelp::getCreateTime);
        return elderlyEmergencyMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public IPage<EmergencyHelp> getAllHelpRecords(Integer page, Integer size, String status) {
        Page<EmergencyHelp> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<EmergencyHelp> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(EmergencyHelp::getHelpStatus, status);
        }
        wrapper.orderByDesc(EmergencyHelp::getCreateTime);
        return elderlyEmergencyMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Map<String, Object> getEmergencyHelpStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计总求助数
        long total = elderlyEmergencyMapper.selectCount(null);
        statistics.put("total", total);
        
        // 统计待处理求助数
        long pending = elderlyEmergencyMapper.selectCount(new LambdaQueryWrapper<EmergencyHelp>()
                .eq(EmergencyHelp::getHelpStatus, "pending"));
        statistics.put("pending", pending);
        
        // 统计已处理求助数
        long processed = elderlyEmergencyMapper.selectCount(new LambdaQueryWrapper<EmergencyHelp>()
                .eq(EmergencyHelp::getIsProcessed, 1));
        statistics.put("processed", processed);
        
        return statistics;
    }

    @Override
    public List<EmergencyHelp> getRecentHelpRecords(int limit) {
        LambdaQueryWrapper<EmergencyHelp> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(EmergencyHelp::getCreateTime);
        wrapper.last("LIMIT " + limit);
        return elderlyEmergencyMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelHelp(Long helpId, Long userId) {
        EmergencyHelp help = elderlyEmergencyMapper.selectById(helpId);
        if (help == null) {
            throw new RuntimeException("求助记录不存在");
        }
        
        if (!help.getUserId().equals(userId)) {
            throw new RuntimeException("只能取消自己的求助");
        }
        
        help.setHelpStatus("cancelled");
        help.setIsProcessed(1);
        return elderlyEmergencyMapper.updateById(help) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeHelp(Long helpId, Long caregiverId, String notes) {
        EmergencyHelp help = elderlyEmergencyMapper.selectById(helpId);
        if (help == null) {
            throw new RuntimeException("求助记录不存在");
        }
        
        if (!help.getHandlerId().equals(caregiverId)) {
            throw new RuntimeException("只能完成分配给自己的求助");
        }
        
        help.setHelpStatus("completed");
        help.setHandleResult(notes);
        help.setIsProcessed(1);
        help.setHandleTime(LocalDateTime.now());
        return elderlyEmergencyMapper.updateById(help) > 0;
    }
}
