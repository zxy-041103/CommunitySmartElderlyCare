package com.eldercare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
