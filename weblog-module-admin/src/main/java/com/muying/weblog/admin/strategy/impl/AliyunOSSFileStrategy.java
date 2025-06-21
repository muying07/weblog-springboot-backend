package com.muying.weblog.admin.strategy.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.muying.weblog.admin.config.AliyunOSSProperties;
import com.muying.weblog.admin.strategy.FileStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.UUID;
/**
 * @description: 阿里云 OSS 文件上传策略
 **/
@Slf4j
public class AliyunOSSFileStrategy implements FileStrategy {

    @Resource
    private AliyunOSSProperties aliyunOSSProperties;

    @Resource
    private OSS ossClient;

    @Override
    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        log.info("## 上传文件至阿里云 OSS ...");

        if (file == null || file.isEmpty()) { // 使用isEmpty()更简洁
            log.error("==> 上传文件异常：文件为空");
            throw new RuntimeException("文件不能为空");
        }

        String originalFileName = file.getOriginalFilename();
        String key = UUID.randomUUID().toString().replace("-", "");
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String objectName = key + suffix; // 更简洁的拼接方式

        log.info("==> 开始上传文件至阿里云 OSS, ObjectName: {}", objectName);

        // 修复点：使用传统方式读取字节流
        try (InputStream inputStream = file.getInputStream()) {
            // 创建元数据设置文件大小
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());

            ossClient.putObject(
                    aliyunOSSProperties.getBucketName(),
                    objectName,
                    inputStream, // 直接传递InputStream
                    metadata    // 添加元数据
            );
        } // try-with-resources自动关闭流

        // 构造访问URL
        String url = String.format("https://%s.%s/%s",
                aliyunOSSProperties.getBucketName(),
                aliyunOSSProperties.getEndpoint(),
                objectName
        );
        log.info("==> 上传成功，访问路径: {}", url);
        return url;
    }
}
