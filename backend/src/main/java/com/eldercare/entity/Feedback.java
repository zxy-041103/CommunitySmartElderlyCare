package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private String content;
    private String image;
    private String reply;
    private Long replyBy;
    private LocalDateTime replyTime;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String replyByName;
}
