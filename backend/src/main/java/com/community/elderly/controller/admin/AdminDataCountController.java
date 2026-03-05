package com.community.elderly.controller.admin;

import com.community.elderly.common.Result;
import com.community.elderly.dto.admin.DataCountStatisticsDTO;
import com.community.elderly.service.admin.DataCountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员数据统计详情页控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/data-count")
@Tag(name = "管理员数据统计详情页", description = "提供数据统计详情页相关接口")
public class AdminDataCountController {
    
    @Autowired
    private DataCountService dataCountService;
    
    /**
     * 获取数据统计详情页统计数据
     * 
     * @param timeRange 时间范围：week(本周)、month(本月)、year(本年)，默认month
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取数据统计详情页统计数据", 
               description = "返回健康监测覆盖率、服务核销率、紧急求助响应率、老人满意度、健康异常率趋势、护工工作量分布、服务类型占比、老人健康状态分布")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<DataCountStatisticsDTO> getStatistics(
            @RequestParam(defaultValue = "month")
            @Parameter(description = "时间范围：week(本周)、month(本月)、year(本年)，默认month") 
            String timeRange) {
        log.info("获取数据统计详情页统计数据，时间范围：{}", timeRange);
        try {
            // 验证时间范围参数
            if (!"week".equals(timeRange) && !"month".equals(timeRange) && !"year".equals(timeRange)) {
                timeRange = "month";
            }
            
            DataCountStatisticsDTO statistics = dataCountService.getStatistics(timeRange);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取数据统计详情页统计数据失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
}