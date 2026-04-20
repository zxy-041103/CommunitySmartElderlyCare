package com.eldercare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.Result;
import com.eldercare.entity.ServiceCategory;
import com.eldercare.entity.ServiceItem;
import com.eldercare.mapper.ServiceCategoryMapper;
import com.eldercare.mapper.ServiceItemMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/serviceItem")
public class ServiceItemController {

    @Resource
    private ServiceItemMapper serviceItemMapper;
    @Resource
    private ServiceCategoryMapper serviceCategoryMapper;

    private void fillCategoryName(ServiceItem item) {
        if (item.getCategoryId() != null) {
            ServiceCategory cat = serviceCategoryMapper.selectById(item.getCategoryId());
            if (cat != null) item.setCategoryName(cat.getName());
        }
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) Long categoryId) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), ServiceItem::getName, name);
        wrapper.eq(categoryId != null, ServiceItem::getCategoryId, categoryId);
        wrapper.orderByAsc(ServiceItem::getId);
        Page<ServiceItem> page = serviceItemMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillCategoryName);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Long categoryId) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceItem::getStatus, 1);
        wrapper.eq(categoryId != null, ServiceItem::getCategoryId, categoryId);
        wrapper.orderByAsc(ServiceItem::getId);
        List<ServiceItem> list = serviceItemMapper.selectList(wrapper);
        list.forEach(this::fillCategoryName);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        ServiceItem item = serviceItemMapper.selectById(id);
        if (item != null) fillCategoryName(item);
        return Result.success(item);
    }

    @PostMapping
    public Result<?> add(@RequestBody ServiceItem item) {
        serviceItemMapper.insert(item);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody ServiceItem item) {
        serviceItemMapper.updateById(item);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        serviceItemMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
