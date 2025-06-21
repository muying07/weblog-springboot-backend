package com.muying.weblog.admin.convert;

import com.muying.weblog.admin.model.vo.wiki.FindWikiPageListRspVO;
import com.muying.weblog.common.entity.Wiki;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WikiConvert {
    /**
     * 初始化 convert 实例
     */
    WikiConvert INSTANCE = Mappers.getMapper(WikiConvert.class);

    /**
     * WikiDO -> FindWikiPageListRspVO
     * @param bean
     * @return
     */
    @Mapping(target = "isTop", expression = "java(bean.getWeight() > 0)")
    @Mapping(target = "id", expression = "java(java.lang.Long.toString(bean.getId()))")
    FindWikiPageListRspVO convertDO2VO(Wiki bean);

}