package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muying.weblog.common.entity.Article;
import com.muying.weblog.common.entity.ArticlePublishCount;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public interface ArticleMapper extends BaseMapper<Article> {
    /**
     * 分页查询
     * @param current
     * @param size
     * @param title
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<Article> selectPageList(Long current, Long size, String title,
                                           LocalDate startDate, LocalDate endDate, Integer type) {
        // 分页对象(查询第几页、每页多少数据)
        Page<Article> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<Article> wrapper = Wrappers.<Article>lambdaQuery()
                .like(StringUtils.isNotBlank(title), Article::getTitle, title) // like 模块查询
                .ge(Objects.nonNull(startDate), Article::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), Article::getCreateTime, endDate)  // 小于等于 endDate
                .eq(Objects.nonNull(type), Article::getType, type) // 文章类型
                .orderByDesc(Article::getWeight) // 按权重倒序
                .orderByDesc(Article::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }

    /**
     * 根据文章 ID 批量分页查询
     *
     * @param current
     * @param size
     * @param articleIds
     * @return
     */
    default Page<Article> selectPageListByArticleIds(Long current, Long size, List<Long> articleIds) {
        // 分页对象(查询第几页、每页多少数据)
        Page<Article> page = new Page<>(current, size);
        return selectPage(page, Wrappers.<Article>lambdaQuery()
                .in(CollectionUtils.isNotEmpty(articleIds), Article::getId, articleIds)
                .orderByDesc(Article::getCreateTime));
    }

    /**
     * 查询上一篇文章
     *
     * @param articleId
     * @return
     */
    default Article selectPreArticle(Long articleId) {
        return selectOne(Wrappers.<Article>lambdaQuery()
                .orderByAsc(Article::getId) // 按文章 ID 升序排列
                .gt(Article::getId, articleId) // 查询比当前文章 ID 大的
                .last("limit 1")); // 第一条记录即为上一篇文章
    }

    /**
     * 查询下一篇文章
     *
     * @param articleId
     * @return
     */
    default Article selectNextArticle(Long articleId) {
        return selectOne(Wrappers.<Article>lambdaQuery()
                .orderByDesc(Article::getId) // 按文章 ID 倒序排列
                .lt(Article::getId, articleId) // 查询比当前文章 ID 小的
                .last("limit 1")); // 第一条记录即为下一篇文章
    }

    /**
     * 阅读量+1
     *
     * @param articleId
     * @return
     */
    default int increaseReadNum(Long articleId) {
        // 执行 SQL : UPDATE t_article SET read_num = read_num + 1 WHERE (id = XX)
        return update(null, Wrappers.<Article>lambdaUpdate()
                .setSql("read_num = read_num + 1")
                .eq(Article::getId, articleId));
    }

    /**
     * 查询所有记录的阅读量
     *
     * @return
     */
    default List<Article> selectAllReadNum() {
        // 设置仅查询 read_num 字段
        return selectList(Wrappers.<Article>lambdaQuery()
                .select(Article::getReadNum));
    }

    /**
     * 按日分组，并统计每日发布的文章数量
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count\n" +
            "FROM t_article\n" +
            "WHERE create_time >= #{startDate} AND create_time < #{endDate}\n" +
            "GROUP BY DATE(create_time)")
    List<ArticlePublishCount> selectDateArticlePublishCount(LocalDate startDate, LocalDate endDate);

    /**
     * 查询权重最大的文章
     * @return
     */
    default Article selectMaxWeightArticle() {
        return selectOne(Wrappers.<Article>lambdaQuery()
                .orderByDesc(Article::getWeight)
                .last("limit 1"));
    }

    /**
     * 批量更新文章
     * @param article
     * @param ids
     * @return
     */
    default int updateByIds(Article article, List<Long> ids) {
        return update(article, Wrappers.<Article>lambdaUpdate()
                .in(Article::getId, ids));
    }

    /**
     * 查询阅读量前5的文章
     * @return
     */
    default List<Article> selectReadNumMax() {
        // 设置仅查询 read_num 字段
        return selectList(Wrappers.<Article>lambdaQuery()
                .orderByDesc(Article::getReadNum)
                .last("limit 5"));
    }

    /**
     * 查询发布最新前3的文章
     * @return
     */
    default List<Article> selectTop2LatestArticles() {
        // 设置仅查询 created time 字段
        return selectList(Wrappers.<Article>lambdaQuery()
                .orderByDesc(Article::getCreateTime)
                .last("limit 3"));
    }


}
