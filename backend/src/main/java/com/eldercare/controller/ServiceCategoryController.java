package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.ServiceCategory;
import com.eldercare.mapper.ServiceCategoryMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/serviceCategory")
public class ServiceCategoryController {

    @Resource
    private ServiceCategoryMapper serviceCategoryMapper;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String name) {
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), ServiceCategory::getName, name);
        wrapper.orderByAsc(ServiceCategory::getSortOrder);
        Page<ServiceCategory> page = serviceCategoryMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<?> list() {
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceCategory::getStatus, 1);
        wrapper.orderByAsc(ServiceCategory::getSortOrder);
        return Result.success(serviceCategoryMapper.selectList(wrapper));
    }

    @PostMapping
    public Result<?> add(@RequestBody ServiceCategory category) {
        serviceCategoryMapper.insert(category);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody ServiceCategory category) {
        serviceCategoryMapper.updateById(category);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        serviceCategoryMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
