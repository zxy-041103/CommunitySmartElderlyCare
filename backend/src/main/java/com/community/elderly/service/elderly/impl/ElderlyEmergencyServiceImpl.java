package com.community.elderly.service.elderly.impl;

import com.community.elderly.entity.EmergencyHelp;
import com.community.elderly.mapper.elderly.ElderlyEmergencyMapper;
import com.community.elderly.service.elderly.ElderlyEmergencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElderlyEmergencyServiceImpl implements ElderlyEmergencyService {

    @Autowired
    private ElderlyEmergencyMapper elderlyEmergencyMapper;

    @Override
    public Map<String, Object> getEmergencyList(Long elderlyId, Integer pageNum, Integer pageSize,
                                                 String type, String status, String startTime, String endTime) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<EmergencyHelp> emergencyList = elderlyEmergencyMapper.selectByUserId(elderlyId);
            
            // 按类型过滤
            if (type != null && !type.isEmpty()) {
                emergencyList = emergencyList.stream()
                    .filter(e -> type.equals(e.getHelpType()))
                    .collect(Collectors.toList());
            }
            
            // 按状态过滤
            if (status != null && !status.isEmpty()) {
                emergencyList = emergencyList.stream()
                    .filter(e -> status.equals(e.getHelpStatus()))
                    .collect(Collectors.toList());
            }
            
            // 按时间范围过滤
            if (startTime != null && !startTime.isEmpty()) {
                final LocalDateTime start = LocalDateTime.parse(startTime + "T00:00:00");
                emergencyList = emergencyList.stream()
                    .filter(e -> e.getCreateTime().isAfter(start) || e.getCreateTime().isEqual(start))
                    .collect(Collectors.toList());
            }
            
            // 如果没有传入结束时间，默认设置为当前时间，避免查询未来数据
            final LocalDateTime end;
            if (endTime != null && !endTime.isEmpty()) {
                end = LocalDateTime.parse(endTime + "T23:59:59");
            } else {
                end = LocalDateTime.now();
            }
            
            if (end != null) {
                emergencyList = emergencyList.stream()
                    .filter(e -> e.getCreateTime().isBefore(end) || e.getCreateTime().isEqual(end))
                    .collect(Collectors.toList());
            }
            
            // 按请求时间倒序排序
            emergencyList.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
            
            // 分页
            int total = emergencyList.size();
            int fromIndex = (pageNum - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, total);
            
            List<EmergencyHelp> pageList = emergencyList.subList(fromIndex, toIndex);
            
            result.put("list", pageList);
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            
        } catch (Exception e) {
            log.error("获取紧急求助列表失败", e);
            throw new RuntimeException("获取紧急求助列表失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public void createEmergency(Long elderlyId, String type, String description) {
        try {
            EmergencyHelp emergency = new EmergencyHelp();
            emergency.setUserId(elderlyId);
            emergency.setHelpType(type);
            emergency.setDescription(description);
            emergency.setHelpStatus("pending");
            emergency.setUrgency("high");
            emergency.setIsProcessed(0);
            emergency.setCreateTime(LocalDateTime.now());
            emergency.setUpdateTime(LocalDateTime.now());
            
            elderlyEmergencyMapper.insert(emergency);
            
            log.info("老人 {} 创建紧急求助成功，类型: {}", elderlyId, type);
        } catch (Exception e) {
            log.error("创建紧急求助失败", e);
            throw new RuntimeException("创建紧急求助失败: " + e.getMessage());
        }
    }

    @Override
    public void cancelEmergency(Long elderlyId, Long emergencyId) {
        try {
            EmergencyHelp emergency = elderlyEmergencyMapper.selectById(emergencyId);
            
            if (emergency == null) {
                throw new RuntimeException("紧急求助记录不存在");
            }
            
            if (!emergency.getUserId().equals(elderlyId)) {
                throw new RuntimeException("无权取消此求助记录");
            }
            
            if (!"pending".equals(emergency.getHelpStatus())) {
                throw new RuntimeException("只能取消待处理的求助记录");
            }
            
            emergency.setHelpStatus("cancelled");
            emergency.setUpdateTime(LocalDateTime.now());
            
            elderlyEmergencyMapper.updateById(emergency);
            
            log.info("老人 {} 取消紧急求助成功，ID: {}", elderlyId, emergencyId);
        } catch (Exception e) {
            log.error("取消紧急求助失败", e);
            throw new RuntimeException("取消紧急求助失败: " + e.getMessage());
        }
    }
}
