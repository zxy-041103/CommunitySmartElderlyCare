package com.community.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.elderly.entity.BackupConfig;
import com.community.elderly.mapper.admin.BackupConfigMapper;
import com.community.elderly.service.BackupConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BackupConfigServiceImpl extends ServiceImpl<BackupConfigMapper, BackupConfig> implements BackupConfigService {

    @Override
    public BackupConfig getActiveConfig() {
        LambdaQueryWrapper<BackupConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BackupConfig::getIsEnabled, 1);
        wrapper.orderByDesc(BackupConfig::getUpdateTime);
        wrapper.last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(BackupConfig config) {
        return this.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean manualBackup() {
        BackupConfig config = getActiveConfig();
        if (config == null) {
            return false;
        }
        
        config.setLastBackupTime(LocalDateTime.now().toString());
        return this.updateById(config);
    }
}
