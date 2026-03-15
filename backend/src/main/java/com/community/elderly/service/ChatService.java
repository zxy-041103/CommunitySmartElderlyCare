package com.community.elderly.service;

import com.community.elderly.entity.ChatMessage;

import java.util.List;

/**
 * 聊天服务接口
 */
public interface ChatService {
    
    /**
     * 发送消息
     * @param message 消息对象
     * @return 发送后的消息
     */
    ChatMessage sendMessage(ChatMessage message);
    
    /**
     * 获取聊天记录
     * @param userId1 用户1ID
     * @param userType1 用户1类型
     * @param userId2 用户2ID
     * @param userType2 用户2类型
     * @return 聊天记录列表
     */
    List<ChatMessage> getChatHistory(Long userId1, String userType1, Long userId2, String userType2);
    
    /**
     * 标记消息为已读
     * @param senderId 发送者ID
     * @param senderType 发送者类型
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     */
    void markAsRead(Long senderId, String senderType, Long receiverId, String receiverType);
    
    /**
     * 获取未读消息数量
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 未读消息数量
     */
    int getUnreadCount(Long receiverId, String receiverType);
    
    /**
     * 清空聊天记录
     * @param userId1 用户1ID
     * @param userType1 用户1类型
     * @param userId2 用户2ID
     * @param userType2 用户2类型
     */
    void clearChatHistory(Long userId1, String userType1, Long userId2, String userType2);
}
