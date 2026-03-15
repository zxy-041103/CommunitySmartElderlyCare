package com.community.elderly.mapper.elderly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.elderly.dto.elderly.EmergencyHelpDTO;
import com.community.elderly.entity.EmergencyHelp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ElderlyEmergencyMapper extends BaseMapper<EmergencyHelp> {

    @Select("SELECT * FROM emergency_help WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<EmergencyHelp> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM emergency_help WHERE handler_id = #{handlerId} ORDER BY create_time DESC")
    List<EmergencyHelp> selectByHandlerId(@Param("handlerId") Long handlerId);

    @Select("SELECT * FROM emergency_help WHERE help_status = #{status} ORDER BY create_time ASC")
    List<EmergencyHelp> selectByStatus(@Param("status") String status);

    @Select("SELECT * FROM emergency_help WHERE help_status = #{status} ORDER BY create_time ASC LIMIT #{limit}")
    List<EmergencyHelp> selectPendingHelp(@Param("status") String status, @Param("limit") Integer limit);

    IPage<EmergencyHelp> selectPageByUserId(Page<EmergencyHelp> page, @Param("userId") Long userId);

    IPage<EmergencyHelp> selectPageByHandlerId(Page<EmergencyHelp> page, @Param("handlerId") Long handlerId);

    @Select("SELECT eh.*, u.real_name as elderlyName, h.real_name as handlerName FROM emergency_help eh LEFT JOIN sys_user u ON eh.user_id = u.id LEFT JOIN sys_user h ON eh.handler_id = h.id WHERE (u.real_name LIKE CONCAT('%', #{name}, '%') OR #{name} IS NULL) AND (eh.help_type = #{type} OR #{type} IS NULL) AND (eh.help_status = #{status} OR #{status} IS NULL) ORDER BY eh.create_time DESC LIMIT #{offset}, #{size}")
    List<EmergencyHelp> selectHelpList(@Param("offset") Integer offset, @Param("size") Integer size, @Param("name") String name, @Param("type") String type, @Param("status") String status);

    @Select("SELECT COUNT(*) FROM emergency_help eh LEFT JOIN sys_user u ON eh.user_id = u.id WHERE (u.username LIKE CONCAT('%', #{name}, '%') OR #{name} IS NULL) AND (eh.help_type = #{type} OR #{type} IS NULL) AND (eh.help_status = #{status} OR #{status} IS NULL)")
    long selectHelpCount(@Param("name") String name, @Param("type") String type, @Param("status") String status);

    @Select("SELECT * FROM emergency_help WHERE create_time >= #{startTime} AND create_time <= #{endTime} ORDER BY create_time ASC")
    List<EmergencyHelp> selectHelpByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // 查询求助列表（包含老人姓名和处理人姓名）
    @Select("SELECT eh.*, u.real_name as elderlyName, h.real_name as handlerName FROM emergency_help eh LEFT JOIN sys_user u ON eh.user_id = u.id LEFT JOIN sys_user h ON eh.handler_id = h.id WHERE (u.real_name LIKE CONCAT('%', #{name}, '%') OR #{name} IS NULL OR #{name} = '') AND (eh.help_type = #{type} OR #{type} IS NULL OR #{type} = '') AND (eh.help_status = #{status} OR #{status} IS NULL OR #{status} = '') ORDER BY eh.create_time DESC LIMIT #{offset}, #{size}")
    List<EmergencyHelpDTO> selectHelpListWithNames(@Param("offset") Integer offset, @Param("size") Integer size, @Param("name") String name, @Param("type") String type, @Param("status") String status);

    @Select("SELECT COUNT(*) FROM emergency_help eh LEFT JOIN sys_user u ON eh.user_id = u.id WHERE (u.real_name LIKE CONCAT('%', #{name}, '%') OR #{name} IS NULL OR #{name} = '') AND (eh.help_type = #{type} OR #{type} IS NULL OR #{type} = '') AND (eh.help_status = #{status} OR #{status} IS NULL OR #{status} = '')")
    long selectHelpCountWithNames(@Param("name") String name, @Param("type") String type, @Param("status") String status);
}
