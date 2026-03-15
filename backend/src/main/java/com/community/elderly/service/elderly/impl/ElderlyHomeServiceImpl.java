package com.community.elderly.service.elderly.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.dto.elderly.FamilyMemberDTO;
import com.community.elderly.entity.Announcement;
import com.community.elderly.entity.FamilyElderlyRelation;
import com.community.elderly.entity.HealthData;
import com.community.elderly.mapper.admin.AnnouncementMapper;
import com.community.elderly.mapper.elderly.ElderlyHealthMapper;
import com.community.elderly.mapper.elderly.FamilyElderlyRelationMapper;
import com.community.elderly.service.elderly.ElderlyHomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ElderlyHomeServiceImpl implements ElderlyHomeService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private FamilyElderlyRelationMapper familyElderlyRelationMapper;

    @Autowired
    private ElderlyHealthMapper elderlyHealthMapper;

    @Override
    public List<Announcement> getAnnouncements() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getIsPublished, 1);
        wrapper.eq(Announcement::getTargetRole, "elderly");
        wrapper.orderByDesc(Announcement::getPublishTime);
        wrapper.last("LIMIT 10");
        return announcementMapper.selectList(wrapper);
    }

    @Override
    public List<FamilyMemberDTO> getFamilyMembers(Long elderlyId) {
        return familyElderlyRelationMapper.getApprovedFamilyMembers(elderlyId);
    }

    @Override
    public Map<String, Object> getHealthOverview(Long elderlyId) {
        Map<String, Object> overview = new HashMap<>();
        
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getUserId, elderlyId);
        // 只查询当天及之前的数据，不包括未来日期
        wrapper.le(HealthData::getMonitorTime, LocalDateTime.now());
        wrapper.orderByDesc(HealthData::getMonitorTime);
        wrapper.last("LIMIT 1");
        HealthData latestHealth = elderlyHealthMapper.selectOne(wrapper);
        
        if (latestHealth != null) {
            overview.put("bloodPressure", latestHealth.getSystolicPressure() + "/" + latestHealth.getDiastolicPressure());
            overview.put("heartRate", latestHealth.getHeartRate());
            overview.put("bloodSugar", latestHealth.getBloodSugar());
            overview.put("temperature", latestHealth.getTemperature());
            overview.put("weight", latestHealth.getWeight());
            overview.put("monitorTime", latestHealth.getMonitorTime());
        } else {
            overview.put("bloodPressure", "-");
            overview.put("heartRate", "-");
            overview.put("bloodSugar", "-");
            overview.put("temperature", "-");
            overview.put("weight", "-");
            overview.put("monitorTime", "-");
        }
        
        return overview;
    }

    @Override
    public Map<String, Object> getHealthTrend(Long elderlyId, Integer days) {
        Map<String, Object> trend = new HashMap<>();
        
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        
        List<HealthData> healthDataList = elderlyHealthMapper.selectHealthDataByUserIdAndTimeRange(
            elderlyId, startTime, endTime
        );
        
        List<String> dates = new ArrayList<>();
        List<Double> bloodPressures = new ArrayList<>();
        List<Double> bloodSugars = new ArrayList<>();
        List<Double> heartRates = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (HealthData data : healthDataList) {
            dates.add(data.getMonitorTime().format(formatter));
            if (data.getSystolicPressure() != null) {
                bloodPressures.add(data.getSystolicPressure());
            }
            if (data.getBloodSugar() != null) {
                bloodSugars.add(data.getBloodSugar());
            }
            if (data.getHeartRate() != null) {
                heartRates.add(data.getHeartRate());
            }
        }
        
        trend.put("dates", dates);
        trend.put("bloodPressure", bloodPressures);
        trend.put("bloodSugar", bloodSugars);
        trend.put("heartRate", heartRates);
        
        return trend;
    }
}