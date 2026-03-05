package com.community.elderly.mapper.caregiver;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.entity.ServiceEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceEvaluationMapper extends BaseMapper<ServiceEvaluation> {

    @Select("SELECT * FROM service_evaluation WHERE order_id = #{orderId} AND is_deleted = 0")
    ServiceEvaluation selectByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT * FROM service_evaluation WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<ServiceEvaluation> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM service_evaluation WHERE caregiver_id = #{caregiverId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<ServiceEvaluation> selectByCaregiverId(@Param("caregiverId") Long caregiverId);
}
