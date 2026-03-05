package com.community.elderly.utils;

import cn.hutool.captcha.CircleCaptcha;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class CaptchaService {

    private static final Map<String, String> captchaCache = new HashMap<>();

    private static final int CAPTCHA_WIDTH = 120;
    private static final int CAPTCHA_HEIGHT = 40;
    private static final int CAPTCHA_CODE_COUNT = 4;

    public static Map<String, Object> generateCaptcha() {
        CircleCaptcha captcha = cn.hutool.captcha.CaptchaUtil.createCircleCaptcha(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, CAPTCHA_CODE_COUNT, 20);
        String code = captcha.getCode();
        String captchaId = UUID.randomUUID().toString();

        captchaCache.put(captchaId, code);

        Map<String, Object> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("image", captcha.getImageBase64());

        return result;
    }

    public static boolean verifyCaptcha(String captchaId, String code) {
        if (captchaId == null || code == null) {
            return false;
        }

        String cachedCode = captchaCache.get(captchaId);
        if (cachedCode == null) {
            return false;
        }

        boolean result = cachedCode.equalsIgnoreCase(code);
        if (result) {
            captchaCache.remove(captchaId);
        }

        return result;
    }
}
