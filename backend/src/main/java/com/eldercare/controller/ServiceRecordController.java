package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.ServiceRecord;
import com.eldercare.entity.User;
import com.eldercare.mapper.ServiceRecordMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/serviceRecord")
public class ServiceRecordController {

    @Resource
    private ServiceRecordMapper serviceRecordMapper;
    @Resource
    private UserMapper userMapper;

    private void fillNames(ServiceRecord record) {
        if (record.getCaregiverId() != null) {
            User u = userMapper.selectById(record.getCaregiverId());
            if (u != null) record.setCaregiverName(u.getName());
        }
        if (record.getElderlyId() != null) {
            User u = userMapper.selectById(record.getElderlyId());
            if (u != null) record.setElderlyName(u.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long caregiverId,
                          @RequestParam(required = false) Long elderlyId) {
        LambdaQueryWrapper<ServiceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(caregiverId != null, ServiceRecord::getCaregiverId, caregiverId);
        wrapper.eq(elderlyId != null, ServiceRecord::getElderlyId, elderlyId);
        wrapper.orderByDesc(ServiceRecord::getServiceTime);
        Page<ServiceRecord> page = serviceRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillNames);
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result<?> myRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<ServiceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceRecord::getCaregiverId, userId);
        wrapper.orderByDesc(ServiceRecord::getServiceTime);
        java.util.List<ServiceRecord> list = serviceRecordMapper.selectList(wrapper);
        list.forEach(this::fillNames);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> add(@RequestBody ServiceRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setCaregiverId(userId);
        if (record.getServiceTime() == null) {
            record.setServiceTime(java.time.LocalDateTime.now());
        }
        serviceRecordMapper.insert(record);
        return Result.success("记录成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody ServiceRecord record) {
        serviceRecordMapper.updateById(record);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        serviceRecordMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
