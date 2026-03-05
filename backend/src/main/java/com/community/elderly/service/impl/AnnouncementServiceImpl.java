package com.community.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.elderly.common.dto.ActivityCategoryStats;
import com.community.elderly.entity.Announcement;
import com.community.elderly.entity.ActivityCategory;
import com.community.elderly.mapper.admin.AnnouncementMapper;
import com.community.elderly.service.admin.ActivityCategoryService;
import com.community.elderly.service.admin.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公告服务实现
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
    
    @Autowired
    private ActivityCategoryService activityCategoryService;
    
    @Override
    public List<Announcement> getActivityList() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getAnnouncementType, "activity");
        wrapper.eq(Announcement::getIsPublished, 1);
        wrapper.orderByDesc(Announcement::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public List<ActivityCategoryStats> getActivityCategoryStats() {
        // 获取所有活动
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getAnnouncementType, "activity");
        wrapper.eq(Announcement::getIsPublished, 1);
        List<Announcement> activities = baseMapper.selectList(wrapper);
        
        // 按分类统计
        Map<Long, List<Announcement>> categoryMap = activities.stream()
            .filter(activity -> activity.getActivityCategoryId() != null)
            .collect(Collectors.groupingBy(Announcement::getActivityCategoryId));
        
        // 获取所有活动分类
        List<ActivityCategory> categories = activityCategoryService.list();
        Map<Long, String> categoryNameMap = categories.stream()
            .collect(Collectors.toMap(ActivityCategory::getId, ActivityCategory::getCategoryName));
        
        // 构建统计结果
        List<ActivityCategoryStats> statsList = new ArrayList<>();
        categoryMap.forEach((categoryId, categoryActivities) -> {
            ActivityCategoryStats stats = new ActivityCategoryStats();
            stats.setCategoryId(categoryId);
            stats.setActivityCount(categoryActivities.size());
            stats.setTotalParticipants(categoryActivities.stream()
                .mapToInt(activity -> activity.getParticipantCount() != null ? activity.getParticipantCount() : 0)
                .sum());
            
            // 设置分类名称（从分类表中查询）
            stats.setCategoryName(categoryNameMap.getOrDefault(categoryId, "未知分类"));
            
            statsList.add(stats);
        });
        
        return statsList;
    }
}
