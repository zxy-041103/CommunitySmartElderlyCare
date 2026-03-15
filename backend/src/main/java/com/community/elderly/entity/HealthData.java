package com.community.elderly.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthData {
    private Long id;
    private Long userId;
    
    @TableField(exist = false) // 数据库表中不存在该字段
    private String elderlyName; // 老人姓名，用于关联查询显示
    
    private Double systolicPressure; // 收缩压
    private Double diastolicPressure; // 舒张压
    private Double heartRate; // 心率
    private Double bloodSugar; // 血糖
    private Double weight; // 体重
    private Double height; // 身高
    private Double temperature; // 体温
    private Double oxygenSaturation; // 血氧饱和度
    private String healthStatus; // 健康状态：normal-正常，abnormal-异常
    private LocalDateTime monitorTime; // 监测时间
    private String description; // 描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted; // 逻辑删除字段：0-未删除，1-已删除
}
