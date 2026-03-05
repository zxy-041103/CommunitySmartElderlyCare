package com.community.elderly.service.elderly;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.elderly.dto.elderly.CreateEmergencyHelpRequest;
import com.community.elderly.dto.elderly.UpdateHelpStatusRequest;
import com.community.elderly.entity.EmergencyHelp;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

public interface ElderlyEmergencyService extends IService<EmergencyHelp> {

    /**
     * 提交紧急求助
     * 事务要求：创建求助记录、分配护工、发送通知必须在同一事务中完成
     *
     * @param userId 用户ID（老人）
     * @param request 求助请求
     * @return 求助记录ID
     */
    Long submitEmergencyHelp(Long userId, CreateEmergencyHelpRequest request);

    /**
     * 查询求助记录详情
     *
     * @param helpId 求助记录ID
     * @return 求助记录详情
     */
    EmergencyHelp getHelpDetail(Long helpId);

    /**
     * 更新求助状态
     *
     * @param helpId 求助记录ID
     * @param request 状态更新请求
     * @return 是否成功
     */
    boolean updateHelpStatus(Long helpId, UpdateHelpStatusRequest request);

    /**
     * 查询老人的求助记录
     *
     * @param userId 用户ID（老人）
     * @param page 页码
     * @param size 每页大小
     * @return 分页求助记录列表
     */
    IPage<EmergencyHelp> getHelpRecordsByUserId(Long userId, Integer page, Integer size);

    /**
     * 查询护工的待处理求助
     *
     * @param caregiverId 护工ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页求助记录列表
     */
    IPage<EmergencyHelp> getPendingHelpsForCaregiver(Long caregiverId, Integer page, Integer size);

    /**
     * 查询所有求助记录（管理员用）
     *
     * @param page 页码
     * @param size 每页大小
     * @param status 状态
     * @return 分页求助记录列表
     */
    IPage<EmergencyHelp> getAllHelpRecords(Integer page, Integer size, String status);

    /**
     * 获取紧急求助统计
     *
     * @return 统计数据
     */
    Map<String, Object> getEmergencyHelpStatistics();

    /**
     * 获取最近的求助记录
     *
     * @param limit 数量限制
     * @return 求助记录列表
     */
    List<EmergencyHelp> getRecentHelpRecords(int limit);

    /**
     * 分配护工处理求助
     *
     * @param helpId 求助记录ID
     * @param caregiverId 护工ID
     * @return 是否成功
     */
    boolean assignCaregiver(Long helpId, Long caregiverId);

    /**
     * 取消求助
     *
     * @param helpId 求助记录ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelHelp(Long helpId, Long userId);

    /**
     * 完成求助
     *
     * @param helpId 求助记录ID
     * @param caregiverId 护工ID
     * @param notes 处理备注
     * @return 是否成功
     */
    boolean completeHelp(Long helpId, Long caregiverId, String notes);
}