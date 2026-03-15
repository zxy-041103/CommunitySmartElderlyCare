package com.community.elderly.service.elderly;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.dto.elderly.FamilyMemberDTO;
import com.community.elderly.entity.Announcement;
import com.community.elderly.entity.FamilyElderlyRelation;
import com.community.elderly.entity.HealthData;

import java.util.List;
import java.util.Map;

public interface ElderlyHomeService {
    
    /**
     * 获取社区公告列表
     */
    List<Announcement> getAnnouncements();
    
    /**
     * 获取家属信息列表
     */
    List<FamilyMemberDTO> getFamilyMembers(Long elderlyId);
    
    /**
     * 获取健康概览数据
     */
    Map<String, Object> getHealthOverview(Long elderlyId);
    
    /**
     * 获取健康数据趋势
     */
    Map<String, Object> getHealthTrend(Long elderlyId, Integer days);
}