package com.muying.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.muying.weblog.admin.service.AdminStatisticsService;
import com.muying.weblog.common.entity.ArticleCategoryRel;
import com.muying.weblog.common.entity.ArticleTagRel;
import com.muying.weblog.common.entity.Category;
import com.muying.weblog.common.entity.Tag;
import com.muying.weblog.common.mapper.ArticleCategoryRelMapper;
import com.muying.weblog.common.mapper.ArticleTagRelMapper;
import com.muying.weblog.common.mapper.CategoryMapper;
import com.muying.weblog.common.mapper.TagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Override
    public void statisticsCategoryArticleTotal() {
        // 查询所有分类
        List<Category> categorys = categoryMapper.selectList(Wrappers.emptyWrapper());

        // 查询所有文章-分类映射记录
        List<ArticleCategoryRel> articleCategoryRels = articleCategoryRelMapper.selectList(Wrappers.emptyWrapper());

        // 按所属分类 ID 进行分组
        Map<Long, List<ArticleCategoryRel>> categoryIdAndArticleCategoryRelMap = Maps.newHashMap();
        // 如果不为空
        if (!CollectionUtils.isEmpty(articleCategoryRels)) {
            categoryIdAndArticleCategoryRelMap = articleCategoryRels.stream()
                    .collect(Collectors.groupingBy(ArticleCategoryRel::getCategoryId));
        }

        if (!CollectionUtils.isEmpty(categorys)) {
            // 循环统计各分类下的文章总数
            for (Category category : categorys) {
                Long categoryId = category.getId();
                // 获取此分类下所有映射记录
                List<ArticleCategoryRel> articleCategoryRelList = categoryIdAndArticleCategoryRelMap.get(categoryId);

                // 获取文章总数
                int articlesTotal = CollectionUtils.isEmpty(articleCategoryRelList) ? 0 : articleCategoryRelList.size();

                // 更新该分类的文章总数
                Category category1 = Category.builder()
                        .id(categoryId)
                        .articlesTotal(articlesTotal)
                        .build();
                categoryMapper.updateById(category1);
            }
        }
    }

    /**
     * 统计各标签下文章总数
     */
    @Override
    public void statisticsTagArticleTotal() {
        // 查询所有标签
        List<Tag> tags = tagMapper.selectList(Wrappers.emptyWrapper());

        // 查询所有文章-标签映射记录
        List<ArticleTagRel> articleTagRelDOS = articleTagRelMapper.selectList(Wrappers.emptyWrapper());

        // 按所属标签 ID 进行分组
        Map<Long, List<ArticleTagRel>> tagIdAndArticleTagRelDOMap = Maps.newHashMap();
        // 如果不为空
        if (!CollectionUtils.isEmpty(articleTagRelDOS)) {
            tagIdAndArticleTagRelDOMap = articleTagRelDOS.stream()
                    .collect(Collectors.groupingBy(ArticleTagRel::getTagId));
        }

        if (!CollectionUtils.isEmpty(tags)) {
            // 循环统计各标签下的文章总数
            for (Tag tagDO : tags) {
                Long tagId = tagDO.getId();

                // 获取此标签下所有映射记录
                List<ArticleTagRel> articleTagRelDOList = tagIdAndArticleTagRelDOMap.get(tagId);

                // 获取文章总数
                int articlesTotal = CollectionUtils.isEmpty(articleTagRelDOList) ? 0 : articleTagRelDOList.size();

                // 更新该标签的文章总数
                Tag tagDO1 = Tag.builder()
                        .id(tagId)
                        .articlesTotal(articlesTotal)
                        .build();
                tagMapper.updateById(tagDO1);
            }
        }
    }
}

