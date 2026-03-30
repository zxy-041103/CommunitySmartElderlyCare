package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("work_schedule")
public class WorkSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long caregiverId;
    private LocalDate workDate;
    private String shiftType;
    private String status;
    private String note;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String caregiverName;
}
