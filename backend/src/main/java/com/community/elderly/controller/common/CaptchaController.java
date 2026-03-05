package com.community.elderly.controller.common;

import com.community.elderly.common.Result;
import com.community.elderly.utils.CaptchaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Api(tags = "验证码管理")
public class CaptchaController {

    @GetMapping("/captcha")
    @ApiOperation(value = "获取验证码", notes = "生成图形验证码，返回验证码ID和Base64图片")
    public Result<Map<String, Object>> getCaptcha() {
        Map<String, Object> captcha = CaptchaService.generateCaptcha();
        return Result.success(captcha);
    }
}
