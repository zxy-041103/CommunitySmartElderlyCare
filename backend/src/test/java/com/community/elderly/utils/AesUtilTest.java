package com.community.elderly.utils;

import org.junit.jupiter.api.Test;

public class AesUtilTest {

    @Test
    public void testEncryptDecrypt() throws Exception {
        AesUtil aesUtil = new AesUtil();
        String originalPhone = "13800138000";
        String key = "elderlycare2024!";
        
        System.out.println("原始手机号: " + originalPhone);
        
        // 加密
        String encrypted = aesUtil.encrypt(originalPhone, key);
        System.out.println("加密后: " + encrypted);
        System.out.println("加密后长度: " + encrypted.length());
        
        // 解密
        String decrypted = aesUtil.decrypt(encrypted, key);
        System.out.println("解密后: " + decrypted);
        
        // 测试数据库中的加密手机号
        String dbEncryptedPhone = "270757EC2262401B736F4AEA4773C3DE";
        System.out.println("\n数据库中的加密手机号: " + dbEncryptedPhone);
        System.out.println("数据库中加密后长度: " + dbEncryptedPhone.length());
        
        // 尝试解密数据库中的手机号
        String decryptedDbPhone = aesUtil.decrypt(dbEncryptedPhone, key);
        System.out.println("数据库中手机号解密后: " + decryptedDbPhone);
    }
}
