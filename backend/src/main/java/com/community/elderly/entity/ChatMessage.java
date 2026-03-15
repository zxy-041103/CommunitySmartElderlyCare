package com.community.elderly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
@Data
@TableName("chat_message")
public class ChatMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 发送者类型：elderly-老人，family-家属
     */
    private String senderType;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 接收者类型：elderly-老人，family-家属
     */
    private String receiverType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：text-文字，voice-语音
     */
    private String messageType;
    
    /**
     * 是否已读：1-是，0-否
     */
    private Integer isRead;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除：1-是，0-否
     */
    private Integer isDeleted;
}
