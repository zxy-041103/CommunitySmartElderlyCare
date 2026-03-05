package com.community.elderly.mapper.caregiver;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.entity.ServiceVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceVerificationMapper extends BaseMapper<ServiceVerification> {

    @Select("SELECT * FROM service_verification WHERE order_id = #{orderId} AND is_deleted = 0")
    ServiceVerification selectByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT * FROM service_verification WHERE verification_code = #{verificationCode} AND is_deleted = 0")
    ServiceVerification selectByVerificationCode(@Param("verificationCode") String verificationCode);

    @Select("SELECT * FROM service_verification WHERE verifier_id = #{verifierId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<ServiceVerification> selectByVerifierId(@Param("verifierId") Long verifierId);

    @Select("SELECT * FROM service_verification WHERE verification_status = #{status} AND is_deleted = 0 ORDER BY create_time ASC")
    List<ServiceVerification> selectByStatus(@Param("status") String status);
}
