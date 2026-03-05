package com.community.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@TableName("sys_announcement")
public class Announcement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String content;
    
    @TableField("announcement_type")
    private String announcementType;
    
    @TableField("target_role")
    private String targetRole;
    
    @TableField("publisher_id")
    private Long publisherId;
    
    @TableField("is_top")
    private Integer isTop;
    
    @TableField("is_published")
    private Integer isPublished;
    
    @TableField("publish_time")
    private LocalDateTime publishTime;
    
    @TableField("view_count")
    private Integer viewCount;
    
    @TableField("activity_category_id")
    private Long activityCategoryId;
    
    @TableField("activity_time")
    private LocalDateTime activityTime;
    
    @TableField("activity_location")
    private String activityLocation;
    
    @TableField("participant_count")
    private Integer participantCount;
    
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}