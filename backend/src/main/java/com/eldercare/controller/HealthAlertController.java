package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.HealthAlert;
import com.eldercare.entity.User;
import com.eldercare.mapper.HealthAlertMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/healthAlert")
public class HealthAlertController {

    @Resource
    private HealthAlertMapper healthAlertMapper;
    @Resource
    private UserMapper userMapper;

    private void fillNames(HealthAlert alert) {
        if (alert.getElderlyId() != null) {
            User u = userMapper.selectById(alert.getElderlyId());
            if (u != null) alert.setElderlyName(u.getName());
        }
        if (alert.getProcessorId() != null) {
            User u = userMapper.selectById(alert.getProcessorId());
            if (u != null) alert.setProcessorName(u.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String alertLevel) {
        LambdaQueryWrapper<HealthAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null && !status.isEmpty(), HealthAlert::getStatus, status);
        wrapper.eq(alertLevel != null && !alertLevel.isEmpty(), HealthAlert::getAlertLevel, alertLevel);
        wrapper.orderByDesc(HealthAlert::getCreateTime);
        Page<HealthAlert> page = healthAlertMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillNames);
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result<?> myAlerts(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<HealthAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAlert::getElderlyId, userId);
        wrapper.orderByDesc(HealthAlert::getCreateTime);
        List<HealthAlert> list = healthAlertMapper.selectList(wrapper);
        list.forEach(this::fillNames);
        return Result.success(list);
    }

    @GetMapping("/pending/count")
    public Result<?> pendingCount() {
        long count = healthAlertMapper.selectCount(
                new LambdaQueryWrapper<HealthAlert>().eq(HealthAlert::getStatus, "PENDING"));
        return Result.success(count);
    }

    @PostMapping
    public Result<?> add(@RequestBody HealthAlert alert) {
        healthAlertMapper.insert(alert);
        return Result.success("添加成功");
    }

    @PutMapping("/process/{id}")
    public Result<?> process(@PathVariable Long id, @RequestBody HealthAlert alert, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        HealthAlert existing = healthAlertMapper.selectById(id);
        existing.setStatus(alert.getStatus());
        existing.setProcessNote(alert.getProcessNote());
        existing.setProcessorId(userId);
        existing.setProcessTime(LocalDateTime.now());
        healthAlertMapper.updateById(existing);
        return Result.success("处理成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        healthAlertMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
