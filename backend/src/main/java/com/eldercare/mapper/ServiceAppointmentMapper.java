package com.eldercare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.entity.ServiceAppointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceAppointmentMapper extends BaseMapper<ServiceAppointment> {
}
