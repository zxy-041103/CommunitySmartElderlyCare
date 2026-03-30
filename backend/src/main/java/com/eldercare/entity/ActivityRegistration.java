package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activity_registration")
public class ActivityRegistration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long userId;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String activityTitle;
    @TableField(exist = false)
    private String userName;
}
