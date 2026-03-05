package com.community.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.elderly.entity.BackupConfig;

public interface BackupConfigService extends IService<BackupConfig> {
    BackupConfig getActiveConfig();
    boolean updateConfig(BackupConfig config);
    boolean manualBackup();
}
