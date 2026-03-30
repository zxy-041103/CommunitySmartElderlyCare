package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("service_record")
public class ServiceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private Long caregiverId;
    private Long elderlyId;
    private String serviceContent;
    private LocalDateTime serviceTime;
    private Integer duration;
    private String note;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String caregiverName;
    @TableField(exist = false)
    private String elderlyName;
}
