package com.community.elderly.controller.common;

import com.community.elderly.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/file")
@Api(tags = "文件上传接口")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    @ApiOperation(value = "上传文件", notes = "上传文件到服务器")
    public Result<List<String>> uploadFiles(
            @ApiParam("文件列表") @RequestParam("files") MultipartFile[] files) {
        
        List<String> fileUrls = new ArrayList<>();
        
        // 创建上传目录
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fullPath = uploadPath + File.separator + datePath;
        File directory = new File(fullPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    // 生成唯一文件名
                    String originalFilename = file.getOriginalFilename();
                    String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
                    String fileName = UUID.randomUUID() + extension;
                    
                    // 保存文件
                    File dest = new File(fullPath + File.separator + fileName);
                    file.transferTo(dest);
                    
                    // 生成文件URL
                    String fileUrl = "/uploads/" + datePath + "/" + fileName;
                    fileUrls.add(fileUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Result.error("文件上传失败");
                }
            }
        }
        
        return Result.success(fileUrls);
    }
}
