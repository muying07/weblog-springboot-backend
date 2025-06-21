package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muying.weblog.common.config.InsertBatchMapper;
import com.muying.weblog.common.entity.ArticleTagRel;

import java.util.List;

public interface ArticleTagRelMapper extends InsertBatchMapper<ArticleTagRel> {
    /**
     * 根据文章 ID 删除关联记录
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId) {
        return delete(Wrappers.<ArticleTagRel>lambdaQuery()
                .eq(ArticleTagRel::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 来查询
     * @param articleId
     * @return
     */
    default List<ArticleTagRel> selectByArticleId(Long articleId) {
        return selectList(Wrappers.<ArticleTagRel>lambdaQuery()
                .eq(ArticleTagRel::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 集合批量查询
     * @param articleIds
     * @return
     */
    default List<ArticleTagRel> selectByArticleIds(List<Long> articleIds) {
        return selectList(Wrappers.<ArticleTagRel>lambdaQuery()
                .in(ArticleTagRel::getArticleId, articleIds));
    }

    /**
     * 查询该标签 ID 下所有关联记录
     * @param tagId
     * @return
     */
    default List<ArticleTagRel> selectByTagId(Long tagId) {
        return selectList(Wrappers.<ArticleTagRel>lambdaQuery()
                .eq(ArticleTagRel::getTagId, tagId));
    }
}
