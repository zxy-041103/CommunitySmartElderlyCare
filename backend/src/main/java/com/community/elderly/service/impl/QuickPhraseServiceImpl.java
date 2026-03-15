package com.community.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.elderly.entity.QuickPhraseTemplate;
import com.community.elderly.mapper.QuickPhraseTemplateMapper;
import com.community.elderly.service.QuickPhraseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 快捷话术服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuickPhraseServiceImpl implements QuickPhraseService {
    
    private final QuickPhraseTemplateMapper quickPhraseTemplateMapper;
    
    @Override
    public List<QuickPhraseTemplate> getPhrasesByCategory(String category) {
        return quickPhraseTemplateMapper.selectByCategory(category);
    }
    
    @Override
    public Map<String, List<QuickPhraseTemplate>> getAllPhrasesGrouped() {
        List<QuickPhraseTemplate> allPhrases = quickPhraseTemplateMapper.selectAllEnabled();
        return allPhrases.stream().collect(Collectors.groupingBy(QuickPhraseTemplate::getCategory));
    }
    
    @Override
    @Transactional
    @PostConstruct
    public void initDefaultPhrases() {
        // 检查是否已有数据
        Long count = quickPhraseTemplateMapper.selectCount(null);
        if (count > 0) {
            log.info("快捷话术模板已存在，跳过初始化");
            return;
        }
        
        log.info("开始初始化默认快捷话术模板");
        
        List<QuickPhraseTemplate> phrases = new ArrayList<>();
        
        // 日常报平安
        phrases.add(createPhrase("daily", "我现在一切都好，在家休息呢，你不用惦记", 1));
        phrases.add(createPhrase("daily", "今天天气挺好，我出去走了走，身体挺舒服", 2));
        phrases.add(createPhrase("daily", "晚饭吃过啦，吃得很饱，你早点休息", 3));
        
        // 身体不适
        phrases.add(createPhrase("sick", "我今天有点头晕/胸闷，现在在家坐着，你方便的话赶紧回来看看我", 1));
        phrases.add(createPhrase("sick", "我感觉浑身不舒服，已经量过体温了，你给我回个电话吧", 2));
        phrases.add(createPhrase("sick", "我刚才不小心摔了一跤，现在坐在地上，没法起来，你快来帮我一下", 3));
        
        // 生活需求
        phrases.add(createPhrase("need", "家里的米/盐用完了，你下次回来帮我带一点哦", 1));
        phrases.add(createPhrase("need", "我想明天去医院复查，能不能陪我一起去呀", 2));
        phrases.add(createPhrase("need", "这个手机/电视我不会操作了，你有空的时候教我一下", 3));
        
        // 情感关怀
        phrases.add(createPhrase("emotion", "今天看到你小时候的照片，特别想你，有空多回家看看", 1));
        phrases.add(createPhrase("emotion", "外面降温了，你出门记得多穿件衣服，别感冒了", 2));
        phrases.add(createPhrase("emotion", "谢谢你一直照顾我，有你在我特别安心", 3));
        
        // 紧急情况
        phrases.add(createPhrase("emergency", "我现在特别不舒服，已经打了120，你赶紧来医院找我", 1));
        phrases.add(createPhrase("emergency", "家里有陌生人敲门，我很害怕，你快给我回电话", 2));
        phrases.add(createPhrase("emergency", "我忘带钥匙了，现在在门口，你赶紧送钥匙过来", 3));
        
        for (QuickPhraseTemplate phrase : phrases) {
            quickPhraseTemplateMapper.insert(phrase);
        }
        
        log.info("默认快捷话术模板初始化完成，共{}条", phrases.size());
    }
    
    private QuickPhraseTemplate createPhrase(String category, String content, int sort) {
        QuickPhraseTemplate phrase = new QuickPhraseTemplate();
        phrase.setCategory(category);
        phrase.setContent(content);
        phrase.setSort(sort);
        phrase.setIsEnabled(1);
        phrase.setCreateTime(LocalDateTime.now());
        phrase.setUpdateTime(LocalDateTime.now());
        return phrase;
    }
}
