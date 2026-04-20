package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.Activity;
import com.eldercare.entity.ActivityRegistration;
import com.eldercare.entity.User;
import com.eldercare.mapper.ActivityMapper;
import com.eldercare.mapper.ActivityRegistrationMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityRegistrationMapper registrationMapper;
    @Resource
    private UserMapper userMapper;

    private void fillOrganizerName(Activity activity) {
        if (activity.getOrganizerId() != null) {
            User u = userMapper.selectById(activity.getOrganizerId());
            if (u != null)
                activity.setOrganizerName(u.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String title) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null && !status.isEmpty(), Activity::getStatus, status);
        wrapper.like(title != null && !title.isEmpty(), Activity::getTitle, title);
        wrapper.orderByAsc(Activity::getId);
        Page<Activity> page = activityMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillOrganizerName);
        return Result.success(page);
    }

    @GetMapping("/published")
    public Result<?> published(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Activity::getStatus, "UPCOMING", "ONGOING");
        wrapper.orderByAsc(Activity::getId);
        Page<Activity> page = activityMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillOrganizerName);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id, HttpServletRequest request) {
        Activity activity = activityMapper.selectById(id);
        if (activity != null) {
            fillOrganizerName(activity);
            Long userId = (Long) request.getAttribute("userId");
            if (userId != null) {
                long count = registrationMapper.selectCount(new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getActivityId, id)
                        .eq(ActivityRegistration::getUserId, userId)
                        .ne(ActivityRegistration::getStatus, "CANCELLED"));
                activity.setRegistered(count > 0);
            }
        }
        return Result.success(activity);
    }

    @PostMapping
    public Result<?> add(@RequestBody Activity activity, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        activity.setOrganizerId(userId);
        activityMapper.insert(activity);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody Activity activity) {
        activityMapper.updateById(activity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        activityMapper.deleteById(id);
        registrationMapper
                .delete(new LambdaQueryWrapper<ActivityRegistration>().eq(ActivityRegistration::getActivityId, id));
        return Result.success("删除成功");
    }

    @PostMapping("/register/{id}")
    public Result<?> register(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        long count = registrationMapper.selectCount(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, id)
                .eq(ActivityRegistration::getUserId, userId)
                .ne(ActivityRegistration::getStatus, "CANCELLED"));
        if (count > 0)
            return Result.error("您已报名该活动");
        Activity activity = activityMapper.selectById(id);
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return Result.error("报名人数已满");
        }
        ActivityRegistration reg = new ActivityRegistration();
        reg.setActivityId(id);
        reg.setUserId(userId);
        reg.setStatus("REGISTERED");
        registrationMapper.insert(reg);
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);
        return Result.success("报名成功");
    }

    @PostMapping("/cancel/{id}")
    public Result<?> cancelRegistration(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ActivityRegistration reg = registrationMapper.selectOne(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, id)
                .eq(ActivityRegistration::getUserId, userId)
                .eq(ActivityRegistration::getStatus, "REGISTERED"));
        if (reg == null)
            return Result.error("未找到报名记录");
        reg.setStatus("CANCELLED");
        registrationMapper.updateById(reg);
        Activity activity = activityMapper.selectById(id);
        activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
        activityMapper.updateById(activity);
        return Result.success("取消报名成功");
    }

    @GetMapping("/registrations/{activityId}")
    public Result<?> registrations(@PathVariable Long activityId) {
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getActivityId, activityId);
        wrapper.orderByAsc(ActivityRegistration::getId);
        List<ActivityRegistration> list = registrationMapper.selectList(wrapper);
        list.forEach(r -> {
            User u = userMapper.selectById(r.getUserId());
            if (u != null)
                r.setUserName(u.getName());
            Activity a = activityMapper.selectById(r.getActivityId());
            if (a != null)
                r.setActivityTitle(a.getTitle());
        });
        return Result.success(list);
    }

    @GetMapping("/myRegistrations")
    public Result<?> myRegistrations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getUserId, userId);
        wrapper.orderByAsc(ActivityRegistration::getId);
        List<ActivityRegistration> list = registrationMapper.selectList(wrapper);
        list.forEach(r -> {
            Activity a = activityMapper.selectById(r.getActivityId());
            if (a != null)
                r.setActivityTitle(a.getTitle());
        });
        return Result.success(list);
    }
}
