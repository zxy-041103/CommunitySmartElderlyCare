package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.EmergencyRecord;
import com.eldercare.entity.User;
import com.eldercare.mapper.EmergencyRecordMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyRecordController {

    @Resource
    private EmergencyRecordMapper emergencyRecordMapper;
    @Resource
    private UserMapper userMapper;

    private void fillNames(EmergencyRecord record) {
        if (record.getElderlyId() != null) {
            User u = userMapper.selectById(record.getElderlyId());
            if (u != null) {
                record.setElderlyName(u.getName());
                record.setElderlyPhone(u.getPhone());
            }
        }
        if (record.getResponderId() != null) {
            User u = userMapper.selectById(record.getResponderId());
            if (u != null) record.setResponderName(u.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String status) {
        LambdaQueryWrapper<EmergencyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null && !status.isEmpty(), EmergencyRecord::getStatus, status);
        wrapper.orderByAsc(EmergencyRecord::getId);
        Page<EmergencyRecord> page = emergencyRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillNames);
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result<?> myRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<EmergencyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyRecord::getElderlyId, userId);
        wrapper.orderByAsc(EmergencyRecord::getId);
        java.util.List<EmergencyRecord> list = emergencyRecordMapper.selectList(wrapper);
        list.forEach(this::fillNames);
        return Result.success(list);
    }

    @GetMapping("/pending")
    public Result<?> pending() {
        LambdaQueryWrapper<EmergencyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EmergencyRecord::getStatus, "PENDING", "PROCESSING");
        wrapper.orderByAsc(EmergencyRecord::getId);
        java.util.List<EmergencyRecord> list = emergencyRecordMapper.selectList(wrapper);
        list.forEach(this::fillNames);
        return Result.success(list);
    }

    @GetMapping("/pending/count")
    public Result<?> pendingCount() {
        long count = emergencyRecordMapper.selectCount(
                new LambdaQueryWrapper<EmergencyRecord>().eq(EmergencyRecord::getStatus, "PENDING"));
        return Result.success(count);
    }

    @PostMapping
    public Result<?> add(@RequestBody EmergencyRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setElderlyId(userId);
        record.setStatus("PENDING");
        emergencyRecordMapper.insert(record);
        return Result.success("求助已发送");
    }

    @PutMapping("/respond/{id}")
    public Result<?> respond(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        EmergencyRecord record = emergencyRecordMapper.selectById(id);
        record.setStatus("PROCESSING");
        record.setResponderId(userId);
        record.setResponseTime(LocalDateTime.now());
        emergencyRecordMapper.updateById(record);
        return Result.success("已响应");
    }

    @PutMapping("/resolve/{id}")
    public Result<?> resolve(@PathVariable Long id, @RequestBody EmergencyRecord record) {
        EmergencyRecord existing = emergencyRecordMapper.selectById(id);
        existing.setStatus("RESOLVED");
        existing.setResolveTime(LocalDateTime.now());
        existing.setResolveNote(record.getResolveNote());
        emergencyRecordMapper.updateById(existing);
        return Result.success("已解决");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        emergencyRecordMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
