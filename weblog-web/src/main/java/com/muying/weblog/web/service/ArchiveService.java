package com.muying.weblog.web.service;


import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;

/**
 * @description: 归档文章
 **/
public interface ArchiveService {
    /**
     * 获取文章归档分页数据
     * @param findArchiveArticlePageListReqVO
     * @return
     */
    Response findArchivePageList(FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO);
}
