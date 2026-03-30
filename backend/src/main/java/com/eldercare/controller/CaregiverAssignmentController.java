package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.CaregiverAssignment;
import com.eldercare.entity.User;
import com.eldercare.mapper.CaregiverAssignmentMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/assignment")
public class CaregiverAssignmentController {

    @Resource
    private CaregiverAssignmentMapper assignmentMapper;
    @Resource
    private UserMapper userMapper;

    private void fillNames(CaregiverAssignment assignment) {
        if (assignment.getCaregiverId() != null) {
            User u = userMapper.selectById(assignment.getCaregiverId());
            if (u != null) {
                assignment.setCaregiverName(u.getName());
                assignment.setCaregiverPhone(u.getPhone());
            }
        }
        if (assignment.getElderlyId() != null) {
            User u = userMapper.selectById(assignment.getElderlyId());
            if (u != null) {
                assignment.setElderlyName(u.getName());
                assignment.setElderlyPhone(u.getPhone());
            }
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<CaregiverAssignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CaregiverAssignment::getCreateTime);
        Page<CaregiverAssignment> page = assignmentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillNames);
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result<?> myAssignments(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<CaregiverAssignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CaregiverAssignment::getCaregiverId, userId);
        wrapper.eq(CaregiverAssignment::getStatus, 1);
        wrapper.orderByDesc(CaregiverAssignment::getCreateTime);
        List<CaregiverAssignment> list = assignmentMapper.selectList(wrapper);
        list.forEach(this::fillNames);
        return Result.success(list);
    }

    @GetMapping("/myElderly")
    public Result<?> myElderly(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<CaregiverAssignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CaregiverAssignment::getCaregiverId, userId);
        wrapper.eq(CaregiverAssignment::getStatus, 1);
        List<CaregiverAssignment> assignments = assignmentMapper.selectList(wrapper);
        List<Long> elderlyIds = assignments.stream().map(CaregiverAssignment::getElderlyId).distinct().collect(java.util.stream.Collectors.toList());
        if (elderlyIds.isEmpty()) return Result.success(new java.util.ArrayList<>());
        List<User> elderlyList = userMapper.selectBatchIds(elderlyIds);
        elderlyList.forEach(u -> u.setPassword(null));
        return Result.success(elderlyList);
    }

    @PostMapping
    public Result<?> add(@RequestBody CaregiverAssignment assignment) {
        assignmentMapper.insert(assignment);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody CaregiverAssignment assignment) {
        assignmentMapper.updateById(assignment);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        assignmentMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
