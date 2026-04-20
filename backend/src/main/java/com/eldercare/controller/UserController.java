package com.eldercare.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.User;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), User::getName, name);
        wrapper.eq(role != null && !role.isEmpty(), User::getRole, role);
        wrapper.orderByAsc(User::getId);
        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(role != null && !role.isEmpty(), User::getRole, role);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByAsc(User::getId);
        java.util.List<User> list = userMapper.selectList(wrapper);
        list.forEach(u -> u.setPassword(null));
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    public Result<?> add(@RequestBody User user) {
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())) != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword(DigestUtil.md5Hex(user.getPassword() != null ? user.getPassword() : "123456"));
        userMapper.insert(user);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody User user) {
        user.setPassword(null);
        userMapper.updateById(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/resetPassword/{id}")
    public Result<?> resetPassword(@PathVariable Long id) {
        User user = new User();
        user.setId(id);
        user.setPassword(DigestUtil.md5Hex("123456"));
        userMapper.updateById(user);
        return Result.success("密码已重置为123456");
    }
}
