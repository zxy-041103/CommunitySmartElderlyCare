package com.community.elderly.mapper.elderly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.elderly.entity.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ElderlyBookingMapper extends BaseMapper<ServiceOrder> {

    @Select("SELECT * FROM service_order WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<ServiceOrder> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM service_order WHERE caregiver_id = #{caregiverId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<ServiceOrder> selectByCaregiverId(@Param("caregiverId") Long caregiverId);

    @Select("SELECT * FROM service_order WHERE order_no = #{orderNo} AND is_deleted = 0")
    ServiceOrder selectByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM service_order WHERE user_id = #{userId} AND order_status = #{status} AND is_deleted = 0 ORDER BY create_time DESC")
    List<ServiceOrder> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Select("SELECT * FROM service_order WHERE service_date >= #{startDate} AND service_date <= #{endDate} AND is_deleted = 0 ORDER BY create_time DESC")
    List<ServiceOrder> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    IPage<ServiceOrder> selectPageByUserId(Page<ServiceOrder> page, @Param("userId") Long userId);

    IPage<ServiceOrder> selectPageByCaregiverId(Page<ServiceOrder> page, @Param("caregiverId") Long caregiverId);
}
