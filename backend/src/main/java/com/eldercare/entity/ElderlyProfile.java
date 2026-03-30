package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("elderly_profile")
public class ElderlyProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String emergencyContact;
    private String emergencyPhone;
    private String bloodType;
    private String medicalHistory;
    private String allergyHistory;
    private String livingCondition;
    private String disabilityLevel;
    private String photo;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userPhone;
    @TableField(exist = false)
    private Integer userAge;
    @TableField(exist = false)
    private String userGender;
    @TableField(exist = false)
    private String userAddress;
}
