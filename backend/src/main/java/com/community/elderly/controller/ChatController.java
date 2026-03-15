package com.community.elderly.controller;

import com.community.elderly.common.Result;
import com.community.elderly.entity.ChatMessage;
import com.community.elderly.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天控制器
 * 处理实时聊天消息和聊天记录查询
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Api(tags = "聊天管理")
public class ChatController {
    
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * WebSocket发送消息
     * @param message 消息对象
     */
    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage message) {
        log.info("收到WebSocket消息: senderId={}, receiverId={}", message.getSenderId(), message.getReceiverId());
        
        // 保存消息到数据库
        ChatMessage savedMessage = chatService.sendMessage(message);
        
        // 发送给接收者
        String destination = "/topic/messages/" + message.getReceiverType() + "/" + message.getReceiverId();
        messagingTemplate.convertAndSend(destination, savedMessage);
        
        // 发送回发送者（确认消息已发送）
        String senderDestination = "/topic/messages/" + message.getSenderType() + "/" + message.getSenderId();
        messagingTemplate.convertAndSend(senderDestination, savedMessage);
        
        log.info("消息已转发: destination={}", destination);
    }
    
    /**
     * 获取聊天记录
     * @param familyId 家属ID
     * @param authentication 当前用户认证信息
     * @return 聊天记录列表
     */
    @GetMapping("/history/{familyId}")
    @ApiOperation(value = "获取聊天记录", notes = "获取与指定家属的聊天记录")
    public Result<List<ChatMessage>> getChatHistory(
            @PathVariable Long familyId,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            
            List<ChatMessage> messages = chatService.getChatHistory(
                    elderlyId, "elderly", familyId, "family");
            
            return Result.success(messages);
        } catch (Exception e) {
            log.error("获取聊天记录失败", e);
            return Result.error("获取聊天记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     * @param familyId 家属ID
     * @param authentication 当前用户认证信息
     * @return 操作结果
     */
    @PutMapping("/read/{familyId}")
    @ApiOperation(value = "标记消息已读", notes = "将与指定家属的聊天记录标记为已读")
    public Result<Void> markAsRead(
            @PathVariable Long familyId,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            
            chatService.markAsRead(familyId, "family", elderlyId, "elderly");
            
            return Result.success();
        } catch (Exception e) {
            log.error("标记消息已读失败", e);
            return Result.error("标记消息已读失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数量
     * @param authentication 当前用户认证信息
     * @return 未读消息数量
     */
    @GetMapping("/unread/count")
    @ApiOperation(value = "获取未读消息数量", notes = "获取当前用户的未读消息数量")
    public Result<Integer> getUnreadCount(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            String userType = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("elderly");
            
            int count = chatService.getUnreadCount(userId, userType);
            
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取未读消息数量失败", e);
            return Result.error("获取未读消息数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空聊天记录
     * @param familyId 家属ID
     * @param authentication 当前用户认证信息
     * @return 操作结果
     */
    @DeleteMapping("/history/{familyId}")
    @ApiOperation(value = "清空聊天记录", notes = "清空与指定家属的聊天记录")
    public Result<Void> clearChatHistory(
            @PathVariable Long familyId,
            Authentication authentication) {
        try {
            Long elderlyId = Long.parseLong(authentication.getName());
            
            chatService.clearChatHistory(elderlyId, "elderly", familyId, "family");
            
            return Result.success();
        } catch (Exception e) {
            log.error("清空聊天记录失败", e);
            return Result.error("清空聊天记录失败: " + e.getMessage());
        }
    }
}
