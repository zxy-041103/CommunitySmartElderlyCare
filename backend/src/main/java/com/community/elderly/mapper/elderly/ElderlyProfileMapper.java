package com.community.elderly.mapper.elderly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ElderlyProfileMapper extends BaseMapper<User> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND is_deleted = 0")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND is_deleted = 0")
    User selectByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM sys_user WHERE id_card = #{idCard} AND is_deleted = 0")
    User selectByIdCard(@Param("idCard") String idCard);
}
