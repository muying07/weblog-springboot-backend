package com.muying.weblog.admin.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticleDetailRspVO {

    /**
     * 文章 ID
     */
    private String id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章封面
     */
    private String cover;

    /**
     * 文章内容
     */
    private String content;
    private String aiDescribe;
    /**
     * 分类 ID
     */
    private String categoryId;

    /**
     * 标签 ID 集合
     */
    private List<String> tagIds;

    /**
     * 摘要
     */
    private String summary;
}
