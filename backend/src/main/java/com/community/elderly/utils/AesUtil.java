package com.community.elderly.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class AesUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String CHARSET = StandardCharsets.UTF_8.name();

    private static final String DEFAULT_KEY = "elderlycare2024!";

    public String encrypt(String content) throws Exception {
        return encrypt(content, DEFAULT_KEY);
    }

    public String encrypt(String content, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new IllegalArgumentException("Key length must be 16 characters");
        }
        byte[] raw = key.getBytes(CHARSET);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec iv = new IvParameterSpec(raw);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes(CHARSET));
        return Base64.encodeBase64String(encrypted);
    }

    public String decrypt(String content, String key) {
        if (key == null || key.length() != 16) {
            System.out.println("解密失败：key 为 null 或长度不为 16");
            return content;
        }
        try {
            System.out.println("开始解密，密文：" + content);
            System.out.println("密文长度：" + content.length());
            byte[] raw = key.getBytes(CHARSET);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec iv = new IvParameterSpec(raw);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            
            byte[] encrypted;
            
            // 检查是否是十六进制字符串
            if (isHexString(content)) {
                System.out.println("使用十六进制解码");
                encrypted = hexStringToByteArray(content);
            } else {
                System.out.println("使用Base64解码");
                encrypted = Base64.decodeBase64(content);
            }
            
            System.out.println("解码后长度：" + encrypted.length);
            
            byte[] original = cipher.doFinal(encrypted);
            String result = new String(original, CHARSET);
            System.out.println("解密成功，明文：" + result);
            return result;
        } catch (Exception e) {
            System.out.println("解密失败：" + e.getMessage());
            e.printStackTrace();
            return content;
        }
    }
    
    // 检查字符串是否是十六进制格式
    private boolean isHexString(String s) {
        if (s == null) {
            return false;
        }
        int len = s.length();
        if (len % 2 != 0) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }
    
    // 将十六进制字符串转换为字节数组
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public String decrypt(String content) {
        return decrypt(content, DEFAULT_KEY);
    }

    public static String encryptPassword(String password) {
        try {
            AesUtil aesUtil = new AesUtil();
            return aesUtil.encrypt(password);
        } catch (Exception e) {
            return password;
        }
    }

    public static String decryptPassword(String encryptedPassword) {
        AesUtil aesUtil = new AesUtil();
        return aesUtil.decrypt(encryptedPassword);
    }
}
