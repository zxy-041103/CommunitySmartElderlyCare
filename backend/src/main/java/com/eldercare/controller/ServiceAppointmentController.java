package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.ServiceAppointment;
import com.eldercare.entity.ServiceItem;
import com.eldercare.entity.User;
import com.eldercare.mapper.ServiceAppointmentMapper;
import com.eldercare.mapper.ServiceItemMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/appointment")
public class ServiceAppointmentController {

    @Resource
    private ServiceAppointmentMapper appointmentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ServiceItemMapper serviceItemMapper;

    private void fillNames(ServiceAppointment apt) {
        if (apt.getElderlyId() != null) {
            User u = userMapper.selectById(apt.getElderlyId());
            if (u != null) apt.setElderlyName(u.getName());
        }
        if (apt.getCaregiverId() != null) {
            User u = userMapper.selectById(apt.getCaregiverId());
            if (u != null) apt.setCaregiverName(u.getName());
        }
        if (apt.getServiceItemId() != null) {
            ServiceItem si = serviceItemMapper.selectById(apt.getServiceItemId());
            if (si != null) apt.setServiceItemName(si.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) Long elderlyId,
                          @RequestParam(required = false) Long caregiverId) {
        LambdaQueryWrapper<ServiceAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null && !status.isEmpty(), ServiceAppointment::getStatus, status);
        wrapper.eq(elderlyId != null, ServiceAppointment::getElderlyId, elderlyId);
        wrapper.eq(caregiverId != null, ServiceAppointment::getCaregiverId, caregiverId);
        wrapper.orderByDesc(ServiceAppointment::getCreateTime);
        Page<ServiceAppointment> page = appointmentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillNames);
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result<?> myAppointments(HttpServletRequest request,
                                     @RequestParam(required = false) String status) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        LambdaQueryWrapper<ServiceAppointment> wrapper = new LambdaQueryWrapper<>();
        if ("CAREGIVER".equals(role)) {
            wrapper.eq(ServiceAppointment::getCaregiverId, userId);
        } else {
            wrapper.eq(ServiceAppointment::getElderlyId, userId);
        }
        wrapper.eq(status != null && !status.isEmpty(), ServiceAppointment::getStatus, status);
        wrapper.orderByDesc(ServiceAppointment::getCreateTime);
        java.util.List<ServiceAppointment> list = appointmentMapper.selectList(wrapper);
        list.forEach(this::fillNames);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> add(@RequestBody ServiceAppointment apt, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        apt.setElderlyId(userId);
        apt.setStatus("PENDING");
        appointmentMapper.insert(apt);
        return Result.success("预约成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody ServiceAppointment apt) {
        appointmentMapper.updateById(apt);
        return Result.success("更新成功");
    }

    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody ServiceAppointment apt) {
        ServiceAppointment existing = appointmentMapper.selectById(id);
        existing.setStatus(apt.getStatus());
        if (apt.getCaregiverId() != null) existing.setCaregiverId(apt.getCaregiverId());
        appointmentMapper.updateById(existing);
        return Result.success("状态更新成功");
    }

    @PutMapping("/rate/{id}")
    public Result<?> rate(@PathVariable Long id, @RequestBody ServiceAppointment apt) {
        ServiceAppointment existing = appointmentMapper.selectById(id);
        existing.setRating(apt.getRating());
        existing.setRatingContent(apt.getRatingContent());
        appointmentMapper.updateById(existing);
        return Result.success("评价成功");
    }

    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        ServiceAppointment existing = appointmentMapper.selectById(id);
        existing.setStatus("CANCELLED");
        appointmentMapper.updateById(existing);
        return Result.success("已取消");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        appointmentMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
