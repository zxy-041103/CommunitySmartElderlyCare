package com.community.elderly.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.elderly.entity.Announcement;
import java.util.List;

/**
 * 公告服务
 */
public interface AnnouncementService extends IService<Announcement> {
    
    /**
     * 获取活动列表
     */
    List<Announcement> getActivityList();
    
    /**
     * 获取活动分类统计
     */
    List<com.community.elderly.common.dto.ActivityCategoryStats> getActivityCategoryStats();
}
