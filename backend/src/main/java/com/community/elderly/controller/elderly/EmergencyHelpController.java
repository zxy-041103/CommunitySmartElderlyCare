package com.community.elderly.controller.elderly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.elderly.dto.elderly.CreateEmergencyHelpRequest;
import com.community.elderly.dto.elderly.UpdateHelpStatusRequest;
import com.community.elderly.entity.EmergencyHelp;
import com.community.elderly.service.elderly.EmergencyHelpService;
import com.community.elderly.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/emergency-help")
@Api(tags = "紧急求助接口")
public class EmergencyHelpController {

    @Autowired
    private EmergencyHelpService emergencyHelpService;

    /**
     * 一键求助提交
     * 紧急求助接口：一键求助提交
     *
     * @param request 求助请求
     * @param authentication 认证信息
     * @return 求助记录ID
     */
    @PostMapping
    @ApiOperation(value = "一键求助提交", notes = "老人用户提交紧急求助，系统自动分配护工并发送通知")
    public Result<Long> submitEmergencyHelp(
            @Valid @RequestBody CreateEmergencyHelpRequest request,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            Long helpId = emergencyHelpService.submitEmergencyHelp(userId, request);
            return Result.success(helpId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 求助单分配（就近护工）
     * 紧急求助接口：求助单分配（就近护工）
     *
     * @param helpId 求助ID
     * @return 分配结果
     */
    @PostMapping("/{helpId}/assign")
    @ApiOperation(value = "求助单分配", notes = "自动分配就近护工处理求助")
    public Result<Map<String, Object>> assignCaregiver(
            @ApiParam(value = "求助ID", required = true) @PathVariable Long helpId) {
        try {
            Map<String, Object> result = emergencyHelpService.assignCaregiver(helpId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 求助状态更新
     * 紧急求助接口：求助状态更新
     *
     * @param helpId 求助ID
     * @param request 状态更新请求
     * @param authentication 认证信息
     * @return 更新结果
     */
    @PutMapping("/{helpId}/status")
    @ApiOperation(value = "更新求助状态", notes = "护工更新求助处理状态")
    public Result<Boolean> updateHelpStatus(
            @ApiParam(value = "求助ID", required = true) @PathVariable Long helpId,
            @Valid @RequestBody UpdateHelpStatusRequest request,
            Authentication authentication) {
        try {
            Long handlerId = Long.parseLong(authentication.getName());
            boolean result = emergencyHelpService.updateHelpStatus(helpId, handlerId, request);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 求助记录查询
     * 紧急求助接口：求助记录查询
     *
     * @param page 页码
     * @param size 每页大小
     * @param authentication 认证信息
     * @return 求助记录列表
     */
    @GetMapping
    @ApiOperation(value = "查询求助记录", notes = "查询用户的求助记录")
    public Result<IPage<EmergencyHelp>> getHelpRecords(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            IPage<EmergencyHelp> records = emergencyHelpService.getHelpRecords(userId, page, size);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取求助详情
     *
     * @param helpId 求助ID
     * @return 求助详情
     */
    @GetMapping("/{helpId}")
    @ApiOperation(value = "获取求助详情", notes = "获取指定求助的详细信息")
    public Result<EmergencyHelp> getHelpDetail(
            @ApiParam(value = "求助ID", required = true) @PathVariable Long helpId) {
        try {
            EmergencyHelp help = emergencyHelpService.getHelpDetail(helpId);
            return Result.success(help);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询所有求助记录（管理员用）
     *
     * @param page 页码
     * @param size 每页大小
     * @return 求助记录列表
     */
    @GetMapping("/admin/all")
    @ApiOperation(value = "查询所有求助记录", notes = "管理员查询所有求助记录")
    public Result<IPage<EmergencyHelp>> getAllHelpRecords(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<EmergencyHelp> pageParam = new Page<>(page, size);
            IPage<EmergencyHelp> records = emergencyHelpService.getAllHelpRecords(page, size);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询待处理求助记录
     *
     * @param page 页码
     * @param size 每页大小
     * @return 待处理求助记录列表
     */
    @GetMapping("/admin/pending")
    @ApiOperation(value = "查询待处理求助", notes = "管理员查询待处理的求助记录")
    public Result<IPage<EmergencyHelp>> getPendingHelpRecords(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<EmergencyHelp> pageParam = new Page<>(page, size);
            LambdaQueryWrapper<EmergencyHelp> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EmergencyHelp::getHelpStatus, "pending");
            IPage<EmergencyHelp> records = emergencyHelpService.getPendingHelpRecords(page, size);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询我处理的求助记录（护工用）
     *
     * @param page 页码
     * @param size 每页大小
     * @param authentication 认证信息
     * @return 求助记录列表
     */
    @GetMapping("/caregiver/handled")
    @ApiOperation(value = "查询我处理的求助", notes = "护工查询自己处理的求助记录")
    public Result<IPage<EmergencyHelp>> getHelpRecordsByHandler(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        try {
            Long handlerId = Long.parseLong(authentication.getName());
            Page<EmergencyHelp> pageParam = new Page<>(page, size);
            LambdaQueryWrapper<EmergencyHelp> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EmergencyHelp::getHandlerId, handlerId);
            IPage<EmergencyHelp> records = emergencyHelpService.getHelpRecordsByHandler(handlerId, page, size);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取紧急求助统计
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @ApiOperation(value = "获取求助统计", notes = "获取紧急求助的统计信息")
    public Result<Map<String, Object>> getHelpStatistics() {
        try {
            Map<String, Object> statistics = emergencyHelpService.getHelpStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取紧急求助列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param name 用户名
     * @param type 求助类型
     * @param status 求助状态
     * @return 求助记录列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取求助列表", notes = "分页查询紧急求助记录")
    public Result<Map<String, Object>> getHelpList(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "用户名") @RequestParam(required = false) String name,
            @ApiParam(value = "求助类型") @RequestParam(required = false) String type,
            @ApiParam(value = "求助状态") @RequestParam(required = false) String status) {
        try {
            Page<EmergencyHelp> pageParam = new Page<>(page, size);
            LambdaQueryWrapper<EmergencyHelp> wrapper = new LambdaQueryWrapper<>();
            if (status != null && !status.isEmpty()) {
                wrapper.eq(EmergencyHelp::getHelpStatus, status);
            }
            if (type != null && !type.isEmpty()) {
                wrapper.eq(EmergencyHelp::getHelpType, type);
            }
            IPage<EmergencyHelp> records = emergencyHelpService.getAllHelpRecords(page, size);
            Map<String, Object> result = new HashMap<>();
            result.put("records", records.getRecords());
            result.put("total", records.getTotal());
            result.put("pages", records.getPages());
            result.put("current", records.getCurrent());
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取紧急求助趋势
     *
     * @param days 天数
     * @return 趋势数据
     */
    @GetMapping("/trend")
    @ApiOperation(value = "获取求助趋势", notes = "获取紧急求助的趋势数据")
    public Result<Map<String, Object>> getHelpTrend(
            @ApiParam(value = "天数", defaultValue = "7") @RequestParam(defaultValue = "7") Integer days) {
        try {
            Map<String, Object> trend = emergencyHelpService.getHelpTrend(days);
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
