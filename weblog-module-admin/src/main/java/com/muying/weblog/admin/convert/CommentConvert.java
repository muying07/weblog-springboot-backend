package com.muying.weblog.admin.convert;

import com.muying.weblog.admin.model.vo.comment.FindCommentPageListRspVO;
import com.muying.weblog.common.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentConvert {
    /**
     * 初始化 convert 实例
     */
    CommentConvert INSTANCE = Mappers.getMapper(CommentConvert.class);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    @Mapping(target = "id", expression = "java(java.lang.Long.toString(bean.getId()))")
    FindCommentPageListRspVO convertDO2VO(Comment bean);

}
