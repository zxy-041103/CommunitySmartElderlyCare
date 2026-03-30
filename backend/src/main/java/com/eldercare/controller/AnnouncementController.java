package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.Announcement;
import com.eldercare.entity.User;
import com.eldercare.mapper.AnnouncementMapper;
import com.eldercare.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Resource
    private AnnouncementMapper announcementMapper;
    @Resource
    private UserMapper userMapper;

    private void fillPublisherName(Announcement ann) {
        if (ann.getPublisherId() != null) {
            User u = userMapper.selectById(ann.getPublisherId());
            if (u != null) ann.setPublisherName(u.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) String title) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null && !type.isEmpty(), Announcement::getType, type);
        wrapper.like(title != null && !title.isEmpty(), Announcement::getTitle, title);
        wrapper.orderByDesc(Announcement::getTop).orderByDesc(Announcement::getCreateTime);
        Page<Announcement> page = announcementMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillPublisherName);
        return Result.success(page);
    }

    @GetMapping("/published")
    public Result<?> published(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1);
        wrapper.orderByDesc(Announcement::getTop).orderByDesc(Announcement::getCreateTime);
        Page<Announcement> page = announcementMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillPublisherName);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Announcement ann = announcementMapper.selectById(id);
        if (ann != null) fillPublisherName(ann);
        return Result.success(ann);
    }

    @PostMapping
    public Result<?> add(@RequestBody Announcement ann, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ann.setPublisherId(userId);
        announcementMapper.insert(ann);
        return Result.success("发布成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody Announcement ann) {
        announcementMapper.updateById(ann);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        announcementMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
