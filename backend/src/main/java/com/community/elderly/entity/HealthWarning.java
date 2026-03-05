package com.community.elderly.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthWarning {
    private Long id;
    private Long healthDataId;
    private Long userId;
    private String warningType; // 预警类型：bloodPressure, heartRate, bloodSugar, temperature
    private String warningLevel; // 预警级别：low, medium, high
    private String warningMessage; // 预警信息
    private Integer notified; // 是否已通知：0-未通知，1-已通知
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
