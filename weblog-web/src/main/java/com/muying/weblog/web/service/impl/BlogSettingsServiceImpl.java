package com.muying.weblog.web.service.impl;


import com.muying.weblog.common.entity.BlogSettings;
import com.muying.weblog.common.mapper.BlogSettingsMapper;
import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.convert.BlogSettingsConvert;
import com.muying.weblog.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;
import com.muying.weblog.web.service.BlogSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 博客设置
 **/
@Service
@Slf4j
public class BlogSettingsServiceImpl implements BlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    /**
     * 获取博客设置信息
     *
     * @return
     */
    @Override
    public Response findDetail() {
        // 查询博客设置信息（约定的 ID 为 1）
        BlogSettings blogSettings = blogSettingsMapper.selectById(1L);
        // DO 转 VO
        FindBlogSettingsDetailRspVO vo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettings);

        return Response.success(vo);
    }
}
