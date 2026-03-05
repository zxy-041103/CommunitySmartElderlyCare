package com.community.elderly.utils;

import org.junit.jupiter.api.Test;

public class AesUtilBatchGenerateTest {

    @Test
    public void testBatchGenerateEncryptedPhone() throws Exception {
        AesUtil aesUtil = new AesUtil();
        String key = "elderlycare2024!";
        
        // 用户手机号映射
        String[][] users = {
            {"1", "admin", "13800138000"},
            {"2", "elderly001", "13800138002"},
            {"3", "elderly002", "13800138003"},
            {"4", "elderly003", "13800138004"},
            {"5", "elderly004", "13800138005"},
            {"6", "elderly005", "13800138006"},
            {"7", "family001", "13800138007"},
            {"8", "family002", "13800138008"},
            {"9", "caregiver001", "13800138009"},
            {"10", "caregiver002", "13800138010"}
        };
        
        System.out.println("开始批量生成加密手机号：");
        System.out.println("=============================================");
        
        for (String[] user : users) {
            String userId = user[0];
            String username = user[1];
            String originalPhone = user[2];
            
            // 加密
            String encrypted = aesUtil.encrypt(originalPhone, key);
            
            // 解密（测试）
            String decrypted = aesUtil.decrypt(encrypted, key);
            
            System.out.println("用户ID: " + userId);
            System.out.println("用户名: " + username);
            System.out.println("原始手机号: " + originalPhone);
            System.out.println("加密后: " + encrypted);
            System.out.println("解密后: " + decrypted);
            System.out.println("=============================================");
        }
        
        System.out.println("批量生成完成，请更新data.sql文件中的phone字段。");
    }
}
