package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String image;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Long organizerId;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String organizerName;
    @TableField(exist = false)
    private Boolean registered;
}
