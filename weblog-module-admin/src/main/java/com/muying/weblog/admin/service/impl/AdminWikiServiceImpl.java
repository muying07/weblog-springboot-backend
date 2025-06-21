package com.muying.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.muying.weblog.admin.convert.WikiConvert;
import com.muying.weblog.admin.model.vo.wiki.*;
import com.muying.weblog.admin.service.AdminWikiService;
import com.muying.weblog.common.entity.Article;
import com.muying.weblog.common.entity.Wiki;
import com.muying.weblog.common.entity.WikiCatalog;
import com.muying.weblog.common.enums.ArticleTypeEnum;
import com.muying.weblog.common.enums.ResponseCodeEnum;
import com.muying.weblog.common.enums.WikiCatalogLevelEnum;
import com.muying.weblog.common.exception.BizException;
import com.muying.weblog.common.mapper.ArticleMapper;
import com.muying.weblog.common.mapper.WikiCatalogMapper;
import com.muying.weblog.common.mapper.WikiMapper;
import com.muying.weblog.common.utils.PageResponse;
import com.muying.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminWikiServiceImpl implements AdminWikiService {

    @Autowired
    private WikiMapper wikiMapper;
    @Autowired
    private WikiCatalogMapper wikiCatalogMapper;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Response findWikiPageList(FindWikiPageListReqVO findWikiPageListReqVO) {

        // 获取当前页、以及每页需要展示的数据数量
        Long current = findWikiPageListReqVO.getCurrent();
        Long size = findWikiPageListReqVO.getSize();
        // 查询条件
        String title = findWikiPageListReqVO.getTitle();
        LocalDate startDate = findWikiPageListReqVO.getStartDate();
        LocalDate endDate = findWikiPageListReqVO.getEndDate();

        // 执行分页查询
        Page<Wiki> wikiPage = wikiMapper.selectPageList(current, size, title, startDate, endDate, null);

        // 获取查询记录
        List<Wiki> wikis = wikiPage.getRecords();

        // DO 转 VO
        List<FindWikiPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(wikis)) {
            vos = wikis.stream()
                    .map(articleDO -> WikiConvert.INSTANCE.convertDO2VO(articleDO))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(wikiPage, vos);
    }

    /**
     * 查询知识库目录
     *
     * @param findWikiCatalogListReqVO
     * @return
     */
    @Override
    public Response findWikiCatalogList(FindWikiCatalogListReqVO findWikiCatalogListReqVO) {
        Long wikiId = Long.valueOf(findWikiCatalogListReqVO.getId());

        // 查询此知识库下所有目录
        List<WikiCatalog> catalogs = wikiCatalogMapper.selectByWikiId(wikiId);

        // DO 转 VO
        // 组装一、二级目录结构
        List<FindWikiCatalogListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(catalogs)) {
            vos = Lists.newArrayList();

            // 提取一级目录
            List<WikiCatalog> level1Catalogs = catalogs.stream()
                    .filter(catalog -> Objects.equals(catalog.getLevel(), WikiCatalogLevelEnum.ONE.getValue())) // 一级目录
                    .sorted(Comparator.comparing(WikiCatalog::getSort)) // 升序排列
                    .collect(Collectors.toList());

            // 循环一级目录 DO 集合，转 VO
            for (WikiCatalog level1Catalog : level1Catalogs) {
                vos.add(FindWikiCatalogListRspVO.builder()
                        .id(Long.toString(level1Catalog.getId()))
                        .articleId(Long.toString(level1Catalog.getArticleId()))
                        .title(level1Catalog.getTitle())
                        .level(level1Catalog.getLevel())
                        .sort(level1Catalog.getSort())
                        .editing(Boolean.FALSE)
                        .build());
            }

            // 设置一级目录下，二级目录的数据
            vos.forEach(level1Catalog -> {
                Long parentId = Long.valueOf(level1Catalog.getId());
                // 提取二级目录
                List<WikiCatalog> level2CatalogDOS = catalogs.stream()
                        .filter(catalogDO -> Objects.equals(catalogDO.getParentId(), parentId)
                                && Objects.equals(catalogDO.getLevel(), WikiCatalogLevelEnum.TWO.getValue()))
                        .sorted(Comparator.comparing(WikiCatalog::getSort))
                        .collect(Collectors.toList());

                // 二级目录 DO 转 VO
                List<FindWikiCatalogListRspVO> level2Catalogs = level2CatalogDOS.stream()
                        .map(catalogDO -> FindWikiCatalogListRspVO.builder()
                                .id(Long.toString(catalogDO.getId()))
                                .articleId(Long.toString(catalogDO.getArticleId()))
                                .title(catalogDO.getTitle())
                                .level(catalogDO.getLevel())
                                .sort(catalogDO.getSort())
                                .editing(Boolean.FALSE)
                                .build())
                        .collect(Collectors.toList());
                level1Catalog.setChildren(level2Catalogs);
            });
        }

        return Response.success(vos);
    }


    /**
     * 更新知识库目录
     *
     * @param updateWikiCatalogReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateWikiCatalogs(UpdateWikiCatalogReqVO updateWikiCatalogReqVO) {
        // 知识库 ID
        Long wikiId = Long.valueOf(updateWikiCatalogReqVO.getId());
        // 目录
        List<UpdateWikiCatalogItemReqVO> catalogs = updateWikiCatalogReqVO.getCatalogs();

        // 1. 先将此知识库中的所有文章类型更新为普通
        // 查出此 wiki 下所有的文章 ID
        List<WikiCatalog> wikiCatalogs = wikiCatalogMapper.selectByWikiId(wikiId);
        List<Long> articleIds = wikiCatalogs.stream()
                .filter(wikiCatalog -> Objects.nonNull(wikiCatalog.getArticleId()))
                .map(WikiCatalog::getArticleId).collect(Collectors.toList());

        // 更新为普通文章类型
        if (!CollectionUtils.isEmpty(articleIds)) {
            articleMapper.updateByIds(Article.builder()
                    .type(ArticleTypeEnum.NORMAL.getValue()).build(), articleIds);
        }

        // 2. 先删除所有此知识库下所有目录
        wikiCatalogMapper.deleteByWikiId(wikiId);

        // 3. 再重新插入新的目录数据
        // 若入参传入的目录不为空
        if (!CollectionUtils.isEmpty(catalogs)) {
            // 重新设置 sort 排序字段的值
            for (int i = 0; i < catalogs.size(); i++) {
                UpdateWikiCatalogItemReqVO vo = catalogs.get(i);
                List<UpdateWikiCatalogItemReqVO> children = vo.getChildren();
                vo.setSort(i + 1);
                if (!CollectionUtils.isEmpty(children)) {
                    for (int j = 0; j < children.size(); j++) {
                        children.get(j).setSort(j + 1);
                    }
                }
            }

            // VO 转 DO
            catalogs.forEach(catalog -> {
                // 一级目录
                WikiCatalog wikiCatalog = WikiCatalog.builder()
                        .wikiId(wikiId)
                        .title(catalog.getTitle())
                        .level(WikiCatalogLevelEnum.ONE.getValue())
                        .sort(catalog.getSort())
                        .build();
                // 添加一级目录
                wikiCatalogMapper.insert(wikiCatalog);

                // 一级目录 ID
                Long catalogId = wikiCatalog.getId();

                // 获取下面的二级目录
                List<UpdateWikiCatalogItemReqVO> children = catalog.getChildren();
                // 需要被更新 type 字段的所有文章 ID
                List<Long> updateArticleIds = Lists.newArrayList();
                if (!CollectionUtils.isEmpty(children)) {
                    List<WikiCatalog> level2Catalogs = Lists.newArrayList();
                    // VO 转 DO
                    children.forEach(child -> {
                        level2Catalogs.add(WikiCatalog.builder()
                                .wikiId(wikiId)
                                .title(child.getTitle())
                                .level(WikiCatalogLevelEnum.TWO.getValue())
                                .sort(child.getSort())
                                .articleId(Long.valueOf(child.getArticleId()))
                                .parentId(catalogId)
                                .createTime(LocalDateTime.now())
                                .updateTime(LocalDateTime.now())
                                .isDeleted(Boolean.FALSE)
                                .build());

                        updateArticleIds.add(Long.valueOf(child.getArticleId()));
                    });

                    // 批量插入二级目录数据
                    wikiCatalogMapper.insertBatchSomeColumn(level2Catalogs);
                    // 更新相关文章的 type 字段，知识库类型
                    articleMapper.updateByIds(Article.builder()
                            .type(ArticleTypeEnum.WIKI.getValue()).build(), updateArticleIds);
                }
            });
        }

        return Response.success();
    }

    /**
     * 新增知识库
     *
     * @param addWikiReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addWiki(AddWikiReqVO addWikiReqVO) {
        // VO 转 DO
        Wiki wiki = Wiki.builder()
                .cover(addWikiReqVO.getCover())
                .title(addWikiReqVO.getTitle())
                .summary(addWikiReqVO.getSummary())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 新增知识库
        wikiMapper.insert(wiki);
        // 获取新增记录的主键 ID
        Long wikiId = wiki.getId();

        // 初始化默认目录
        // > 概述
        // > 基础
        wikiCatalogMapper.insert(WikiCatalog.builder().wikiId(wikiId).title("概述").sort(1).build());
        wikiCatalogMapper.insert(WikiCatalog.builder().wikiId(wikiId).title("基础").sort(2).build());
        return Response.success();
    }

    /**
     * 更新知识库
     *
     * @param updateWikiReqVO
     * @return
     */
    @Override
    public Response updateWiki(UpdateWikiReqVO updateWikiReqVO) {
        // VO 转 DO
        Wiki wikiDO = Wiki.builder()
                .id(Long.valueOf(updateWikiReqVO.getId()))
                .title(updateWikiReqVO.getTitle())
                .cover(updateWikiReqVO.getCover())
                .summary(updateWikiReqVO.getSummary())
                .build();

        // 根据 ID 更新知识库
        wikiMapper.updateById(wikiDO);
        return Response.success();
    }

    @Override
    public Response updateWikiIsTop(UpdateWikiIsTopReqVO updateWikiIsTopReqVO) {
        Long wikiId = Long.valueOf(updateWikiIsTopReqVO.getId());
        Boolean isTop = updateWikiIsTopReqVO.getIsTop();

        // 默认权重值为 0 ，即不参与置顶
        Integer weight = 0;
        // 若设置为置顶
        if (isTop) {
            // 查询最大权重值
            Wiki wiki = wikiMapper.selectMaxWeight();
            Integer maxWeight = wiki.getWeight();
            // 最大权重值加一
            weight = maxWeight + 1;
        }

        // 更新该知识库的权重值
        wikiMapper.updateById(Wiki.builder().id(wikiId).weight(weight).build());
        return Response.success();
    }

    @Override
    public Response updateWikiIsPublish(UpdateWikiIsPublishReqVO updateWikiIsPublishReqVO) {
        Long wikiId = Long.valueOf(updateWikiIsPublishReqVO.getId());
        Boolean isPublish = updateWikiIsPublishReqVO.getIsPublish();
        // 更新发布状态
        wikiMapper.updateById(Wiki.builder().id(wikiId).isPublish(isPublish).build());
        return Response.success();
    }

    /**
     * 删除知识库
     *
     * @param deleteWikiReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteWiki(DeleteWikiReqVO deleteWikiReqVO) {
        Long wikiId = Long.valueOf(deleteWikiReqVO.getId());

        // 删除知识库
        int count = wikiMapper.deleteById(wikiId);

        // 若知识库不存在
        if (count == 0) {
            log.warn("该知识库不存在, wikiId: {}", wikiId);
            throw new BizException(ResponseCodeEnum.WIKI_NOT_FOUND);
        }

        // 查询此知识库下所有目录
        List<WikiCatalog> wikiCatalogs= wikiCatalogMapper.selectByWikiId(wikiId);
        // 过滤目录中所有文章的 ID
        List<Long> articleIds = wikiCatalogs.stream()
                .filter(wikiCatalog -> Objects.nonNull(wikiCatalog.getArticleId())  // 文章 ID 不为空
                        && Objects.equals(wikiCatalog.getLevel(), WikiCatalogLevelEnum.TWO.getValue())) // 二级目录
                .map(WikiCatalog::getArticleId) // 提取文章 ID
                .collect(Collectors.toList());

        // 更新文章类型 type 为普通
        if (!CollectionUtils.isEmpty(articleIds)) {
            articleMapper.updateByIds(Article.builder()
                    .type(ArticleTypeEnum.NORMAL.getValue())
                    .build(), articleIds);
        }

        // 删除知识库目录
        wikiCatalogMapper.deleteByWikiId(wikiId);
        return Response.success();
    }
}
