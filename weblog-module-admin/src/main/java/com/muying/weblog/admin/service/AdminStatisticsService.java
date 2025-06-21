package com.muying.weblog.admin.service;

public interface AdminStatisticsService {

    /**
     * 统计各分类下文章总数
     */
    void statisticsCategoryArticleTotal();
    /**
     * 统计各标签下文章总数
     */
    void statisticsTagArticleTotal();
}
