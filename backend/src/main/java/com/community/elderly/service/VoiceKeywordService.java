package com.community.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.elderly.entity.VoiceKeyword;
import java.util.List;

public interface VoiceKeywordService extends IService<VoiceKeyword> {
    List<VoiceKeyword> getEnabledKeywords();
    boolean addKeyword(VoiceKeyword keyword);
    boolean removeKeyword(Long id);
    boolean updateKeyword(VoiceKeyword keyword);
}
