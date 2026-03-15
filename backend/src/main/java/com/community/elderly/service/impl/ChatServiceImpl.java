package com.community.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.community.elderly.entity.ChatMessage;
import com.community.elderly.mapper.ChatMessageMapper;
import com.community.elderly.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    
    private final ChatMessageMapper chatMessageMapper;
    
    @Override
    @Transactional
    public ChatMessage sendMessage(ChatMessage message) {
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setIsDeleted(0);
        
        chatMessageMapper.insert(message);
        log.info("消息发送成功: senderId={}, receiverId={}", message.getSenderId(), message.getReceiverId());
        
        return message;
    }
    
    @Override
    public List<ChatMessage> getChatHistory(Long userId1, String userType1, Long userId2, String userType2) {
        return chatMessageMapper.selectChatHistory(userId1, userType1, userId2, userType2);
    }
    
    @Override
    @Transactional
    public void markAsRead(Long senderId, String senderType, Long receiverId, String receiverType) {
        int count = chatMessageMapper.markAsRead(senderId, senderType, receiverId, receiverType);
        log.info("标记消息已读: senderId={}, receiverId={}, count={}", senderId, receiverId, count);
    }
    
    @Override
    public int getUnreadCount(Long receiverId, String receiverType) {
        return chatMessageMapper.selectUnreadCount(receiverId, receiverType);
    }
    
    @Override
    @Transactional
    public void clearChatHistory(Long userId1, String userType1, Long userId2, String userType2) {
        LambdaUpdateWrapper<ChatMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.and(w -> w.and(w1 -> w1.eq(ChatMessage::getSenderId, userId1)
                        .eq(ChatMessage::getSenderType, userType1)
                        .eq(ChatMessage::getReceiverId, userId2)
                        .eq(ChatMessage::getReceiverType, userType2))
                .or(w2 -> w2.eq(ChatMessage::getSenderId, userId2)
                        .eq(ChatMessage::getSenderType, userType2)
                        .eq(ChatMessage::getReceiverId, userId1)
                        .eq(ChatMessage::getReceiverType, userType1)))
                .set(ChatMessage::getIsDeleted, 1)
                .set(ChatMessage::getUpdateTime, LocalDateTime.now());
        
        chatMessageMapper.update(null, wrapper);
        log.info("清空聊天记录: userId1={}, userId2={}", userId1, userId2);
    }
}
