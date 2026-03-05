package com.community.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_backup_config")
public class BackupConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String backupFrequency; // 备份频率：daily-每天, weekly-每周, monthly-每月

    private String backupTime; // 备份时间：HH:mm格式

    private String backupPath; // 备份文件存储路径

    private Integer retentionDays; // 备份保留天数

    private Integer isEnabled; // 是否启用：0-未启用，1-已启用

    private String lastBackupTime; // 最后备份时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
