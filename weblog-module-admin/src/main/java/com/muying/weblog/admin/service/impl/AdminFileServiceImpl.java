package com.muying.weblog.admin.service.impl;

import com.muying.weblog.admin.model.vo.file.UploadFileRspVO;
import com.muying.weblog.admin.service.AdminFileService;
import com.muying.weblog.admin.strategy.FileStrategy;
import com.muying.weblog.common.enums.ResponseCodeEnum;
import com.muying.weblog.common.exception.BizException;
import com.muying.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {

    @Resource
    private FileStrategy fileStrategy;
    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            // 上传文件
            String url = fileStrategy.uploadFile(file);

            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> 上传文件 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }
}
