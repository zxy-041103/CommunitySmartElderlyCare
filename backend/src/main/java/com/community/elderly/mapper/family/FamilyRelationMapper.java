package com.community.elderly.mapper.family;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.entity.FamilyElderlyRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FamilyRelationMapper extends BaseMapper<FamilyElderlyRelation> {

    @Select("SELECT * FROM family_elderly_relation WHERE family_id = #{familyId} AND is_deleted = 0")
    List<FamilyElderlyRelation> selectByFamilyId(@Param("familyId") Long familyId);

    @Select("SELECT * FROM family_elderly_relation WHERE elderly_id = #{elderlyId} AND is_deleted = 0")
    List<FamilyElderlyRelation> selectByElderlyId(@Param("elderlyId") Long elderlyId);

    @Select("SELECT * FROM family_elderly_relation WHERE family_id = #{familyId} AND elderly_id = #{elderlyId} AND is_deleted = 0")
    FamilyElderlyRelation selectByFamilyAndElderly(@Param("familyId") Long familyId, @Param("elderlyId") Long elderlyId);

    @Update("UPDATE family_elderly_relation SET audit_status = #{auditStatus}, audit_remark = #{auditRemark}, auditor_id = #{auditorId}, audit_time = NOW() WHERE id = #{id}")
    int updateAuditStatus(@Param("id") Long id, @Param("auditStatus") String auditStatus, @Param("auditRemark") String auditRemark, @Param("auditorId") Long auditorId);
}
