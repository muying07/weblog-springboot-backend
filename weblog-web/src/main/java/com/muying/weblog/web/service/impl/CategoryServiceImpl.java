package com.muying.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muying.weblog.common.entity.Article;
import com.muying.weblog.common.entity.ArticleCategoryRel;
import com.muying.weblog.common.entity.Category;
import com.muying.weblog.common.enums.ResponseCodeEnum;
import com.muying.weblog.common.exception.BizException;
import com.muying.weblog.common.mapper.ArticleCategoryRelMapper;
import com.muying.weblog.common.mapper.ArticleMapper;
import com.muying.weblog.common.mapper.CategoryMapper;
import com.muying.weblog.common.utils.PageResponse;
import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.convert.ArticleConvert;
import com.muying.weblog.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.muying.weblog.web.model.vo.category.FindCategoryArticlePageListRspVO;
import com.muying.weblog.web.model.vo.category.FindCategoryListRspVO;
import com.muying.weblog.web.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 分类
 **/
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 获取分类列表
     *
     * @return
     */
    @Override
    public Response findCategoryList() {
        // 查询所有分类
        List<Category> categorys = categoryMapper.selectList(Wrappers.emptyWrapper());

        // DO 转 VO
        List<FindCategoryListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categorys)) {
            vos = categorys.stream()
                    .map(category -> FindCategoryListRspVO.builder()
                            .id(Long.toString(category.getId()))
                            .name(category.getName())
                            .articlesTotal(category.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * 获取分类下文章分页数据
     *
     * @param findCategoryArticlePageListReqVO
     * @return
     */
    @Override
    public Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO) {
        Long current = findCategoryArticlePageListReqVO.getCurrent();
        Long size = findCategoryArticlePageListReqVO.getSize();
        Long categoryId = Long.valueOf(findCategoryArticlePageListReqVO.getId());

        Category category = categoryMapper.selectById(categoryId);

        // 判断该分类是否存在
        if (Objects.isNull(category)) {
            log.warn("==> 该分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 先查询该分类下所有关联的文章 ID
        List<ArticleCategoryRel> articleCategoryRelS = articleCategoryRelMapper.selectListByCategoryId(categoryId);

        // 若该分类下未发布任何文章
        if (CollectionUtils.isEmpty(articleCategoryRelS)) {
            log.info("==> 该分类下还未发布任何文章, categoryId: {}", categoryId);
            return PageResponse.success(null, null);
        }

        List<Long> articleIds = articleCategoryRelS.stream().map(ArticleCategoryRel::getArticleId).collect(Collectors.toList());

        // 根据文章 ID 集合查询文章分页数据
        Page<Article> page = articleMapper.selectPageListByArticleIds(current, size, articleIds);
        List<Article> articleDOS = page.getRecords();

        // DO 转 VO
        List<FindCategoryArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2CategoryArticleVO(articleDO))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }
}
