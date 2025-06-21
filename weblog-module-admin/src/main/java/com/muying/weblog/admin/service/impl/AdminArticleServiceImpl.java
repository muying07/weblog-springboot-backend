package com.muying.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.muying.weblog.admin.convert.ArticleDetailConvert;
import com.muying.weblog.admin.event.DeleteArticleEvent;
import com.muying.weblog.admin.event.PublishArticleEvent;
import com.muying.weblog.admin.event.SummaryArticleEvent;
import com.muying.weblog.admin.event.UpdateArticleEvent;
import com.muying.weblog.admin.model.vo.article.*;
import com.muying.weblog.admin.service.AdminArticleService;
import com.muying.weblog.common.entity.*;
import com.muying.weblog.common.enums.ResponseCodeEnum;
import com.muying.weblog.common.exception.BizException;
import com.muying.weblog.common.mapper.*;
import com.muying.weblog.common.utils.PageResponse;
import com.muying.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Service
@Slf4j
public class AdminArticleServiceImpl implements AdminArticleService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleContentMapper articleContentMapper;
    @Resource
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;


    /**
     * 查询文章分页数据
     *
     * @param findArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findArticlePageListReqVO.getCurrent();
        Long size = findArticlePageListReqVO.getSize();
        String title = findArticlePageListReqVO.getTitle();
        LocalDate startDate = findArticlePageListReqVO.getStartDate();
        LocalDate endDate = findArticlePageListReqVO.getEndDate();
        Integer type = findArticlePageListReqVO.getType();

        // 执行分页查询
        Page<Article> articleDOPage = articleMapper.selectPageList(current, size, title, startDate, endDate, type);

        List<Article> articleDOS = articleDOPage.getRecords();

        // DO 转 VO
        List<FindArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> FindArticlePageListRspVO.builder()
                            .id(Long.toString(articleDO.getId()))
                            .title(articleDO.getTitle())
                            .cover(articleDO.getCover())
                            .createTime(articleDO.getCreateTime())
                            .isTop(articleDO.getWeight()>0) // 是否置顶
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 查询文章详情
     *
     * @param findArticleDetailReqVO
     * @return
     */
    @Override
    public Response<FindArticleDetailRspVO> findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = Long.valueOf(findArticleDetailReqVO.getId());

        Article article = articleMapper.selectById(articleId);

        if (Objects.isNull(article)) {
            log.warn("==> 查询的文章不存在，articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        ArticleContent articleContent = articleContentMapper.selectByArticleId(articleId);

        // 所属分类
        ArticleCategoryRel articleCategoryRel = articleCategoryRelMapper.selectByArticleId(articleId);

        // 对应标签
        List<ArticleTagRel> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        // 获取对应标签 ID 集合
        List<String> tagIds = articleTagRelDOS.stream().map(u->Long.toString(u.getTagId())).collect(Collectors.toList());

        // DO 转 VO
        FindArticleDetailRspVO vo = ArticleDetailConvert.INSTANCE.convertDO2VO(article);
        vo.setContent(articleContent.getContent());
        vo.setCategoryId(Long.toString(articleCategoryRel.getCategoryId()));
        vo.setTagIds(tagIds);

        return Response.success(vo);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        Long articleId = Long.valueOf(updateArticleReqVO.getId());

        // 1. VO 转 ArticleDO, 并更新
        Article article = Article.builder()
                .id(articleId)
                .title(updateArticleReqVO.getTitle())
                .cover(updateArticleReqVO.getCover())
                .summary(updateArticleReqVO.getSummary())
                .updateTime(LocalDateTime.now())
                .build();
        int count = articleMapper.updateById(article);

        // 根据更新是否成功，来判断该文章是否存在
        if (count == 0) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 2. VO 转 ArticleContentDO，并更新
        ArticleContent articleContent = ArticleContent.builder()
                .articleId(articleId)
                .content(updateArticleReqVO.getContent())
                .build();
        articleContentMapper.updateByArticleId(articleContent);


        // 3. 更新文章分类
        Long categoryId =Long.parseLong(updateArticleReqVO.getCategoryId());

        // 3.1 校验提交的分类是否真实存在
        Category category = categoryMapper.selectById(categoryId);
        if (Objects.isNull(category)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 先删除该文章关联的分类记录，再插入新的关联关系
        articleCategoryRelMapper.deleteByArticleId(articleId);
        ArticleCategoryRel articleCategoryRel = ArticleCategoryRel.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRel);

        // 4. 保存文章关联的标签集合
        // 先删除该文章对应的标签
        articleTagRelMapper.deleteByArticleId(articleId);
        List<String> publishTags = updateArticleReqVO.getTags();
        insertTags(articleId, publishTags);
        // 发布文章修改事件
        eventPublisher.publishEvent(new UpdateArticleEvent(this, articleId));
        return Response.success();
    }

    @Override
    public Response updateArticleIsTop(UpdateArticleIsTopReqVO updateArticleIsTopReqVO) {
        Long id = Long.valueOf(updateArticleIsTopReqVO.getId());
        Boolean isTop = updateArticleIsTopReqVO.getIsTop();

        // 默认权重为 0
        Integer weight = 0;

        if(isTop){//是置顶
            Article maxWeightArticle = articleMapper.selectMaxWeightArticle();
            Integer currentMaxWeight = maxWeightArticle.getWeight();
            weight = currentMaxWeight + 1;
        }

        int updateById = articleMapper.updateById(Article.builder()
                .id(id)
                .weight(weight)
                .build());

        return updateById == 1  ? Response.success() : Response.fail();
    }

    /**
     * 发布文章
     *
     * @param publishArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        // 1. VO 转 Article, 并保存
        Article article = Article.builder()
                .title(publishArticleReqVO.getTitle())
                .summary(publishArticleReqVO.getSummary())
                .cover(publishArticleReqVO.getCover())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.insert(article);
        // 拿到插入记录的主键 ID
        Long articleId = article.getId();

        // 2. VO 转 ArticleContentDO，并保存
        String content = publishArticleReqVO.getContent();
        ArticleContent articleContent = ArticleContent.builder()
                .articleId(articleId)
                .content(content)
                .build();
        articleContentMapper.insert(articleContent);

        // 3. 处理文章关联的分类
        Long categoryId = publishArticleReqVO.getCategoryId();

        // 3.1 校验提交的分类是否真实存在
        Category categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        ArticleCategoryRel articleCategoryRel = ArticleCategoryRel.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRel);

        // 4. 保存文章关联的标签集合
        List<String> publishTags = publishArticleReqVO.getTags();
        insertTags(articleId, publishTags);
         // 发送文章发布事件
        eventPublisher.publishEvent(new PublishArticleEvent(this, articleId));
        eventPublisher.publishEvent(new SummaryArticleEvent(this, articleId, content,publishArticleReqVO.getSummary()));

        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId = Long.valueOf(deleteArticleReqVO.getId());

        // 1. 删除文章
        articleMapper.deleteById(articleId);

        // 2. 删除文章内容
        articleContentMapper.deleteByArticleId(articleId);

        // 3. 删除文章-分类关联记录
        articleCategoryRelMapper.deleteByArticleId(articleId);

        // 4. 删除文章-标签关联记录
        articleTagRelMapper.deleteByArticleId(articleId);
        // 发布文章删除事件
        eventPublisher.publishEvent(new DeleteArticleEvent(this, articleId));
        return Response.success();
    }

    /**
     * 保存标签
     * @param articleId
     * @param publishTags
     */
    private void insertTags(Long articleId, List<String> publishTags) {
        // 筛选提交的标签（表中不存在的标签）
        List<String> notExistTags = null;
        // 筛选提交的标签（表中已存在的标签）
        List<String> existedTags = null;

        // 查询出所有标签
        List<Tag> tagDos = tagMapper.selectList(null);

        // 如果表中还没有添加任何标签
        if (CollectionUtils.isEmpty(tagDos)) {
            notExistTags = publishTags;
        } else {
            List<String> tagIds = tagDos.stream().map(tagDO -> String.valueOf(tagDO.getId())).collect(Collectors.toList());
            // 表中已添加相关标签，则需要筛选
            // 通过标签 ID 来筛选，包含对应 ID 则表示提交的标签是表中存在的
            existedTags = publishTags.stream().filter(tagIds::contains).collect(Collectors.toList());
            // 否则则是不存在的
            notExistTags = publishTags.stream().filter(publishTag -> !tagIds.contains(publishTag)).collect(Collectors.toList());

            // 补充逻辑：
            // 还有一种可能：按字符串名称提交上来的标签，也有可能是表中已存在的，比如表中已经有了 Java 标签，用户提交了个 java 小写的标签，需要内部装换为 Java 标签
            Map<String, Long> tagNameIdMap = tagDos.stream().collect(Collectors.toMap(tagDO -> tagDO.getName().toLowerCase(), Tag::getId));

            // 使用迭代器进行安全的删除操作
            Iterator<String> iterator = notExistTags.iterator();
            while (iterator.hasNext()) {
                String notExistTag = iterator.next();
                // 转小写, 若 Map 中相同的 key，则表示该新标签是重复标签
                if (tagNameIdMap.containsKey(notExistTag.toLowerCase())) {
                    // 从不存在的标签集合中清除
                    iterator.remove();
                    // 并将对应的 ID 添加到已存在的标签集合
                    existedTags.add(String.valueOf(tagNameIdMap.get(notExistTag.toLowerCase())));
                }
            }
        }

        // 将提交的上来的，已存在于表中的标签，文章-标签关联关系入库
        if (!CollectionUtils.isEmpty(existedTags)) {
            List<ArticleTagRel> articleTagRelDos = Lists.newArrayList();
            existedTags.forEach(tagId -> {
                ArticleTagRel articleTagRelDO = ArticleTagRel.builder()
                        .articleId(articleId)
                        .tagId(Long.valueOf(tagId))
                        .build();
                articleTagRelDos.add(articleTagRelDO);
            });
            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDos);
        }

        // 将提交的上来的，不存在于表中的标签，入库保存
        if (!CollectionUtils.isEmpty(notExistTags)) {
            // 需要先将标签入库，拿到对应标签 ID 后，再把文章-标签关联关系入库
            List<ArticleTagRel> articleTagRelDos = Lists.newArrayList();
            notExistTags.forEach(tagName -> {
                Tag tagDO = Tag.builder()
                        .name(tagName)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();

                tagMapper.insert(tagDO);

                // 拿到保存的标签 ID
                Long tagId = tagDO.getId();

                // 文章-标签关联关系
                ArticleTagRel articleTagRelDO = ArticleTagRel.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .build();
                articleTagRelDos.add(articleTagRelDO);
            });
            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDos);
        }
    }

}
