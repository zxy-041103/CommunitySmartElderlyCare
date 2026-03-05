package com.community.elderly.dto.admin;

import lombok.Data;

@Data
public class BackupConfigRequest {
    private Long id;
    private String backupFrequency;
    private String backupTime;
    private String backupPath;
    private Integer retentionDays;
    private Integer isEnabled;
}
