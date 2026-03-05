package com.community.elderly.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.elderly.entity.ActivityCategory;
import java.util.List;

/**
 * 活动分类服务
 */
public interface ActivityCategoryService extends IService<ActivityCategory> {
    
    /**
     * 获取所有启用的活动分类
     */
    List<ActivityCategory> getEnabledCategories();
}
