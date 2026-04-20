package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.WorkSchedule;
import com.eldercare.entity.User;
import com.eldercare.mapper.WorkScheduleMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workSchedule")
public class WorkScheduleController {

    @Resource
    private WorkScheduleMapper workScheduleMapper;
    @Resource
    private UserMapper userMapper;

    private void fillCaregiverName(WorkSchedule ws) {
        if (ws.getCaregiverId() != null) {
            User u = userMapper.selectById(ws.getCaregiverId());
            if (u != null) ws.setCaregiverName(u.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long caregiverId,
                          @RequestParam(required = false) String workDate) {
        LambdaQueryWrapper<WorkSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(caregiverId != null, WorkSchedule::getCaregiverId, caregiverId);
        if (workDate != null && !workDate.isEmpty()) {
            wrapper.eq(WorkSchedule::getWorkDate, LocalDate.parse(workDate));
        }
        wrapper.orderByAsc(WorkSchedule::getId);
        Page<WorkSchedule> page = workScheduleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillCaregiverName);
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result<?> mySchedule(HttpServletRequest request,
                                 @RequestParam(required = false) String startDate,
                                 @RequestParam(required = false) String endDate) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<WorkSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkSchedule::getCaregiverId, userId);
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(WorkSchedule::getWorkDate, LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(WorkSchedule::getWorkDate, LocalDate.parse(endDate));
        }
        wrapper.orderByAsc(WorkSchedule::getId);
        List<WorkSchedule> list = workScheduleMapper.selectList(wrapper);
        list.forEach(this::fillCaregiverName);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> add(@RequestBody WorkSchedule ws) {
        workScheduleMapper.insert(ws);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody WorkSchedule ws) {
        workScheduleMapper.updateById(ws);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        workScheduleMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
