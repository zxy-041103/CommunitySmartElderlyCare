package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.ElderlyProfile;
import com.eldercare.entity.User;
import com.eldercare.mapper.ElderlyProfileMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/elderlyProfile")
public class ElderlyProfileController {

    @Resource
    private ElderlyProfileMapper elderlyProfileMapper;
    @Resource
    private UserMapper userMapper;

    private void fillUserInfo(ElderlyProfile profile) {
        if (profile != null && profile.getUserId() != null) {
            User user = userMapper.selectById(profile.getUserId());
            if (user != null) {
                profile.setUserName(user.getName());
                profile.setUserPhone(user.getPhone());
                profile.setUserAge(user.getAge());
                profile.setUserGender(user.getGender());
                profile.setUserAddress(user.getAddress());
            }
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String name) {
        Page<ElderlyProfile> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ElderlyProfile> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getName, name).eq(User::getRole, "USER");
            java.util.List<User> users = userMapper.selectList(userWrapper);
            if (users.isEmpty()) return Result.success(page);
            wrapper.in(ElderlyProfile::getUserId, users.stream().map(User::getId).collect(java.util.stream.Collectors.toList()));
        }
        wrapper.orderByDesc(ElderlyProfile::getCreateTime);
        elderlyProfileMapper.selectPage(page, wrapper);
        page.getRecords().forEach(this::fillUserInfo);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        ElderlyProfile profile = elderlyProfileMapper.selectById(id);
        fillUserInfo(profile);
        return Result.success(profile);
    }

    @GetMapping("/byUserId/{userId}")
    public Result<?> getByUserId(@PathVariable Long userId) {
        ElderlyProfile profile = elderlyProfileMapper.selectOne(
                new LambdaQueryWrapper<ElderlyProfile>().eq(ElderlyProfile::getUserId, userId));
        if (profile != null) fillUserInfo(profile);
        return Result.success(profile);
    }

    @PostMapping
    public Result<?> add(@RequestBody ElderlyProfile profile) {
        ElderlyProfile existing = elderlyProfileMapper.selectOne(
                new LambdaQueryWrapper<ElderlyProfile>().eq(ElderlyProfile::getUserId, profile.getUserId()));
        if (existing != null) {
            return Result.error("该用户档案已存在");
        }
        elderlyProfileMapper.insert(profile);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody ElderlyProfile profile) {
        elderlyProfileMapper.updateById(profile);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        elderlyProfileMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/my")
    public Result<?> getMyProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ElderlyProfile profile = elderlyProfileMapper.selectOne(
                new LambdaQueryWrapper<ElderlyProfile>().eq(ElderlyProfile::getUserId, userId));
        if (profile != null) fillUserInfo(profile);
        return Result.success(profile);
    }

    @PostMapping("/my")
    public Result<?> saveMyProfile(@RequestBody ElderlyProfile profile, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ElderlyProfile existing = elderlyProfileMapper.selectOne(
                new LambdaQueryWrapper<ElderlyProfile>().eq(ElderlyProfile::getUserId, userId));
        if (existing != null) {
            profile.setId(existing.getId());
            profile.setUserId(userId);
            elderlyProfileMapper.updateById(profile);
        } else {
            profile.setUserId(userId);
            elderlyProfileMapper.insert(profile);
        }
        return Result.success("保存成功");
    }
}
