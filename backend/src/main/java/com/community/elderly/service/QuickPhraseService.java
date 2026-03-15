package com.community.elderly.service;

import com.community.elderly.entity.QuickPhraseTemplate;

import java.util.List;
import java.util.Map;

/**
 * 快捷话术服务接口
 */
public interface QuickPhraseService {
    
    /**
     * 根据分类获取话术模板
     * @param category 分类
     * @return 话术模板列表
     */
    List<QuickPhraseTemplate> getPhrasesByCategory(String category);
    
    /**
     * 获取所有话术模板（按分类分组）
     * @return 分组后的话术模板
     */
    Map<String, List<QuickPhraseTemplate>> getAllPhrasesGrouped();
    
    /**
     * 初始化默认话术模板
     */
    void initDefaultPhrases();
}
