package com.muying.weblog.web.service;

import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;

/**
 * @description: 分类
 **/
public interface CategoryService {
    /**
     * 获取分类列表
     * @return
     */
    Response findCategoryList();

    /**
     * 获取分类下文章分页数据
     * @param findCategoryArticlePageListReqVO
     * @return
     */
    Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO);
}
