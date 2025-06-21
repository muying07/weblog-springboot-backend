package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muying.weblog.common.entity.ArticleContent;

public interface ArticleContentMapper extends BaseMapper<ArticleContent> {
    /**
     * 根据文章 ID 删除记录
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId) {
        return delete(Wrappers.<ArticleContent>lambdaQuery()
                .eq(ArticleContent::getArticleId, articleId));
    }
    /**
     * 根据文章 ID 查询
     * @param articleId
     * @return
     */
    default ArticleContent selectByArticleId(Long articleId) {
        return selectOne(Wrappers.<ArticleContent>lambdaQuery()
                .eq(ArticleContent::getArticleId, articleId));
    }

    /**
     * 通过文章 ID 更新
     * @param articleContentDO
     */
    default int updateByArticleId(ArticleContent articleContentDO) {
        return update(articleContentDO,
                Wrappers.<ArticleContent>lambdaQuery()
                        .eq(ArticleContent::getArticleId, articleContentDO.getArticleId()));
    }
}
