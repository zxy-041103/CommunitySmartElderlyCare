package com.eldercare.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.Result;
import com.eldercare.entity.User;
import com.eldercare.mapper.UserMapper;
import com.eldercare.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        String md5Password = DigestUtil.md5Hex(password);
        if (!md5Password.equals(user.getPassword())) {
            return Result.error("密码错误");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        user.setPassword(null);
        data.put("user", user);
        return Result.success("登录成功", data);
    }

    @GetMapping("/info")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        User user = userMapper.selectById(userId);
        if (!DigestUtil.md5Hex(oldPassword).equals(user.getPassword())) {
            return Result.error("原密码错误");
        }
        user.setPassword(DigestUtil.md5Hex(newPassword));
        userMapper.updateById(user);
        return Result.success("密码修改成功");
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        user.setPassword(null);
        user.setRole(null);
        user.setUsername(null);
        userMapper.updateById(user);
        User updated = userMapper.selectById(userId);
        updated.setPassword(null);
        return Result.success("更新成功", updated);
    }
}
