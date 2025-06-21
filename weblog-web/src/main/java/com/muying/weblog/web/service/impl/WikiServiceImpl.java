package com.muying.weblog.web.service.impl;

import com.google.common.collect.Lists;
import com.muying.weblog.common.entity.Wiki;
import com.muying.weblog.common.entity.WikiCatalog;
import com.muying.weblog.common.enums.WikiCatalogLevelEnum;
import com.muying.weblog.common.mapper.WikiCatalogMapper;
import com.muying.weblog.common.mapper.WikiMapper;
import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.model.vo.article.FindPreNextArticleRspVO;
import com.muying.weblog.web.model.vo.wiki.*;
import com.muying.weblog.web.service.WikiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WikiServiceImpl implements WikiService {

    @Autowired
    private WikiMapper wikiMapper;
    @Autowired
    private WikiCatalogMapper wikiCatalogMapper;

    /**
     * 获取知识库
     *
     * @return
     */
    @Override
    public Response findWikiList() {
        // 查询已发布的知识库
        List<Wiki> wikis = wikiMapper.selectPublished();

        // DO 转 VO
        List<FindWikiListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(wikis)) {
            vos = wikis.stream()
                    .map(wikiDO -> FindWikiListRspVO.builder()
                            .id(Long.toString(wikiDO.getId()))
                            .title(wikiDO.getTitle())
                            .cover(wikiDO.getCover())
                            .summary(wikiDO.getSummary())
                            .isTop(wikiDO.getWeight() > 0)
                            .build())
                    .collect(Collectors.toList());

            // 设置每个知识库的第一篇文章 ID，方便前端跳转
            vos.forEach(vo -> {
                Long wikiId = Long.valueOf(vo.getId());
                WikiCatalog wikiCatalog = wikiCatalogMapper.selectFirstArticleId(wikiId);
                vo.setFirstArticleId(Objects.nonNull(wikiCatalog) ? Long.toString(wikiCatalog.getArticleId()) : null);
            });
        }

        return Response.success(vos);
    }

    /**
     * 获取知识库目录
     *
     * @param findWikiCatalogListReqVO
     * @return
     */
    @Override
    public Response findWikiCatalogList(FindWikiCatalogListReqVO findWikiCatalogListReqVO) {
        Long wikiId = Long.valueOf(findWikiCatalogListReqVO.getId());

        // 获取该知识库下所有目录数据
        List<WikiCatalog> catalogs = wikiCatalogMapper.selectByWikiId(wikiId);

        // DO 转 VO
        List<FindWikiCatalogListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(catalogs)) {
            vos = Lists.newArrayList();

            // 过滤出一级目录，并按 sort 值升序排列
            List<WikiCatalog> level1Catalogs = catalogs.stream()
                    .filter(catalogDO -> Objects.equals(catalogDO.getLevel(), WikiCatalogLevelEnum.ONE.getValue())) // 过滤
                    .sorted(Comparator.comparing(WikiCatalog::getSort)) // 升序
                    .collect(Collectors.toList());

            // 构造 VO 对象, 并添加到 vos 集合中
            for (WikiCatalog level1Catalog : level1Catalogs) {
                vos.add(FindWikiCatalogListRspVO.builder()
                        .id(Long.toString(level1Catalog.getId()))
                        .articleId(Long.toString(level1Catalog.getArticleId()))
                        .title(level1Catalog.getTitle())
                        .level(level1Catalog.getLevel())
                        .build());
            }

            // 循环 vos 集合，开始构造二级目录数据
            vos.forEach(level1Catalog -> {
                // 一级目录的 ID
                Long parentId = Long.valueOf(level1Catalog.getId());

                // 过滤出当前一级目录下的子目录，并按照 sort 值升序排列
                List<WikiCatalog> level2CatalogDOS = catalogs.stream()
                        .filter(catalogDO -> Objects.equals(catalogDO.getParentId(), parentId)
                                && Objects.equals(catalogDO.getLevel(), WikiCatalogLevelEnum.TWO.getValue()))
                        .sorted(Comparator.comparing(WikiCatalog::getSort))
                        .collect(Collectors.toList());

                // 设置子目录数据到 children 字段中
                if (!CollectionUtils.isEmpty(level2CatalogDOS)) {
                    List<FindWikiCatalogListRspVO> level2Catalogs = level2CatalogDOS.stream()
                            .map(catalogDO -> FindWikiCatalogListRspVO.builder()
                                    .id(Long.toString(catalogDO.getId()))
                                    .articleId(Long.toString(catalogDO.getArticleId()))
                                    .title(catalogDO.getTitle())
                                    .level(catalogDO.getLevel())
                                    .build())
                            .collect(Collectors.toList());
                    level1Catalog.setChildren(level2Catalogs);
                }
            });
        }

        return Response.success(vos);
    }

    /**
     * 获取上下页
     *
     * @param findWikiArticlePreNextReqVO
     * @return
     */
    @Override
    public Response findArticlePreNext(FindWikiArticlePreNextReqVO findWikiArticlePreNextReqVO) {
        // 知识库 ID
        Long wikiId = Long.valueOf(findWikiArticlePreNextReqVO.getId());
        // 文章 ID
        Long articleId = Long.valueOf(findWikiArticlePreNextReqVO.getArticleId());

        FindWikiArticlePreNextRspVO vo = new FindWikiArticlePreNextRspVO();
        // 获取当前文章所属知识库的目录
        WikiCatalog wikiCatalogDO = wikiCatalogMapper.selectByWikiIdAndArticleId(wikiId, articleId);

        // 构建上一篇文章 VO
        WikiCatalog preArticleDO = wikiCatalogMapper.selectPreArticle(wikiId, wikiCatalogDO.getId());
        if (Objects.nonNull(preArticleDO)) {
            FindPreNextArticleRspVO preArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(Long.toString(preArticleDO.getArticleId()))
                    .articleTitle(preArticleDO.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 构建下一篇文章 VO
        WikiCatalog nextArticleDO = wikiCatalogMapper.selectNextArticle(wikiId, wikiCatalogDO.getId());
        if (Objects.nonNull(nextArticleDO)) {
            FindPreNextArticleRspVO nextArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(Long.toString(nextArticleDO.getArticleId()))
                    .articleTitle(nextArticleDO.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        return Response.success(vo);
    }

}
