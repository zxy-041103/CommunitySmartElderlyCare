package com.community.elderly.controller;

import com.community.elderly.common.Result;
import com.community.elderly.entity.QuickPhraseTemplate;
import com.community.elderly.service.QuickPhraseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 快捷话术模板控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/quick-phrases")
@RequiredArgsConstructor
@Api(tags = "快捷话术管理")
public class QuickPhraseController {
    
    private final QuickPhraseService quickPhraseService;
    
    /**
     * 根据分类获取话术模板
     * @param category 分类：daily-日常报平安，sick-身体不适，need-生活需求，emotion-情感关怀，emergency-紧急情况
     * @return 话术模板列表
     */
    @GetMapping("/category/{category}")
    @ApiOperation(value = "根据分类获取话术模板", notes = "获取指定分类的快捷话术模板列表")
    public Result<List<QuickPhraseTemplate>> getPhrasesByCategory(@PathVariable String category) {
        try {
            List<QuickPhraseTemplate> phrases = quickPhraseService.getPhrasesByCategory(category);
            return Result.success(phrases);
        } catch (Exception e) {
            log.error("获取话术模板失败", e);
            return Result.error("获取话术模板失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有话术模板（按分类分组）
     * @return 分组后的话术模板
     */
    @GetMapping("/all")
    @ApiOperation(value = "获取所有话术模板", notes = "获取所有快捷话术模板，按分类分组返回")
    public Result<Map<String, List<QuickPhraseTemplate>>> getAllPhrases() {
        try {
            Map<String, List<QuickPhraseTemplate>> phrases = quickPhraseService.getAllPhrasesGrouped();
            return Result.success(phrases);
        } catch (Exception e) {
            log.error("获取话术模板失败", e);
            return Result.error("获取话术模板失败: " + e.getMessage());
        }
    }
}
