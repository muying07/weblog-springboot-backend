package com.muying.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muying.weblog.common.entity.Article;
import com.muying.weblog.common.entity.ArticleTagRel;
import com.muying.weblog.common.entity.Tag;
import com.muying.weblog.common.enums.ResponseCodeEnum;
import com.muying.weblog.common.exception.BizException;
import com.muying.weblog.common.mapper.ArticleMapper;
import com.muying.weblog.common.mapper.ArticleTagRelMapper;
import com.muying.weblog.common.mapper.TagMapper;
import com.muying.weblog.common.utils.PageResponse;
import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.convert.ArticleConvert;
import com.muying.weblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.muying.weblog.web.model.vo.tag.FindTagArticlePageListRspVO;
import com.muying.weblog.web.model.vo.tag.FindTagListRspVO;
import com.muying.weblog.web.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 标签
 **/
@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 获取标签列表
     *
     * @return
     */
    @Override
    public Response findTagList() {
        // 查询所有标签
        List<Tag> tags = tagMapper.selectList(Wrappers.emptyWrapper());

        // DO 转 VO
        List<FindTagListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tags)) {
            vos = tags.stream()
                    .map(tagDO -> FindTagListRspVO.builder()
                            .id(Long.toString(tagDO.getId()))
                            .name(tagDO.getName())
                            .articlesTotal(tagDO.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * 获取标签下文章分页列表
     *
     * @param findTagArticlePageListReqVO
     * @return
     */
    @Override
    public Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        Long current = findTagArticlePageListReqVO.getCurrent();
        Long size = findTagArticlePageListReqVO.getSize();
        // 标签 ID
        Long tagId = Long.valueOf(findTagArticlePageListReqVO.getId());

        // 判断该标签是否存在
        Tag tag = tagMapper.selectById(tagId);
        if (Objects.isNull(tag)) {
            log.warn("==> 该标签不存在, tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }

        // 先查询该标签下所有关联的文章 ID
        List<ArticleTagRel> articleTagRelS = articleTagRelMapper.selectByTagId(tagId);

        // 若该标签下未发布任何文章
        if (CollectionUtils.isEmpty(articleTagRelS)) {
            log.info("==> 该标签下还未发布任何文章, tagId: {}", tagId);
            return PageResponse.success(null, null);
        }

        // 提取所有文章 ID
        List<Long> articleIds = articleTagRelS.stream().map(ArticleTagRel::getArticleId).collect(Collectors.toList());

        // 根据文章 ID 集合查询文章分页数据
        Page<Article> page = articleMapper.selectPageListByArticleIds(current, size, articleIds);
        List<Article> articleDOS = page.getRecords();

        // DO 转 VO
        List<FindTagArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> ArticleConvert.INSTANCE.convertDO2TagArticleVO(articleDO))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }
}
