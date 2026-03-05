package com.community.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.elderly.entity.VoiceKeyword;
import com.community.elderly.mapper.admin.VoiceKeywordMapper;
import com.community.elderly.service.VoiceKeywordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VoiceKeywordServiceImpl extends ServiceImpl<VoiceKeywordMapper, VoiceKeyword> implements VoiceKeywordService {

    @Override
    public List<VoiceKeyword> getEnabledKeywords() {
        LambdaQueryWrapper<VoiceKeyword> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoiceKeyword::getIsEnabled, 1);
        wrapper.orderByAsc(VoiceKeyword::getSort);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addKeyword(VoiceKeyword keyword) {
        return this.save(keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeKeyword(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateKeyword(VoiceKeyword keyword) {
        return this.updateById(keyword);
    }
}
