package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("health_data")
public class HealthData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long elderlyId;
    private Integer heartRate;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private BigDecimal bloodSugar;
    private BigDecimal bodyTemperature;
    private Integer bloodOxygen;
    private LocalDateTime recordTime;
    private Long recorderId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String elderlyName;
    @TableField(exist = false)
    private String recorderName;
}
