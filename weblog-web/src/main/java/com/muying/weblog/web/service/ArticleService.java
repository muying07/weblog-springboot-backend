package com.muying.weblog.web.service;

import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.muying.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;

public interface ArticleService {
    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);
    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    /**
     * 获取首页推荐文章列表
     * @return
     */
    Response findArticleLoginList();


    /**
     * 获取首页推荐文章列表
     * @return
     */
    Response findArticleHotList();
}
