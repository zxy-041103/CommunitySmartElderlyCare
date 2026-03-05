package com.community.elderly.utils;

import org.junit.jupiter.api.Test;

public class AesUtilGenerateTest {

    @Test
    public void testGenerateEncryptedPhone() throws Exception {
        AesUtil aesUtil = new AesUtil();
        String originalPhone = "13800138000";
        String key = "elderlycare2024!";
        
        System.out.println("原始手机号: " + originalPhone);
        
        // 加密（Base64格式）
        String encryptedBase64 = aesUtil.encrypt(originalPhone, key);
        System.out.println("Base64加密后: " + encryptedBase64);
        System.out.println("Base64加密后长度: " + encryptedBase64.length());
        
        // 解密（测试）
        String decryptedBase64 = aesUtil.decrypt(encryptedBase64, key);
        System.out.println("Base64解密后: " + decryptedBase64);
        
        // 生成十六进制格式的加密手机号（模拟数据库格式）
        System.out.println("\n请将数据库中的手机号更新为以下Base64格式的加密值:");
        System.out.println("加密值: " + encryptedBase64);
        System.out.println("对应的原始手机号: " + originalPhone);
    }
}
