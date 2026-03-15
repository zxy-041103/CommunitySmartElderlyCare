package com.community.elderly.mapper.elderly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.entity.HealthData;
import com.community.elderly.entity.HealthWarning;
import com.community.elderly.entity.HealthWarningThreshold;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ElderlyHealthMapper extends BaseMapper<HealthData> {
    // 健康数据相关
    int insertHealthData(HealthData healthData);
    HealthData selectHealthDataById(Long id);
    List<HealthData> selectHealthDataByUserId(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
    int updateHealthData(HealthData healthData);
    int deleteHealthDataById(Long id);
    int countHealthDataByUserId(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 健康预警相关
    int insertHealthWarning(HealthWarning healthWarning);
    List<HealthWarning> selectHealthWarningsByUserId(Long userId);
    
    // 健康预警阈值相关
    int insertHealthWarningThreshold(HealthWarningThreshold threshold);
    int updateHealthWarningThreshold(HealthWarningThreshold threshold);
    List<HealthWarningThreshold> selectActiveHealthWarningThresholds();
    HealthWarningThreshold selectHealthWarningThresholdByType(String indicatorType);

    // 统计相关（定义在 XML 中）
    long selectTotalCount();
    long selectAbnormalCount();
    long selectMonthlyAbnormalCount();
    long selectTodayCount();

    // 分页查询相关
    List<HealthData> selectHealthDataList(@Param("offset") Integer offset, @Param("size") Integer size, @Param("name") String name, @Param("status") String status, @Param("startDate") String startDate, @Param("endDate") String endDate);

    long selectHealthDataCount(@Param("name") String name, @Param("status") String status, @Param("startDate") String startDate, @Param("endDate") String endDate);

    // 趋势数据相关
    @Select("SELECT * FROM health_data WHERE user_id = #{userId} AND monitor_time >= #{startTime} AND monitor_time <= #{endTime} ORDER BY monitor_time ASC")
    List<HealthData> selectHealthDataByUserIdAndTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // 查询所有用户的健康数据（按时间范围）
    @Select("SELECT * FROM health_data WHERE monitor_time >= #{startTime} AND monitor_time <= #{endTime} ORDER BY monitor_time ASC")
    List<HealthData> selectAllHealthDataByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
