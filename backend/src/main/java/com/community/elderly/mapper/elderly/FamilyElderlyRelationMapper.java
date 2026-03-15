package com.community.elderly.mapper.elderly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.entity.FamilyElderlyRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FamilyElderlyRelationMapper extends BaseMapper<FamilyElderlyRelation> {
    
    @Select("SELECT fer.*, u.real_name as familyName, u.phone as familyPhone FROM family_elderly_relation fer LEFT JOIN sys_user u ON fer.family_id = u.id WHERE fer.elderly_id = #{elderlyId} AND fer.audit_status = 'approved' AND fer.is_deleted = 0")
    List<com.community.elderly.dto.elderly.FamilyMemberDTO> getApprovedFamilyMembers(@Param("elderlyId") Long elderlyId);
}