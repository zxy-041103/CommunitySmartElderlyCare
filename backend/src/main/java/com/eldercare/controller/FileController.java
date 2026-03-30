package com.eldercare.controller;

import com.eldercare.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir.getAbsolutePath() + File.separator + newFilename));
            String url = "/uploads/" + newFilename;
            return Result.success("上传成功", url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
