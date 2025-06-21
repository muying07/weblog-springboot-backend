package com.muying.weblog.web.service;

import com.muying.weblog.admin.model.vo.comment.FindCommentPageListReqVO;
import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.model.vo.comment.FindCommentListReqVO;
import com.muying.weblog.web.model.vo.comment.FindQQUserInfoReqVO;
import com.muying.weblog.web.model.vo.comment.PublishCommentReqVO;

/**
 * @description: 评论
 **/
public interface CommentService {

    /**
     * 根据 QQ 号获取用户信息
     * @param findQQUserInfoReqVO
     * @return
     */
    Response findQQUserInfo(FindQQUserInfoReqVO findQQUserInfoReqVO);
    /**
     * 发布评论
     * @param publishCommentReqVO
     * @return
     */
    Response publishComment(PublishCommentReqVO publishCommentReqVO);
    /**
     * 查询页面所有评论
     * @param findCommentListReqVO
     * @return
     */
    Response findCommentList(FindCommentListReqVO findCommentListReqVO);


}
