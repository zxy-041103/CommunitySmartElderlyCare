package com.community.elderly.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class VerificationCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final Random random = new Random();

    /**
     * 生成服务核销码
     * 算法说明：
     * 1. 使用时间戳（14位）+ 随机字符（8位）组合，总长度22位
     * 2. 时间戳确保唯一性，格式：yyyyMMddHHmmss
     * 3. 随机字符从预定义字符集中选择，避免使用易混淆字符（0/O, 1/I/L）
     * 4. 加入订单ID哈希值进一步增强唯一性
     *
     * @param orderId 订单ID
     * @return 核销码
     */
    public String generateVerificationCode(Long orderId) {
        StringBuilder code = new StringBuilder();

        String timestamp = LocalDateTime.now().format(FORMATTER);
        code.append(timestamp);

        String randomPart = generateRandomPart();
        code.append(randomPart);

        int orderIdHash = Math.abs(orderId.hashCode() % 10000);
        String hashPart = String.format("%04d", orderIdHash);
        code.append(hashPart);

        return code.toString();
    }

    /**
     * 生成8位随机字符
     * 使用预定义字符集，避免易混淆字符
     */
    private String generateRandomPart() {
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            randomPart.append(CHARACTERS.charAt(index));
        }
        return randomPart.toString();
    }

    /**
     * 验证核销码格式
     * 格式：yyyyMMddHHmmss(14位) + 8位随机字符 + 4位哈希值 = 26位
     *
     * @param code 待验证的核销码
     * @return 格式是否正确
     */
    public boolean validateVerificationCodeFormat(String code) {
        if (code == null || code.length() != 26) {
            return false;
        }

        try {
            String timestampPart = code.substring(0, 14);
            LocalDateTime.parse(timestampPart, FORMATTER);

            String randomPart = code.substring(14, 22);
            for (char c : randomPart.toCharArray()) {
                if (CHARACTERS.indexOf(c) < 0) {
                    return false;
                }
            }

            String hashPart = code.substring(22);
            Integer.parseInt(hashPart);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从核销码中提取时间戳
     *
     * @param code 核销码
     * @return 时间戳字符串
     */
    public String extractTimestamp(String code) {
        if (code != null && code.length() >= 14) {
            return code.substring(0, 14);
        }
        return null;
    }

    /**
     * 生成短格式核销码（用于二维码展示）
     * 算法：Base32编码的订单ID + 随机校验位
     *
     * @param orderId 订单ID
     * @return 短核销码（10位）
     */
    public String generateShortCode(Long orderId) {
        StringBuilder code = new StringBuilder();

        String encodedOrderId = encodeOrderId(orderId);
        code.append(encodedOrderId);

        String checkDigit = generateCheckDigit(code.toString());
        code.append(checkDigit);

        return code.toString();
    }

    /**
     * 编码订单ID为Base32格式
     */
    private String encodeOrderId(Long orderId) {
        String base32Chars = "0123456789ABCDEFGHJKMNPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();
        long n = orderId;

        if (n == 0) {
            return "0";
        }

        while (n > 0) {
            int digit = (int) (n % 32);
            result.insert(0, base32Chars.charAt(digit));
            n /= 32;
        }

        while (result.length() < 6) {
            result.insert(0, "0");
        }

        return result.substring(0, 6);
    }

    /**
     * 生成校验位
     */
    private String generateCheckDigit(String data) {
        int sum = 0;
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            sum += (c * (i + 1));
        }
        return String.valueOf((char) ('A' + (sum % 26)));
    }

    /**
     * 验证短格式核销码
     *
     * @param shortCode 短核销码
     * @return 是否有效
     */
    public boolean validateShortCode(String shortCode) {
        if (shortCode == null || shortCode.length() != 7) {
            return false;
        }

        String data = shortCode.substring(0, 6);
        String checkDigit = shortCode.substring(6);

        return checkDigit.equals(generateCheckDigit(data));
    }

    /**
     * 从短格式核销码解码订单ID
     *
     * @param shortCode 短核销码
     * @return 订单ID，解析失败返回-1
     */
    public Long decodeOrderIdFromShortCode(String shortCode) {
        if (!validateShortCode(shortCode)) {
            return -1L;
        }

        try {
            String encodedPart = shortCode.substring(0, 6);
            String base32Chars = "0123456789ABCDEFGHJKMNPQRSTUVWXYZ";

            long result = 0;
            for (char c : encodedPart.toCharArray()) {
                result = result * 32 + base32Chars.indexOf(c);
            }

            return result;
        } catch (Exception e) {
            return -1L;
        }
    }
}
