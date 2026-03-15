package com.community.elderly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.elderly.entity.QuickPhraseTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 快捷话术模板Mapper接口
 */
@Mapper
public interface QuickPhraseTemplateMapper extends BaseMapper<QuickPhraseTemplate> {
    
    /**
     * 根据分类查询话术模板
     * @param category 分类
     * @return 话术模板列表
     */
    @Select("SELECT * FROM quick_phrase_template " +
            "WHERE category = #{category} AND is_enabled = 1 " +
            "ORDER BY sort ASC")
    List<QuickPhraseTemplate> selectByCategory(@Param("category") String category);
    
    /**
     * 查询所有启用的话术模板
     * @return 话术模板列表
     */
    @Select("SELECT * FROM quick_phrase_template " +
            "WHERE is_enabled = 1 " +
            "ORDER BY category ASC, sort ASC")
    List<QuickPhraseTemplate> selectAllEnabled();
}
