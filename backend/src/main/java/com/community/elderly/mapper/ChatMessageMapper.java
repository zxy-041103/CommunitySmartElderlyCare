package com.community.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    /**
     * 查询两个用户之间的聊天记录
     * @param userId1 用户1ID
     * @param userType1 用户1类型
     * @param userId2 用户2ID
     * @param userType2 用户2类型
     * @return 聊天记录列表
     */
    @Select("SELECT * FROM chat_message " +
            "WHERE ((sender_id = #{userId1} AND sender_type = #{userType1} AND receiver_id = #{userId2} AND receiver_type = #{userType2}) " +
            "OR (sender_id = #{userId2} AND sender_type = #{userType2} AND receiver_id = #{userId1} AND receiver_type = #{userType1})) " +
            "AND is_deleted = 0 " +
            "ORDER BY create_time DESC")
    List<ChatMessage> selectChatHistory(@Param("userId1") Long userId1, 
                                        @Param("userType1") String userType1,
                                        @Param("userId2") Long userId2, 
                                        @Param("userType2") String userType2);
    
    /**
     * 标记消息为已读
     * @param senderId 发送者ID
     * @param senderType 发送者类型
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 更新的记录数
     */
    @Update("UPDATE chat_message SET is_read = 1, read_time = NOW() " +
            "WHERE sender_id = #{senderId} AND sender_type = #{senderType} " +
            "AND receiver_id = #{receiverId} AND receiver_type = #{receiverType} " +
            "AND is_read = 0 AND is_deleted = 0")
    int markAsRead(@Param("senderId") Long senderId, 
                   @Param("senderType") String senderType,
                   @Param("receiverId") Long receiverId, 
                   @Param("receiverType") String receiverType);
    
    /**
     * 查询未读消息数量
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 未读消息数量
     */
    @Select("SELECT COUNT(*) FROM chat_message " +
            "WHERE receiver_id = #{receiverId} AND receiver_type = #{receiverType} " +
            "AND is_read = 0 AND is_deleted = 0")
    int selectUnreadCount(@Param("receiverId") Long receiverId, 
                          @Param("receiverType") String receiverType);
}
