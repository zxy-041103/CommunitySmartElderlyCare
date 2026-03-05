package com.community.elderly.service.elderly;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.elderly.dto.elderly.CreateEmergencyHelpRequest;
import com.community.elderly.dto.elderly.UpdateHelpStatusRequest;
import com.community.elderly.entity.EmergencyHelp;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface EmergencyHelpService extends IService<EmergencyHelp> {

    /**
     * 一键求助提交
     * 事务要求：保存求助记录和发送通知必须在同一事务中完成
     *
     * @param userId 用户ID（老人）
     * @param request 求助请求
     * @return 求助记录ID
     */
    Long submitEmergencyHelp(Long userId, CreateEmergencyHelpRequest request);

    /**
     * 求助单分配（就近护工）
     * 根据老人位置和护工位置分配最近的可用护工
     *
     * @param helpId 求助ID
     * @return 分配的护工信息
     */
    Map<String, Object> assignCaregiver(Long helpId);

    /**
     * 自动分配所有待处理求助单
     * 系统定时任务调用
     *
     * @return 分配的求助单数量
     */
    int assignAllPendingHelp();

    /**
     * 更新求助状态
     *
     * @param helpId 求助ID
     * @param handlerId 处理人ID（护工）
     * @param request 状态更新请求
     * @return 是否成功
     */
    boolean updateHelpStatus(Long helpId, Long handlerId, UpdateHelpStatusRequest request);

    /**
     * 查询求助记录
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页求助记录
     */
    IPage<EmergencyHelp> getHelpRecords(Long userId, Integer page, Integer size);

    /**
     * 查询所有求助记录（管理员用）
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页求助记录
     */
    IPage<EmergencyHelp> getAllHelpRecords(Integer page, Integer size);

    /**
     * 查询待处理的求助记录
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页求助记录
     */
    IPage<EmergencyHelp> getPendingHelpRecords(Integer page, Integer size);

    /**
     * 查询我处理的求助记录（护工用）
     *
     * @param handlerId 处理人ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页求助记录
     */
    IPage<EmergencyHelp> getHelpRecordsByHandler(Long handlerId, Integer page, Integer size);

    /**
     * 获取求助详情
     *
     * @param helpId 求助ID
     * @return 求助详情
     */
    EmergencyHelp getHelpDetail(Long helpId);

    /**
     * 获取紧急求助统计
     *
     * @return 统计信息
     */
    Map<String, Object> getHelpStatistics();

    /**
     * 获取紧急求助列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param name 用户名
     * @param type 求助类型
     * @param status 求助状态
     * @return 求助记录列表
     */
    Map<String, Object> getHelpList(Integer page, Integer size, String name, String type, String status);

    /**
     * 获取紧急求助趋势
     *
     * @param days 天数
     * @return 趋势数据
     */
    Map<String, Object> getHelpTrend(Integer days);
}
