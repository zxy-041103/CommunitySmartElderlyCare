package com.community.elderly.mapper.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据统计详情页Mapper接口
 */
@Mapper
public interface DataCountMapper {
    
    /**
     * 查询老人总数
     */
    Long selectElderlyCount();
    
    /**
     * 查询家属总数（role_type为family的用户）
     */
    Long selectFamilyCount();
    
    /**
     * 查询护工总数（role_type为caregiver的用户）
     */
    Long selectCaregiverCount();
    
    /**
     * 查询有健康数据的老人数
     */
    Long selectElderlyWithHealthDataCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询服务订单总数
     */
    Long selectTotalServiceOrderCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询已核销的服务订单数
     */
    Long selectVerifiedServiceOrderCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询紧急求助总数
     */
    Long selectTotalEmergencyCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询已响应的紧急求助数
     */
    Long selectRespondedEmergencyCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询服务评价平均分
     */
    BigDecimal selectAverageSatisfaction(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询指定时间范围内的健康异常数据数
     */
    Long selectAbnormalHealthDataCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询指定日期的健康数据总数
     */
    Long selectHealthDataCountByDate(@Param("date") LocalDate date);
    
    /**
     * 查询指定日期的异常健康数据数
     */
    Long selectAbnormalHealthDataCountByDate(@Param("date") LocalDate date);
    
    /**
     * 查询指定月份的健康数据总数
     */
    Long selectHealthDataCountByMonth(@Param("year") int year, @Param("month") int month);
    
    /**
     * 查询指定月份的异常健康数据数
     */
    Long selectAbnormalHealthDataCountByMonth(@Param("year") int year, @Param("month") int month);
    
    /**
     * 查询护工工作量分布
     */
    List<Map<String, Object>> selectCaregiverWorkload(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询服务类型占比
     */
    List<Map<String, Object>> selectServiceTypeRatio(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询老人健康状态分布
     */
    List<Map<String, Object>> selectHealthStatusDistribution(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}