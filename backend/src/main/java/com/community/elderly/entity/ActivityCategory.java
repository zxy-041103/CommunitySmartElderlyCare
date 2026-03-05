package com.community.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动分类实体类
 */
@Data
@TableName("activity_category")
public class ActivityCategory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("category_name")
    private String categoryName;
    
    private String description;
    
    private Integer sort;
    
    @TableField("is_enabled")
    private Integer isEnabled;
    
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}