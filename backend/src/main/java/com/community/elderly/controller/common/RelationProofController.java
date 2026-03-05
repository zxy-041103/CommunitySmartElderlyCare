package com.community.elderly.controller.common;

import com.community.elderly.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/relation-proof")
@Api(tags = "关系审核材料上传接口")
public class RelationProofController {

    // 前端图片存储路径
    private static final String FRONTEND_IMAGE_PATH = "D:/CommunitySmartElderlyCareMonitoringandManagementPlatform/frontend/src/assets/images/relation";

    @PostMapping("/upload")
    @ApiOperation(value = "上传关系审核材料", notes = "上传关系审核材料到前端assets/images/relation目录，文件名为关联记录ID")
    public Result<List<String>> uploadProofMaterials(
            @ApiParam("关联关系ID") @RequestParam("relationId") Long relationId,
            @ApiParam("文件列表") @RequestParam("files") MultipartFile[] files,
            HttpServletRequest request) {
        
        List<String> fileUrls = new ArrayList<>();
        
        if (files == null || files.length == 0) {
            return Result.error("请选择要上传的文件");
        }
        
        // 创建上传目录
        File directory = new File(FRONTEND_IMAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        int fileIndex = 0;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    // 生成文件名：relationId_index.extension
                    String originalFilename = file.getOriginalFilename();
                    String extension = ".jpg";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    
                    // 如果有多个文件，使用 relationId_0.jpg, relationId_1.jpg 等命名
                    String fileName;
                    if (files.length == 1) {
                        fileName = relationId + extension;
                    } else {
                        fileName = relationId + "_" + fileIndex + extension;
                    }
                    
                    // 保存文件
                    File dest = new File(FRONTEND_IMAGE_PATH + File.separator + fileName);
                    file.transferTo(dest);
                    
                    // 生成文件URL（相对于前端assets目录的路径）
                    String fileUrl = "/src/assets/images/relation/" + fileName;
                    fileUrls.add(fileUrl);
                    
                    fileIndex++;
                } catch (IOException e) {
                    e.printStackTrace();
                    return Result.error("文件上传失败: " + e.getMessage());
                }
            }
        }
        
        return Result.success(fileUrls);
    }
}
