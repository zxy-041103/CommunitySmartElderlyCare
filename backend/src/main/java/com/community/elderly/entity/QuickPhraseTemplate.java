package com.community.elderly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 快捷话术模板实体类
 */
@Data
@TableName("quick_phrase_template")
public class QuickPhraseTemplate {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类：daily-日常报平安，sick-身体不适，need-生活需求，emotion-情感关怀，emergency-紧急情况
     */
    private String category;
    
    /**
     * 话术内容
     */
    private String content;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否启用：1-是，0-否
     */
    private Integer isEnabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
