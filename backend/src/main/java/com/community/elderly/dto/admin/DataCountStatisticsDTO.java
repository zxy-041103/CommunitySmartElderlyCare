package com.community.elderly.dto.admin;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 数据统计详情页统计数据传输对象
 */
@Data
public class DataCountStatisticsDTO {
    
    // ========== 核心指标（前端展示用）==========
    
    /**
     * 健康监测覆盖率（前端字段名）
     */
    private String healthCoverage;
    
    /**
     * 健康监测覆盖率（兼容旧字段）
     */
    private String healthCoverageRate;
    
    /**
     * 健康监测覆盖率趋势
     */
    private Double healthCoverageTrend;
    
    /**
     * 服务核销率（前端字段名）
     */
    private String serviceVerification;
    
    /**
     * 服务核销率（兼容旧字段）
     */
    private String serviceVerificationRate;
    
    /**
     * 服务核销率趋势
     */
    private Double serviceVerificationTrend;
    
    /**
     * 紧急求助响应率（前端字段名）
     */
    private String emergencyResponse;
    
    /**
     * 紧急求助响应率（兼容旧字段）
     */
    private String emergencyResponseRate;
    
    /**
     * 紧急求助响应率趋势
     */
    private Double emergencyResponseTrend;
    
    /**
     * 老人满意度（前端字段名）
     */
    private String satisfaction;
    
    /**
     * 老人满意度（兼容旧字段）
     */
    private String elderlySatisfaction;
    
    /**
     * 老人满意度趋势
     */
    private Double satisfactionTrend;
    
    // ========== 数据概览 ==========
    
    /**
     * 总老人数
     */
    private Long totalElderly;
    
    /**
     * 总家属数
     */
    private Long totalFamily;
    
    /**
     * 总护工数
     */
    private Long totalCaregiver;
    
    /**
     * 本月服务数
     */
    private Long monthlyServices;
    
    /**
     * 本月求助数
     */
    private Long monthlyEmergencies;
    
    /**
     * 本月健康异常数
     */
    private Long monthlyAbnormalities;
    
    // ========== 图表数据 ==========
    
    /**
     * 健康异常率趋势（原始格式，兼容旧代码）
     */
    private List<HealthAbnormalTrendItem> healthAbnormalTrend;
    
    /**
     * 健康异常率趋势（前端格式）
     */
    private HealthTrend healthTrend;
    
    /**
     * 护工工作量分布（原始格式，兼容旧代码）
     */
    private List<CaregiverWorkloadItem> caregiverWorkload;
    
    /**
     * 护工工作量分布（前端格式）
     */
    private WorkloadDistribution workloadDistribution;
    
    /**
     * 服务类型占比
     */
    private List<ServiceTypeRatioItem> serviceTypeRatio;
    
    /**
     * 老人健康状态分布
     */
    private List<HealthStatusDistributionItem> healthStatusDistribution;
    
    // ========== 内部类定义 ==========
    
    /**
     * 健康异常率趋势（前端格式）
     */
    @Data
    public static class HealthTrend {
        private List<String> labels;
        private List<BigDecimal> values;
        
        public HealthTrend() {}
        
        public HealthTrend(List<String> labels, List<BigDecimal> values) {
            this.labels = labels;
            this.values = values;
        }
    }
    
    /**
     * 护工工作量分布（前端格式）
     */
    @Data
    public static class WorkloadDistribution {
        private List<String> names;
        private List<Integer> values;
        
        public WorkloadDistribution() {}
        
        public WorkloadDistribution(List<String> names, List<Integer> values) {
            this.names = names;
            this.values = values;
        }
    }
    
    /**
     * 健康异常率趋势项（原始格式）
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
        
        public HealthAbnormalTrendItem() {}
        
        public HealthAbnormalTrendItem(String date, BigDecimal rate) {
            this.date = date;
            this.rate = rate;
        }
    }
    
    /**
     * 护工工作量项（原始格式）
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
        
        public CaregiverWorkloadItem() {}
        
        public CaregiverWorkloadItem(String name, Integer count) {
            this.name = name;
            this.count = count;
        }
    }
    
    /**
     * 服务类型占比项
     */
    @Data
    public static class ServiceTypeRatioItem {
        /**
         * 服务类型名称
         */
        private String name;
        
        /**
         * 数量
         */
        private Integer value;
        
        public ServiceTypeRatioItem() {}
        
        public ServiceTypeRatioItem(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }
    
    /**
     * 老人健康状态分布项
     */
    @Data
    public static class HealthStatusDistributionItem {
        /**
         * 状态名称（normal/abnormal）
         */
        private String name;
        
        /**
         * 数量
         */
        private Integer value;
        
        public HealthStatusDistributionItem() {}
        
        public HealthStatusDistributionItem(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }
}