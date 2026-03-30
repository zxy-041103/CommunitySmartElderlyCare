package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("service_appointment")
public class ServiceAppointment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long elderlyId;
    private Long serviceItemId;
    private Long caregiverId;
    private LocalDateTime appointmentTime;
    private String status;
    private String note;
    private Integer rating;
    private String ratingContent;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String elderlyName;
    @TableField(exist = false)
    private String serviceItemName;
    @TableField(exist = false)
    private String caregiverName;
}
