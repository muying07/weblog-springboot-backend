package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muying.weblog.common.entity.ArticleCategoryRel;

import java.util.List;

public interface ArticleCategoryRelMapper extends BaseMapper<ArticleCategoryRel> {
    /**
     * 根据文章 ID 删除关联记录
     * @param articleId
     * @return
     */
    default int deleteByArticleId(Long articleId) {
        return delete(Wrappers.<ArticleCategoryRel>lambdaQuery()
                .eq(ArticleCategoryRel::getArticleId, articleId));
    }

    /**
     * 根据文章 ID 查询
     * @param articleId
     * @return
     */
    default ArticleCategoryRel selectByArticleId(Long articleId) {
        return selectOne(Wrappers.<ArticleCategoryRel>lambdaQuery()
                .eq(ArticleCategoryRel::getArticleId, articleId));
    }

    /**
     * 根据分类 ID 查询
     * @param categoryId
     * @return
     */
    default ArticleCategoryRel selectOneByCategoryId(Long categoryId) {
        return selectOne(Wrappers.<ArticleCategoryRel>lambdaQuery()
                .eq(ArticleCategoryRel::getCategoryId, categoryId)
                .last("LIMIT 1"));
    }

    /**
     * 根据文章 ID 集合批量查询
     * @param articleIds
     * @return
     */
    default List<ArticleCategoryRel> selectByArticleIds(List<Long> articleIds) {
        return selectList(Wrappers.<ArticleCategoryRel>lambdaQuery()
                .in(ArticleCategoryRel::getArticleId, articleIds));
    }

    /**
     * 根据分类 ID 查询所有的关联记录
     * @param categoryId
     * @return
     */
    default List<ArticleCategoryRel> selectListByCategoryId(Long categoryId){
        return selectList(Wrappers.<ArticleCategoryRel>lambdaQuery()
                .eq(ArticleCategoryRel::getCategoryId, categoryId));

    }
}
