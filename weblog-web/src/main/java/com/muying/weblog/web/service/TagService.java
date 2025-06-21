package com.muying.weblog.web.service;

import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;

/**
 * @description: 分类
 **/
public interface TagService {
    /**
     * 获取标签列表
     * @return
     */
    Response findTagList();

    /**
     * 获取标签下文章分页列表
     * @param findTagArticlePageListReqVO
     * @return
     */
    Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO);
}
