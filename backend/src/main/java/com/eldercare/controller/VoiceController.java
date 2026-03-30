package com.eldercare.controller;

import com.eldercare.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 语音识别控制器 - 集成百度语音识别API
 * 接收前端录音数据，转发百度API进行语音识别，返回识别文本
 */
@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Value("${baidu.speech.api-key:}")
    private String apiKey;

    @Value("${baidu.speech.secret-key:}")
    private String secretKey;

    private String accessToken;
    private long tokenExpireTime;

    /**
     * 获取百度语音识别Access Token
     */
    private String getAccessToken() throws Exception {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }
        String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials"
                + "&client_id=" + apiKey + "&client_secret=" + secretKey;
        HttpURLConnection conn = (HttpURLConnection) new URL(tokenUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            sb.append(line);
        reader.close();
        String response = sb.toString();
        // 简单解析JSON获取access_token
        int start = response.indexOf("\"access_token\":\"") + 16;
        int end = response.indexOf("\"", start);
        accessToken = response.substring(start, end);
        tokenExpireTime = System.currentTimeMillis() + 29 * 24 * 3600 * 1000L; // 29天有效期
        return accessToken;
    }

    /**
     * 语音识别接口
     * 
     * @param params 包含 audio (base64编码的音频数据), format (音频格式, 默认pcm), rate (采样率,
     *               默认16000)
     * @return 识别结果文本
     */
    @PostMapping("/recognize")
    public Result<?> recognize(@RequestBody Map<String, Object> params) {
        String audio = (String) params.get("audio");
        String format = (String) params.getOrDefault("format", "pcm");
        int rate = params.get("rate") != null ? Integer.parseInt(params.get("rate").toString()) : 16000;

        if (audio == null || audio.isEmpty()) {
            return Result.error("音频数据不能为空");
        }

        // 检查是否配置了百度API
        if (apiKey == null || apiKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            return Result.error("百度语音API未配置，请使用浏览器语音识别");
        }

        try {
            String token = getAccessToken();
            byte[] audioBytes = java.util.Base64.getDecoder().decode(audio);
            String recognizeUrl = "https://vop.baidu.com/server_api";

            // 构建请求JSON
            String jsonBody = "{\"format\":\"" + format + "\","
                    + "\"rate\":" + rate + ","
                    + "\"channel\":1,"
                    + "\"cuid\":\"eldercare_platform\","
                    + "\"token\":\"" + token + "\","
                    + "\"speech\":\"" + audio + "\","
                    + "\"len\":" + audioBytes.length + "}";

            HttpURLConnection conn = (HttpURLConnection) new URL(recognizeUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);
            reader.close();

            String response = sb.toString();
            // 解析结果: {"err_no":0,"result":["识别的文本"]}
            if (response.contains("\"err_no\":0")) {
                int rStart = response.indexOf("\"result\":[\"") + 11;
                int rEnd = response.indexOf("\"]", rStart);
                String text = response.substring(rStart, rEnd);
                return Result.success("识别成功", text);
            } else {
                return Result.error("识别失败: " + response);
            }
        } catch (Exception e) {
            return Result.error("语音识别异常: " + e.getMessage());
        }
    }

    /**
     * 检查百度语音API配置状态
     */
    @GetMapping("/status")
    public Result<?> status() {
        boolean configured = apiKey != null && !apiKey.isEmpty()
                && secretKey != null && !secretKey.isEmpty();
        return Result.success(configured ? "configured" : "not_configured");
    }
}
