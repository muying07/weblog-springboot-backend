package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muying.weblog.common.entity.Comment;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 根据路由地址、状态查询对应的评论
     * @param routerUrl
     * @return
     */
    default List<Comment> selectByRouterUrlAndStatus(String routerUrl, Integer status) {
        return selectList(Wrappers.<Comment>lambdaQuery()
                .eq(Comment::getRouterUrl, routerUrl) // 按路由地址查询
                .eq(Comment::getStatus, status) // 按状态查询
                .orderByDesc(Comment::getCreateTime) // 按创建时间倒序
        );
    }

    /**
     * 分页查询
     * @param current
     * @param size
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<Comment> selectPageList(Long current, Long size, String routerUrl,
                                           LocalDate startDate, LocalDate endDate, Integer status) {
        // 分页对象(查询第几页、每页多少数据)
        Page<Comment> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<Comment> wrapper = Wrappers.<Comment>lambdaQuery()
                .like(StringUtils.isNotBlank(routerUrl), Comment::getRouterUrl, routerUrl) // like 模糊查询
                .eq(Objects.nonNull(status), Comment::getStatus, status) // 评论状态
                .ge(Objects.nonNull(startDate), Comment::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), Comment::getCreateTime, endDate)  // 小于等于 endDate
                .orderByDesc(Comment::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }

    /**
     * 根据 reply_comment_id 查询评论
     * @param replyCommentId
     * @return
     */
    default List<Comment> selectByReplyCommentId(Long replyCommentId) {
        return selectList(Wrappers.<Comment>lambdaQuery()
                .eq(Comment::getReplyCommentId, replyCommentId)
                .orderByDesc(Comment::getCreateTime)
        );
    }

    /**
     * 根据 parent_comment_id 删除
     * @param id
     * @return
     */
    default int deleteByParentCommentId(Long id) {
        return delete(Wrappers.<Comment>lambdaQuery()
                .eq(Comment::getParentCommentId, id));
    }
}
