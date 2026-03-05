package com.community.elderly.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.elderly.common.Result;
import com.community.elderly.entity.ActivityCategory;
import com.community.elderly.entity.Announcement;
import com.community.elderly.service.admin.ActivityCategoryService;
import com.community.elderly.service.admin.AnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动管理Controller
 */
@RestController
@RequestMapping("/admin/activities")
@Api(tags = "活动管理接口")
public class AdminActivityController {
    
    @Autowired
    private ActivityCategoryService activityCategoryService;
    
    @Autowired
    private AnnouncementService announcementService;
    
    // 活动分类管理
    
    @GetMapping("/categories")
    @ApiOperation(value = "获取活动分类列表", notes = "获取所有活动分类")
    public Result<List<ActivityCategory>> getActivityCategories() {
        return Result.success(activityCategoryService.list());
    }
    
    @PostMapping("/categories")
    @ApiOperation(value = "创建活动分类", notes = "创建新的活动分类")
    public Result<ActivityCategory> createCategory(@RequestBody ActivityCategory category) {
        activityCategoryService.save(category);
        return Result.success(category);
    }
    
    @PutMapping("/categories/{id}")
    @ApiOperation(value = "更新活动分类", notes = "更新活动分类信息")
    public Result<ActivityCategory> updateCategory(
            @ApiParam("分类ID") @PathVariable Long id,
            @RequestBody ActivityCategory category) {
        category.setId(id);
        activityCategoryService.updateById(category);
        return Result.success(category);
    }
    
    @DeleteMapping("/categories/{id}")
    @ApiOperation(value = "删除活动分类", notes = "删除活动分类")
    public Result<?> deleteCategory(@ApiParam("分类ID") @PathVariable Long id) {
        activityCategoryService.removeById(id);
        return Result.success();
    }
    
    // 活动管理
    
    @GetMapping
    @ApiOperation(value = "获取活动列表", notes = "分页获取活动列表")
    public Result<IPage<Announcement>> getActivities(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        Page<Announcement> pagination = new Page<>(page, size);
        IPage<Announcement> result = announcementService.page(pagination, 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getAnnouncementType, "activity")
                .orderByDesc(Announcement::getCreateTime)
        );
        return Result.success(result);
    }
    
    @PostMapping
    @ApiOperation(value = "创建活动", notes = "创建新的活动")
    public Result<Announcement> createActivity(@RequestBody Announcement activity) {
        activity.setAnnouncementType("activity");
        announcementService.save(activity);
        return Result.success(activity);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "更新活动", notes = "更新活动信息")
    public Result<Announcement> updateActivity(
            @ApiParam("活动ID") @PathVariable Long id,
            @RequestBody Announcement activity) {
        activity.setId(id);
        announcementService.updateById(activity);
        return Result.success(activity);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除活动", notes = "删除活动")
    public Result<?> deleteActivity(@ApiParam("活动ID") @PathVariable Long id) {
        announcementService.removeById(id);
        return Result.success();
    }
    
    @GetMapping("/category-stats")
    @ApiOperation(value = "获取活动分类统计", notes = "获取活动分类统计数据")
    public Result<List<com.community.elderly.common.dto.ActivityCategoryStats>> getActivityCategoryStats() {
        return Result.success(announcementService.getActivityCategoryStats());
    }
}
