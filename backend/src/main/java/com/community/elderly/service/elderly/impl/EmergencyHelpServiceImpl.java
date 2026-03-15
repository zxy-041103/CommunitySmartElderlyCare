package com.community.elderly.service.elderly.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.elderly.dto.elderly.CreateEmergencyHelpRequest;
import com.community.elderly.dto.elderly.UpdateHelpStatusRequest;
import com.community.elderly.entity.EmergencyHelp;
import com.community.elderly.mapper.elderly.ElderlyEmergencyMapper;
import com.community.elderly.service.elderly.EmergencyHelpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmergencyHelpServiceImpl extends ServiceImpl<ElderlyEmergencyMapper, EmergencyHelp> implements EmergencyHelpService {

    @Override
    @Transactional
    public Long submitEmergencyHelp(Long userId, CreateEmergencyHelpRequest request) {
        EmergencyHelp help = new EmergencyHelp();
        help.setUserId(userId);
        help.setHelpType(request.getHelpType());
        help.setDescription(request.getDescription());
        help.setLocation(request.getLocation());
        help.setHelpStatus("pending");
        help.setCreateTime(LocalDateTime.now());
        help.setUpdateTime(LocalDateTime.now());
        
        save(help);
        
        return help.getId();
    }

    @Override
    @Transactional
    public Map<String, Object> assignCaregiver(Long helpId) {
        EmergencyHelp help = getById(helpId);
        if (help == null) {
            throw new RuntimeException("求助记录不存在");
        }
        
        if (!"pending".equals(help.getHelpStatus())) {
            throw new RuntimeException("求助记录状态不允许分配");
        }
        
        Long caregiverId = 1L;
        help.setHandlerId(caregiverId);
        help.setHelpStatus("handling");
        help.setUpdateTime(LocalDateTime.now());
        updateById(help);
        
        Map<String, Object> result = new HashMap<>();
        result.put("helpId", helpId);
        result.put("caregiverId", caregiverId);
        result.put("status", "handling");
        
        return result;
    }

    @Override
    @Transactional
    public int assignAllPendingHelp() {
        List<EmergencyHelp> pendingHelps = baseMapper.selectByStatus("pending");
        int count = 0;
        
        for (EmergencyHelp help : pendingHelps) {
            try {
                assignCaregiver(help.getId());
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return count;
    }

    @Override
    @Transactional
    public boolean updateHelpStatus(Long helpId, Long handlerId, UpdateHelpStatusRequest request) {
        EmergencyHelp help = getById(helpId);
        if (help == null) {
            throw new RuntimeException("求助记录不存在");
        }
        
        if (!handlerId.equals(help.getHandlerId())) {
            throw new RuntimeException("无权更新此求助记录");
        }
        
        help.setHelpStatus(request.getHelpStatus());
        help.setHandleResult(request.getHandleResult());
        help.setUpdateTime(LocalDateTime.now());
        
        return updateById(help);
    }

    @Override
    public IPage<EmergencyHelp> getHelpRecords(Long userId, Integer page, Integer size) {
        Page<EmergencyHelp> pageParam = new Page<>(page, size);
        return baseMapper.selectPageByUserId(pageParam, userId);
    }

    @Override
    public IPage<EmergencyHelp> getAllHelpRecords(Integer page, Integer size) {
        Page<EmergencyHelp> pageParam = new Page<>(page, size);
        return page(pageParam);
    }

    @Override
    public IPage<EmergencyHelp> getPendingHelpRecords(Integer page, Integer size) {
        Page<EmergencyHelp> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<EmergencyHelp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyHelp::getHelpStatus, "pending");
        return page(pageParam, wrapper);
    }

    @Override
    public IPage<EmergencyHelp> getHelpRecordsByHandler(Long handlerId, Integer page, Integer size) {
        Page<EmergencyHelp> pageParam = new Page<>(page, size);
        return baseMapper.selectPageByHandlerId(pageParam, handlerId);
    }

    @Override
    public EmergencyHelp getHelpDetail(Long helpId) {
        return getById(helpId);
    }

    @Override
    public Map<String, Object> getHelpStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("total", count());
        
        LambdaQueryWrapper<EmergencyHelp> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(EmergencyHelp::getHelpStatus, "pending");
        statistics.put("pending", count(pendingWrapper));
        
        LambdaQueryWrapper<EmergencyHelp> handlingWrapper = new LambdaQueryWrapper<>();
        handlingWrapper.eq(EmergencyHelp::getHelpStatus, "handling");
        statistics.put("handling", count(handlingWrapper));
        
        LambdaQueryWrapper<EmergencyHelp> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(EmergencyHelp::getHelpStatus, "completed");
        statistics.put("completed", count(completedWrapper));
        
        return statistics;
    }

    @Override
    public Map<String, Object> getHelpList(Integer page, Integer size, String name, String type, String status) {
        int offset = (page - 1) * size;
        List<EmergencyHelp> records = baseMapper.selectHelpList(offset, size, name, type, status);
        long total = baseMapper.selectHelpCount(name, type, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("pages", (total + size - 1) / size);
        result.put("current", page);
        
        return result;
    }

    @Override
    public Map<String, Object> getHelpTrend(Integer days) {
        Map<String, Object> trend = new HashMap<>();
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        
        List<EmergencyHelp> helpList = baseMapper.selectHelpByTimeRange(startTime, endTime);
        
        Map<String, Long> dailyCount = new HashMap<>();
        for (int i = 0; i < days; i++) {
            LocalDateTime date = endTime.minusDays(days - i - 1);
            String dateStr = date.toLocalDate().toString();
            dailyCount.put(dateStr, 0L);
        }
        
        for (EmergencyHelp help : helpList) {
            String dateStr = help.getCreateTime().toLocalDate().toString();
            dailyCount.put(dateStr, dailyCount.getOrDefault(dateStr, 0L) + 1);
        }
        
        trend.put("dates", dailyCount.keySet());
        trend.put("counts", dailyCount.values());
        
        return trend;
    }
}
