package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("caregiver_assignment")
public class CaregiverAssignment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long caregiverId;
    private Long elderlyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String caregiverName;
    @TableField(exist = false)
    private String elderlyName;
    @TableField(exist = false)
    private String caregiverPhone;
    @TableField(exist = false)
    private String elderlyPhone;
}
