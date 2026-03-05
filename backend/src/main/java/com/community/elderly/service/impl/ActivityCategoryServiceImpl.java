package com.community.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.elderly.entity.ActivityCategory;
import com.community.elderly.mapper.admin.ActivityCategoryMapper;
import com.community.elderly.service.admin.ActivityCategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 活动分类服务实现
 */
@Service
public class ActivityCategoryServiceImpl extends ServiceImpl<ActivityCategoryMapper, ActivityCategory> implements ActivityCategoryService {
    
    @Override
    public List<ActivityCategory> getEnabledCategories() {
        LambdaQueryWrapper<ActivityCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityCategory::getIsEnabled, 1);
        wrapper.orderByAsc(ActivityCategory::getSort);
        return baseMapper.selectList(wrapper);
    }
}
