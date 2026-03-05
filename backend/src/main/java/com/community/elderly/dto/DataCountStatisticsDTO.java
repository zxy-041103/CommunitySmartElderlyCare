package com.community.elderly.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 数据统计详情页统计数据传输对象
 */
@Data
public class DataCountStatisticsDTO {
    
    /**
     * 健康监测覆盖率
     */
    private String healthCoverageRate;
    
    /**
     * 服务核销率
     */
    private String serviceVerificationRate;
    
    /**
     * 紧急求助响应率
     */
    private String emergencyResponseRate;
    
    /**
     * 老人满意度
     */
    private String elderlySatisfaction;
    
    /**
     * 健康异常率趋势
     */
    private List<HealthAbnormalTrendItem> healthAbnormalTrend;
    
    /**
     * 护工工作量分布
     */
    private List<CaregiverWorkloadItem> caregiverWorkload;
    
    /**
     * 健康异常率趋势项
     */
    @Data
    public static class HealthAbnormalTrendItem {
        /**
         * 日期
         */
        private String date;
        
        /**
         * 异常率
         */
        private BigDecimal rate;
        
        public HealthAbnormalTrendItem(String date, BigDecimal rate) {
            this.date = date;
            this.rate = rate;
        }
    }
    
    /**
     * 护工工作量项
     */
    @Data
    public static class CaregiverWorkloadItem {
        /**
         * 护工姓名
         */
        private String name;
        
        /**
         * 订单数量
         */
        private Integer count;
        
        public CaregiverWorkloadItem(String name, Integer count) {
            this.name = name;
            this.count = count;
        }
    }
}
