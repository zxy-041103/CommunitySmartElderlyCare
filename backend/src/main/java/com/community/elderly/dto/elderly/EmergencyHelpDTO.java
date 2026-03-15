package com.community.elderly.dto.elderly;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmergencyHelpDTO {
    private Long id;
    private Long userId;
    private String elderlyName; // 老人姓名
    private String helpType; // 求助类型
    private String urgency; // 紧急程度
    private String description; // 求助描述
    private String phone; // 联系电话
    private String location; // 位置信息
    private String helpStatus; // 处理状态
    private Long handlerId; // 处理人ID
    private String handlerName; // 处理人姓名
    private String handleResult; // 处理结果
    private LocalDateTime handleTime; // 处理时间
    private Integer isProcessed; // 是否已处理
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
