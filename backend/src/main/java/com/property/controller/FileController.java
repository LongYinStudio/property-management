package com.property.controller;

import com.property.common.Result;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload-path:${user.home}/property-uploads}")
    private String uploadPath;

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        // 确保上传目录存在
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return uploadImageToDirectory(file, "avatar", 2, "图片大小不能超过 2MB");
    }

    /**
     * 上传业务图片
     */
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadImageToDirectory(file, "image", 5, "图片大小不能超过 5MB");
    }

    private Result<Map<String, String>> uploadImageToDirectory(
            MultipartFile file,
            String directory,
            int maxSizeMb,
            String sizeErrorMessage
    ) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        if (file.getSize() > (long) maxSizeMb * 1024 * 1024) {
            return Result.error(sizeErrorMessage);
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            Path dirPath = Paths.get(uploadPath, directory, datePath);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = dirPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            String url = baseUrl + "/uploads/" + directory + "/" + datePath + "/" + filename;
            Map<String, String> result = new HashMap<>();
            result.put("url", url);

            return Result.success("上传成功", result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
