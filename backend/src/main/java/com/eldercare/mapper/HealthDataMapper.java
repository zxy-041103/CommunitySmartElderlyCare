package com.eldercare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.entity.HealthData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {
}
