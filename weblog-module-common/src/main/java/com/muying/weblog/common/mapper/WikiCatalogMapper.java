package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muying.weblog.common.config.InsertBatchMapper;
import com.muying.weblog.common.entity.WikiCatalog;
import com.muying.weblog.common.enums.WikiCatalogLevelEnum;

import java.util.List;

public interface WikiCatalogMapper extends InsertBatchMapper<WikiCatalog> {

    /**
     * 根据某个知识库下所有目录
     * @param wikiId
     * @return
     */
    default List<WikiCatalog> selectByWikiId(Long wikiId) {
        return selectList(Wrappers.<WikiCatalog>lambdaQuery()
                .eq(WikiCatalog::getWikiId, wikiId)
        );
    }

    /**
     * 删除知识库
     * @param wikiId
     * @return
     */
    default int deleteByWikiId(Long wikiId) {
        return delete(Wrappers.<WikiCatalog>lambdaQuery()
                .eq(WikiCatalog::getWikiId, wikiId));
    }

    /**
     * 查询知识库目录中第一篇文章
     * @param wikiId
     * @return
     */
    default WikiCatalog selectFirstArticleId(Long wikiId) {
        return selectOne(Wrappers.<WikiCatalog>lambdaQuery()
                .eq(WikiCatalog::getWikiId, wikiId) // 查询指定知识库 id
                .eq(WikiCatalog::getLevel, WikiCatalogLevelEnum.TWO.getValue()) // 查询二级目录
                .isNotNull(WikiCatalog::getArticleId) // article_id 字段不能为空
                .orderByAsc(WikiCatalog::getId) // 按 id 增序排列
                .last("LIMIT 1") // 仅查询一条
        );
    }

    /**
     * 根据知识库 ID 和文章 ID 查询对应的目录
     * @param wikiId
     * @param articleId
     * @return
     */
    default WikiCatalog selectByWikiIdAndArticleId(Long wikiId, Long articleId) {
        return selectOne(Wrappers.<WikiCatalog>lambdaQuery()
                .eq(WikiCatalog::getWikiId, wikiId)
                .eq(WikiCatalog::getArticleId, articleId)
        );
    }

    /**
     * 查询下一篇文章
     * @param wikiId
     * @param catalogId
     * @return
     */
    default WikiCatalog selectNextArticle(Long wikiId, Long catalogId) {
        return selectOne(Wrappers.<WikiCatalog>lambdaQuery()
                .eq(WikiCatalog::getWikiId, wikiId)
                .isNotNull(WikiCatalog::getArticleId) // article_id 字段不能为空
                .orderByAsc(WikiCatalog::getId) // 按目录 ID 倒序排列
                .gt(WikiCatalog::getId, catalogId) // 查询比当前文章 ID 大的
                .last("limit 1") // 第一条记录即为下一篇文章
        );
    }

    /**
     * 查询上一篇文章
     * @param wikiId
     * @param catalogId
     * @return
     */
    default WikiCatalog selectPreArticle(Long wikiId, Long catalogId) {
        return selectOne(Wrappers.<WikiCatalog>lambdaQuery()
                .eq(WikiCatalog::getWikiId, wikiId)
                .isNotNull(WikiCatalog::getArticleId) // // article_id 字段不能为空
                .orderByDesc(WikiCatalog::getId) // 按文章 ID 倒序排列
                .lt(WikiCatalog::getId, catalogId) // 查询比当前文章 ID 小的
                .last("limit 1") // 第一条记录即为上一篇文章
        );
    }
}
