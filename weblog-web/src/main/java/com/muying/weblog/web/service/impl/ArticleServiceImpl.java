package com.muying.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.muying.weblog.common.entity.*;
import com.muying.weblog.common.enums.ResponseCodeEnum;
import com.muying.weblog.common.exception.BizException;
import com.muying.weblog.common.mapper.*;
import com.muying.weblog.common.utils.PageResponse;
import com.muying.weblog.common.utils.Response;
import com.muying.weblog.common.utils.StringUtil;
import com.muying.weblog.web.convert.ArticleConvert;
import com.muying.weblog.web.markdown.MarkdownHelper;
import com.muying.weblog.web.model.vo.article.*;
import com.muying.weblog.web.model.vo.category.FindCategoryListRspVO;
import com.muying.weblog.web.model.vo.tag.FindTagListRspVO;
import com.muying.weblog.web.service.ArticleService;
import com.muying.weblog.web.utils.MarkdownStatsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;


    @Override
    public Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        Long current = findIndexArticlePageListReqVO.getCurrent();
        Long size = findIndexArticlePageListReqVO.getSize();

        // 第一步：分页查询文章主体记录
        Page<Article> articlePage = articleMapper.selectPageList(current, size, null, null, null,null);

        // 返回的分页数据
        List<Article> articles = articlePage.getRecords();

        List<FindIndexArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articles)) {
            // 文章 DO 转 VO
            vos = articles.stream()
                    .map(article -> {
                        FindIndexArticlePageListRspVO findIndexArticlePageListRspVO = ArticleConvert.INSTANCE.convertDO2VO(article);
                        findIndexArticlePageListRspVO.setIsTop(article.getWeight()>0);
                        return findIndexArticlePageListRspVO;
                    })
                    .collect(Collectors.toList());

            // 拿到所有文章的 ID 集合
            List<Long> articleIds = articles.stream().map(Article::getId).collect(Collectors.toList());

            // 第二步：设置文章所属分类
            // 查询所有分类
            List<Category> categorys = categoryMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
            Map<Long, String> categoryIdNameMap = categorys.stream().collect(Collectors.toMap(Category::getId, Category::getName));

            // 根据文章 ID 批量查询所有关联记录
            List<ArticleCategoryRel> articleCategoryRels = articleCategoryRelMapper.selectByArticleIds(articleIds);

            vos.forEach(vo -> {
                Long currArticleId = Long.valueOf(vo.getId());
                // 过滤出当前文章对应的关联数据
                Optional<ArticleCategoryRel> optional = articleCategoryRels.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).findAny();

                // 若不为空
                if (optional.isPresent()) {
                    ArticleCategoryRel articleCategoryRel = optional.get();
                    Long categoryId = articleCategoryRel.getCategoryId();
                    // 通过分类 ID 从 map 中拿到对应的分类名称
                    String categoryName = categoryIdNameMap.get(categoryId);

                    FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                            .id(Long.toString(categoryId))
                            .name(categoryName)
                            .build();
                    // 设置到当前 vo 类中
                    vo.setCategory(findCategoryListRspVO);
                }
            });

            // 第三步：设置文章标签
            // 查询所有标签
            List<Tag> tags = tagMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据标签 ID 拿到对应的标签名称
            Map<Long, String> mapIdNameMap = tags.stream().collect(Collectors.toMap(Tag::getId, Tag::getName));

            // 拿到所有文章的标签关联记录
            List<ArticleTagRel> articleTagRels = articleTagRelMapper.selectByArticleIds(articleIds);
            vos.forEach(vo -> {
                Long currArticleId = Long.valueOf(vo.getId());
                // 过滤出当前文章的标签关联记录
                List<ArticleTagRel> articleTagRelList = articleTagRels.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).collect(Collectors.toList());

                List<FindTagListRspVO> findTagListRspVOS = Lists.newArrayList();
                // 将关联记录 DO 转 VO, 并设置对应的标签名称
                articleTagRelList.forEach(articleTagRelDO -> {
                    Long tagId = articleTagRelDO.getTagId();
                    String tagName = mapIdNameMap.get(tagId);

                    FindTagListRspVO findTagListRspVO = FindTagListRspVO.builder()
                            .id(Long.toString(tagId))
                            .name(tagName)
                            .build();
                    findTagListRspVOS.add(findTagListRspVO);
                });
                // 设置转换后的标签数据
                vo.setTags(findTagListRspVOS);
            });
        }

        return PageResponse.success(articlePage, vos);
    }

    /**
     * 获取文章详情
     *
     * @param findArticleDetailReqVO
     * @return
     */
    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = Long.valueOf(findArticleDetailReqVO.getArticleId());

        Article articleDO = articleMapper.selectById(articleId);

        // 判断文章是否存在
        if (Objects.isNull(articleDO)) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 查询正文
        ArticleContent articleContent = articleContentMapper.selectByArticleId(articleId);
        String content = articleContent.getContent();

        // 计算 md 正文字数
        Integer totalWords = MarkdownStatsUtil.calculateWordCount(content);

        // DO 转 VO
        FindArticleDetailRspVO vo = FindArticleDetailRspVO.builder()
                .title(articleDO.getTitle())
                .createTime(articleDO.getCreateTime())
                .content(MarkdownHelper.convertMarkdown2Html(content))
                .readNum(articleDO.getReadNum())
                .totalWords(totalWords)
                .aiDescribe(articleDO.getAiDescribe())
                .readTime(MarkdownStatsUtil.calculateReadingTime(totalWords))
                .build();

        // 查询所属分类
        ArticleCategoryRel articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);
        Category category = categoryMapper.selectById(articleCategoryRelDO.getCategoryId());
        vo.setCategoryId(Long.toString(category.getId()));
        vo.setCategoryName(category.getName());

        // 查询标签
        List<ArticleTagRel> articleTagRelS = articleTagRelMapper.selectByArticleId(articleId);
        List<Long> tagIds = articleTagRelS.stream().map(ArticleTagRel::getTagId).collect(Collectors.toList());
        List<Tag> tagS = tagMapper.selectByIds(tagIds);

        // 标签 DO 转 VO
        List<FindTagListRspVO> tagVOS = tagS.stream()
                .map(tagDO -> FindTagListRspVO.builder().id(Long.toString(tagDO.getId())).name(tagDO.getName()).build())
                .collect(Collectors.toList());
        vo.setTags(tagVOS);

        // 上一篇
        Article preArticle = articleMapper.selectPreArticle(articleId);
        if (Objects.nonNull(preArticle)) {
            FindPreNextArticleRspVO preArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(Long.toString(preArticle.getId()))
                    .articleTitle(preArticle.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 下一篇
        Article nextArticle = articleMapper.selectNextArticle(articleId);
        if (Objects.nonNull(nextArticle)) {
            FindPreNextArticleRspVO nextArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(Long.toString(nextArticle.getId()))
                    .articleTitle(nextArticle.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        return Response.success(vo);
    }

    @Override
    public Response findArticleLoginList() {
        // 第一步：分页查询文章主体记录
        List<Article> articleList = articleMapper.selectTop2LatestArticles();
        List<FindIndexArticleHotListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleList)) {
            vos = articleList.stream()
                    .map(articleDO -> FindIndexArticleHotListRspVO.builder()
                            .id(Long.toString(articleDO.getId()))
                            .cover(articleDO.getCover())
                            .title(articleDO.getTitle())
                            .createTime(articleDO.getCreateTime())
                            .summary(articleDO.getSummary())
                            .build())
                    .collect(Collectors.toList());
        }
        return Response.success(vos);
    }

    @Override
    public Response findArticleHotList() {
        // 第一步：分页查询文章主体记录
        List<Article> articleList = articleMapper.selectReadNumMax();
        List<FindIndexArticleHotListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleList)) {
             vos = articleList.stream()
                    .map(articleDO -> FindIndexArticleHotListRspVO.builder()
                            .id(Long.toString(articleDO.getId()))
                            .cover(articleDO.getCover())
                            .title(articleDO.getTitle())
                            .createTime(articleDO.getCreateTime())
                            .summary(StringUtil.truncateWithEllipsis(articleDO.getSummary(), 30))
                            .build())
                    .collect(Collectors.toList());
        }
        return Response.success(vos);
    }
}
