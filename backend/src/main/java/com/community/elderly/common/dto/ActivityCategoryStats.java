package com.community.elderly.common.dto;

import lombok.Data;

/**
 * 活动分类统计
 */
@Data
public class ActivityCategoryStats {
    
    private Long categoryId;
    
    private String categoryName;
    
    private Integer activityCount;
    
    private Integer totalParticipants;
}
