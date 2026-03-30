package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.HealthData;
import com.eldercare.entity.User;
import com.eldercare.mapper.HealthDataMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/healthData")
public class HealthDataController {

    @Resource
    private HealthDataMapper healthDataMapper;
    @Resource
    private UserMapper userMapper;

    private void fillNames(HealthData data) {
        if (data.getElderlyId() != null) {
            User elderly = userMapper.selectById(data.getElderlyId());
            if (elderly != null) data.setElderlyName(elderly.getName());
        }
        if (data.getRecorderId() != null) {
            User recorder = userMapper.selectById(data.getRecorderId());
            if (recorder != null) data.setRecorderName(recorder.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long elderlyId,
                          @RequestParam(required = false) String elderlyName) {
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        if (elderlyId != null) {
            wrapper.eq(HealthData::getElderlyId, elderlyId);
        }
        if (elderlyName != null && !elderlyName.isEmpty()) {
            LambdaQueryWrapper<User> uw = new LambdaQueryWrapper<>();
            uw.like(User::getName, elderlyName).eq(User::getRole, "USER");
            List<User> users = userMapper.selectList(uw);
            if (users.isEmpty()) return Result.success(new Page<>(pageNum, pageSize));
            wrapper.in(HealthData::getElderlyId, users.stream().map(User::getId).collect(java.util.stream.Collectors.toList()));
        }
        wrapper.orderByDesc(HealthData::getRecordTime);
        Page<HealthData> page = healthDataMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillNames);
        return Result.success(page);
    }

    @GetMapping("/list/{elderlyId}")
    public Result<?> listByElderly(@PathVariable Long elderlyId,
                                    @RequestParam(defaultValue = "7") Integer days) {
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getElderlyId, elderlyId);
        wrapper.ge(HealthData::getRecordTime, java.time.LocalDateTime.now().minusDays(days));
        wrapper.orderByAsc(HealthData::getRecordTime);
        List<HealthData> list = healthDataMapper.selectList(wrapper);
        list.forEach(this::fillNames);
        return Result.success(list);
    }

    @GetMapping("/latest/{elderlyId}")
    public Result<?> getLatest(@PathVariable Long elderlyId) {
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getElderlyId, elderlyId);
        wrapper.orderByDesc(HealthData::getRecordTime);
        wrapper.last("LIMIT 1");
        HealthData data = healthDataMapper.selectOne(wrapper);
        if (data != null) fillNames(data);
        return Result.success(data);
    }

    @GetMapping("/my")
    public Result<?> myHealthData(HttpServletRequest request,
                                   @RequestParam(defaultValue = "7") Integer days) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getElderlyId, userId);
        wrapper.ge(HealthData::getRecordTime, java.time.LocalDateTime.now().minusDays(days));
        wrapper.orderByAsc(HealthData::getRecordTime);
        return Result.success(healthDataMapper.selectList(wrapper));
    }

    @PostMapping
    public Result<?> add(@RequestBody HealthData data, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        data.setRecorderId(userId);
        if (data.getRecordTime() == null) {
            data.setRecordTime(java.time.LocalDateTime.now());
        }
        healthDataMapper.insert(data);
        return Result.success("记录成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody HealthData data) {
        healthDataMapper.updateById(data);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        healthDataMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
