package com.muying.weblog.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * @desc: Minio 配置
 * @author: muying
 * @date: 2025/5/6 16:07
 * @version: 1.0
 */
@ConfigurationProperties(prefix = "storage.minio")
@Component
@Data
public class MinioProperties {
    /**
     * MinIO 服务器地址
     */
    private String endpoint;
    /**
     * MinIO 访问密钥
     */
    private String accessKey;
    /**
     * MinIO 密钥
     */
    private String secretKey;
    /**
     * MinIO 存储桶名称
     */
    private String bucketName;
}