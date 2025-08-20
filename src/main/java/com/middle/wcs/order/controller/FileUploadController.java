package com.middle.wcs.order.controller;

import com.middle.wcs.hander.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 用于处理移动端上传的日志文件
 * 支持单个文件和批量文件上传
 *
 * @author 文亮
 * @since 2024-12-30
 */

@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/file")
@Slf4j
public class FileUploadController {

    // 日志文件存储路径
    private static final String UPLOAD_DIR = "D://wcs_temp_data/upload_logs/";
    
    // 允许的文件类型
    private static final String[] ALLOWED_EXTENSIONS = {".txt", ".log"};
    
    // 最大文件大小 (50MB)
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    @ApiOperation("上传日志文件")
    @PostMapping("/upload")
    public ResponseResult<Map<String, Object>> uploadLogFile(
            @ApiParam(value = "日志文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "车间标识") @RequestParam(value = "workshop", defaultValue = "2800") String workshop,
            @ApiParam(value = "设备标识") @RequestParam(value = "deviceId", required = false) String deviceId) {
        
        try {
            log.info("接收到文件上传请求 - 文件名: {}, 车间: {}, 设备: {}", file.getOriginalFilename(), workshop, deviceId);
            
            // 文件验证
            ResponseResult<String> validationResult = validateFile(file);
            if (!validationResult.isSuccess()) {
                return ResponseResult.fail(validationResult.getCode(), validationResult.getMsg());
            }
            
            // 确保上传目录存在
            createUploadDirectories();
            
            // 生成文件保存路径
            String savedFilePath = generateFilePath(file.getOriginalFilename(), workshop, deviceId);
            
            // 保存文件
            Path targetPath = Paths.get(savedFilePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 记录上传信息
            Map<String, Object> result = new HashMap<>();
            result.put("originalName", file.getOriginalFilename());
            result.put("savedPath", savedFilePath);
            result.put("fileSize", file.getSize());
            result.put("workshop", workshop);
            result.put("deviceId", deviceId);
            result.put("uploadTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            log.info("文件上传成功 - 原文件名: {}, 保存路径: {}, 文件大小: {} bytes", 
                    file.getOriginalFilename(), savedFilePath, file.getSize());
            
            return ResponseResult.success(result, "文件上传成功");
            
        } catch (IOException e) {
            log.error("文件上传失败 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseResult.fail(500, "文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseResult.fail(500, "文件上传异常: " + e.getMessage());
        }
    }

    @ApiOperation("批量上传日志文件")
    @PostMapping("/batchUpload")
    public ResponseResult<Map<String, Object>> batchUploadLogFiles(
            @ApiParam(value = "日志文件列表", required = true) @RequestParam("files") MultipartFile[] files,
            @ApiParam(value = "车间标识") @RequestParam(value = "workshop", defaultValue = "2800") String workshop,
            @ApiParam(value = "设备标识") @RequestParam(value = "deviceId", required = false) String deviceId) {
        
        try {
            log.info("接收到批量文件上传请求 - 文件数量: {}, 车间: {}, 设备: {}", files.length, workshop, deviceId);
            
            if (files.length == 0) {
                return ResponseResult.fail(400, "没有选择要上传的文件");
            }
            
            if (files.length > 10) {
                return ResponseResult.fail(400, "单次最多只能上传10个文件");
            }
            
            // 确保上传目录存在
            createUploadDirectories();
            
            Map<String, Object> result = new HashMap<>();
            int successCount = 0;
            int failCount = 0;
            StringBuilder errorMessages = new StringBuilder();
            
            for (MultipartFile file : files) {
                try {
                    // 文件验证
                    ResponseResult<String> validationResult = validateFile(file);
                    if (!validationResult.isSuccess()) {
                        failCount++;
                        errorMessages.append(String.format("文件 %s: %s; ", 
                                file.getOriginalFilename(), validationResult.getMsg()));
                        continue;
                    }
                    
                    // 生成文件保存路径
                    String savedFilePath = generateFilePath(file.getOriginalFilename(), workshop, deviceId);
                    
                    // 保存文件
                    Path targetPath = Paths.get(savedFilePath);
                    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    
                    successCount++;
                    log.info("批量上传 - 文件上传成功: {}", file.getOriginalFilename());
                    
                } catch (Exception e) {
                    failCount++;
                    errorMessages.append(String.format("文件 %s: %s; ", 
                            file.getOriginalFilename(), e.getMessage()));
                    log.error("批量上传 - 文件上传失败: {}, 错误: {}", file.getOriginalFilename(), e.getMessage());
                }
            }
            
            result.put("totalCount", files.length);
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("workshop", workshop);
            result.put("deviceId", deviceId);
            result.put("uploadTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            if (failCount > 0) {
                result.put("errors", errorMessages.toString());
            }
            
            String message = String.format("批量上传完成 - 成功: %d个, 失败: %d个", successCount, failCount);
            log.info("批量文件上传完成 - {}", message);
            
            return ResponseResult.success(result, message);
            
        } catch (Exception e) {
            log.error("批量文件上传异常: {}", e.getMessage(), e);
            return ResponseResult.fail(500, "批量上传异常: " + e.getMessage());
        }
    }



    /**
     * 验证上传的文件
     */
    private ResponseResult<String> validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return ResponseResult.fail(400, "上传的文件不能为空");
        }
        
        // 检查文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return ResponseResult.fail(400, "文件名不能为空");
        }
        
        // 检查文件扩展名
        boolean validExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (originalFilename.toLowerCase().endsWith(ext)) {
                validExtension = true;
                break;
            }
        }
        if (!validExtension) {
            return ResponseResult.fail(400, "只允许上传 .txt 或 .log 格式的文件");
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseResult.fail(400, "文件大小不能超过50MB");
        }
        
        return ResponseResult.success("文件验证通过");
    }

    /**
     * 创建上传目录
     */
    private void createUploadDirectories() throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("创建上传目录: {}", UPLOAD_DIR);
        }
    }

    /**
     * 生成文件保存路径
     */
    private String generateFilePath(String originalFilename, String workshop, String deviceId) {
        // 生成时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        // 构建文件名
        StringBuilder fileName = new StringBuilder();
        fileName.append(workshop).append("车间_");
        
        if (deviceId != null && !deviceId.trim().isEmpty()) {
            fileName.append(deviceId).append("_");
        }
        
        fileName.append(timestamp).append("_");
        
        // 处理原始文件名，移除可能的路径
        String cleanOriginalName = Paths.get(originalFilename).getFileName().toString();
        fileName.append(cleanOriginalName);
        
        return UPLOAD_DIR + fileName.toString();
    }
}
