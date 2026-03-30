package com.eldercare.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("service_category")
public class ServiceCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String icon;
    private String description;
    private Integer sortOrder;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
