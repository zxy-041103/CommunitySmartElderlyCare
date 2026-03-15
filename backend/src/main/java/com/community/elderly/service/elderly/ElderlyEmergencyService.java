package com.community.elderly.service.elderly;

import java.util.Map;

public interface ElderlyEmergencyService {
    
    /**
     * 获取老人的紧急求助列表
     */
    Map<String, Object> getEmergencyList(Long elderlyId, Integer pageNum, Integer pageSize, 
                                          String type, String status, String startTime, String endTime);
    
    /**
     * 创建紧急求助
     */
    void createEmergency(Long elderlyId, String type, String description);
    
    /**
     * 取消紧急求助
     */
    void cancelEmergency(Long elderlyId, Long emergencyId);
}
