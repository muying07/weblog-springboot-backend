package com.muying.weblog.web.service;


import com.muying.weblog.common.utils.Response;

/**
 * @description: 博客设置
 **/
public interface BlogSettingsService {
    /**
     * 获取博客设置信息
     * @return
     */
    Response findDetail();
}
