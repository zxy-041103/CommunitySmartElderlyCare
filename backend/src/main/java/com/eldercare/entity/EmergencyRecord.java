package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("emergency_record")
public class EmergencyRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long elderlyId;
    private String emergencyType;
    private String description;
    private String location;
    private String status;
    private Long responderId;
    private LocalDateTime responseTime;
    private LocalDateTime resolveTime;
    private String resolveNote;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String elderlyName;
    @TableField(exist = false)
    private String elderlyPhone;
    @TableField(exist = false)
    private String responderName;
}
