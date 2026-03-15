package com.community.elderly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthWarningThreshold {
    private Long id;
    private String indicatorType; // 指标类型：systolicPressure, diastolicPressure, heartRate, bloodSugar, temperature
    private Double minValue; // 最小值阈值
    private Double maxValue; // 最大值阈值
    private Integer isActive; // 是否激活：0-未激活，1-已激活
    
    @JsonIgnore
    private LocalDateTime createTime;
    
    @JsonIgnore
    private LocalDateTime updateTime;
}
