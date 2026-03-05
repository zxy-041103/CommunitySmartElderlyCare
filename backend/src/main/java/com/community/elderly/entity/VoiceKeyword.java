package com.community.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_voice_keyword")
public class VoiceKeyword {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String keyword; // 语音关键词

    private String actionType; // 触发的动作类型：health_report-健康报告, emergency-紧急求助, booking-预约服务, contact_family-联系家属等

    private String actionParams; // 动作参数（JSON格式）

    private Integer sort; // 排序

    private Integer isEnabled; // 是否启用：0-未启用，1-已启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
