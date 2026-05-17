package com.middle.wcs.order.controller;

import com.middle.wcs.hander.ResponseResult;
import com.middle.wcs.hander.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/file")
@Slf4j
public class FileUploadController {

    private static final String MOBILE_UPLOAD_DIR = "D://wcs_temp_data/mobile-upload-log/";

    @ApiOperation("移动端上传日志文件到服务器")
    @PostMapping("/mobileUpload")
    public ResponseResult<Map<String, Object>> mobileUploadLog(
            @ApiParam(value = "日志文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "车间标识") @RequestParam(value = "workshop", defaultValue = "2800") String workshop) {

        try {
            String originalName = file.getOriginalFilename();
            log.info("移动端日志上传 - 文件名: {}, 车间: {}", originalName, workshop);

            // 确保目录存在
            Path uploadPath = Paths.get(MOBILE_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String cleanName = Paths.get(originalName).getFileName().toString();
            String savedFileName = workshop + "车间_" + timestamp + "_" + cleanName;
            Path targetPath = Paths.get(MOBILE_UPLOAD_DIR + savedFileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("移动端日志上传成功 - 保存路径: {}", targetPath);

            Map<String, Object> result = new HashMap<>();
            result.put("savedName", savedFileName);
            result.put("savedPath", targetPath.toString());
            return ResponseResult.build(result, ResultCodeEnum.SUCCESS.getCode(), "上传成功");

        } catch (Exception e) {
            log.error("移动端日志上传失败: {}", e.getMessage(), e);
            return ResponseResult.build(null, ResultCodeEnum.FAIL.getCode(), "上传失败: " + e.getMessage());
        }
    }
}
