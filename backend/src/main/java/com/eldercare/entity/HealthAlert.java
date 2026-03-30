package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("health_alert")
public class HealthAlert {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long elderlyId;
    private String alertType;
    private String alertLevel;
    private String alertContent;
    private String status;
    private Long processorId;
    private LocalDateTime processTime;
    private String processNote;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String elderlyName;
    @TableField(exist = false)
    private String processorName;
}
