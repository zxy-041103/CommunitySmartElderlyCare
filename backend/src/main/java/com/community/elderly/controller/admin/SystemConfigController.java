package com.community.elderly.controller.admin;

import com.community.elderly.common.Result;
import com.community.elderly.dto.admin.BackupConfigRequest;
import com.community.elderly.dto.admin.VoiceKeywordRequest;
import com.community.elderly.entity.BackupConfig;
import com.community.elderly.entity.VoiceKeyword;
import com.community.elderly.service.BackupConfigService;
import com.community.elderly.service.VoiceKeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/system/config")
public class SystemConfigController {

    @Autowired
    private BackupConfigService backupConfigService;

    @Autowired
    private VoiceKeywordService voiceKeywordService;

    @GetMapping("/backup")
    public Result<?> getBackupConfig() {
        BackupConfig config = backupConfigService.getActiveConfig();
        return Result.success(config);
    }

    @PostMapping("/backup")
    public Result<?> updateBackupConfig(@RequestBody BackupConfigRequest request) {
        BackupConfig config = new BackupConfig();
        config.setId(request.getId());
        config.setBackupFrequency(request.getBackupFrequency());
        config.setBackupTime(request.getBackupTime());
        config.setBackupPath(request.getBackupPath());
        config.setRetentionDays(request.getRetentionDays());
        config.setIsEnabled(request.getIsEnabled());
        
        boolean success = backupConfigService.updateConfig(config);
        return success ? Result.success("备份配置更新成功") : Result.error("备份配置更新失败");
    }

    @PostMapping("/backup/manual")
    public Result<?> manualBackup() {
        boolean success = backupConfigService.manualBackup();
        return success ? Result.success("手动备份成功") : Result.error("手动备份失败");
    }

    @GetMapping("/voice/keywords")
    public Result<?> getVoiceKeywords() {
        List<VoiceKeyword> keywords = voiceKeywordService.getEnabledKeywords();
        return Result.success(keywords);
    }

    @PostMapping("/voice/keyword")
    public Result<?> addVoiceKeyword(@RequestBody VoiceKeywordRequest request) {
        VoiceKeyword keyword = new VoiceKeyword();
        keyword.setKeyword(request.getKeyword());
        keyword.setActionType(request.getActionType());
        keyword.setActionParams(request.getActionParams());
        keyword.setSort(request.getSort());
        keyword.setIsEnabled(request.getIsEnabled());
        
        boolean success = voiceKeywordService.addKeyword(keyword);
        return success ? Result.success("关键词添加成功") : Result.error("关键词添加失败");
    }

    @DeleteMapping("/voice/keyword/{id}")
    public Result<?> removeVoiceKeyword(@PathVariable Long id) {
        boolean success = voiceKeywordService.removeKeyword(id);
        return success ? Result.success("关键词删除成功") : Result.error("关键词删除失败");
    }

    @PutMapping("/voice/keyword")
    public Result<?> updateVoiceKeyword(@RequestBody VoiceKeywordRequest request) {
        VoiceKeyword keyword = new VoiceKeyword();
        keyword.setId(request.getId());
        keyword.setKeyword(request.getKeyword());
        keyword.setActionType(request.getActionType());
        keyword.setActionParams(request.getActionParams());
        keyword.setSort(request.getSort());
        keyword.setIsEnabled(request.getIsEnabled());
        
        boolean success = voiceKeywordService.updateKeyword(keyword);
        return success ? Result.success("关键词更新成功") : Result.error("关键词更新失败");
    }
}
