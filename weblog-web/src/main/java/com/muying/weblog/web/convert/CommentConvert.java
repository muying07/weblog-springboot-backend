package com.muying.weblog.web.convert;

import com.muying.weblog.common.entity.Comment;
import com.muying.weblog.web.model.vo.comment.FindCommentItemRspVO;
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
     * CommentDO -> FindCommentItemRspVO
     * @param bean
     * @return
     */
    @Mapping(target = "id", expression = "java(java.lang.Long.toString(bean.getId()))")
    FindCommentItemRspVO convertDO2VO(Comment bean);

}
