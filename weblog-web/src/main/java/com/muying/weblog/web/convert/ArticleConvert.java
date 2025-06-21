package com.muying.weblog.web.convert;


import com.muying.weblog.common.entity.Article;
import com.muying.weblog.web.model.vo.archive.FindArchiveArticleRspVO;
import com.muying.weblog.web.model.vo.article.FindIndexArticlePageListRspVO;
import com.muying.weblog.web.model.vo.category.FindCategoryArticlePageListRspVO;
import com.muying.weblog.web.model.vo.tag.FindTagArticlePageListRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface ArticleConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleConvert INSTANCE = Mappers.getMapper(ArticleConvert.class);


    /**
     * ArticleDO -> FindArchiveArticleRspVO
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    @Mapping(target = "createMonth", expression = "java(java.time.YearMonth.from(bean.getCreateTime()))")
    @Mapping(target = "id", expression = "java(java.lang.Long.toString(bean.getId()))")
    FindArchiveArticleRspVO convertDO2ArchiveArticleVO(Article bean);

    /**
     * ArticleDO -> FindCategoryArticlePageListRspVO
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    @Mapping(target = "id", expression = "java(java.lang.Long.toString(bean.getId()))")
    FindCategoryArticlePageListRspVO convertDO2CategoryArticleVO(Article bean);

    /**
     * ArticleDO -> FindTagArticlePageListRspVO
     * @param bean
     * @return
     */
    @Mapping(target = "createDate", expression = "java(java.time.LocalDate.from(bean.getCreateTime()))")
    @Mapping(target = "id", expression = "java(java.lang.Long.toString(bean.getId()))")
    FindTagArticlePageListRspVO convertDO2TagArticleVO(Article bean);

    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "id", expression = "java(java.lang.Long.toString(bean.getId()))")
    FindIndexArticlePageListRspVO convertDO2VO(Article bean);
}
