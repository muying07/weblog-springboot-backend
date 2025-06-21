package com.muying.weblog.admin.convert;

import com.muying.weblog.admin.model.vo.article.FindArticleDetailRspVO;
import com.muying.weblog.common.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface ArticleDetailConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleDetailConvert INSTANCE = Mappers.getMapper(ArticleDetailConvert.class);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    @Mapping(target = "id", expression = "java(java.lang.Long.toString(bean.getId()))")
    FindArticleDetailRspVO convertDO2VO(Article bean);
}
