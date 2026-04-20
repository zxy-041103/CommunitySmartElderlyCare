package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.Feedback;
import com.eldercare.entity.User;
import com.eldercare.mapper.FeedbackMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Resource
    private FeedbackMapper feedbackMapper;
    @Resource
    private UserMapper userMapper;

    private void fillNames(Feedback fb) {
        if (fb.getUserId() != null) {
            User u = userMapper.selectById(fb.getUserId());
            if (u != null) fb.setUserName(u.getName());
        }
        if (fb.getReplyBy() != null) {
            User u = userMapper.selectById(fb.getReplyBy());
            if (u != null) fb.setReplyByName(u.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String type) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null && !status.isEmpty(), Feedback::getStatus, status);
        wrapper.eq(type != null && !type.isEmpty(), Feedback::getType, type);
        wrapper.orderByAsc(Feedback::getId);
        Page<Feedback> page = feedbackMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillNames);
        return Result.success(page);
    }

    @GetMapping("/my")
    public Result<?> myFeedback(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId);
        wrapper.orderByAsc(Feedback::getId);
        java.util.List<Feedback> list = feedbackMapper.selectList(wrapper);
        list.forEach(this::fillNames);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> add(@RequestBody Feedback fb, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        fb.setUserId(userId);
        fb.setStatus("PENDING");
        feedbackMapper.insert(fb);
        return Result.success("提交成功");
    }

    @PutMapping("/reply/{id}")
    public Result<?> reply(@PathVariable Long id, @RequestBody Feedback fb, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Feedback existing = feedbackMapper.selectById(id);
        existing.setReply(fb.getReply());
        existing.setReplyBy(userId);
        existing.setReplyTime(LocalDateTime.now());
        existing.setStatus("REPLIED");
        feedbackMapper.updateById(existing);
        return Result.success("回复成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        feedbackMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
