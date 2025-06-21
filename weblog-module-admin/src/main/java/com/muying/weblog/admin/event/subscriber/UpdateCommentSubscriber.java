package com.muying.weblog.admin.event.subscriber;

import com.muying.weblog.admin.event.UpdateCommentEvent;
import com.muying.weblog.common.entity.BlogSettings;
import com.muying.weblog.common.entity.Comment;
import com.muying.weblog.common.enums.CommentStatusEnum;
import com.muying.weblog.common.mail.MailHelper;
import com.muying.weblog.common.mapper.BlogSettingsMapper;
import com.muying.weblog.common.mapper.CommentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class UpdateCommentSubscriber implements ApplicationListener<UpdateCommentEvent> {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Autowired
    private MailHelper mailHelper;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(UpdateCommentEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long commentId = event.getCommentId();

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();

        log.info("==> threadName: {}", threadName);
        log.info("==> 评论发布事件消费成功，commentId: {}", commentId);

        Comment comment = commentMapper.selectById(commentId);
        Long replyCommentId = comment.getReplyCommentId();
        String nickname = comment.getNickname();
        String content = comment.getContent();
        Integer status = comment.getStatus();
        String mail = comment.getMail();
        String routerUrl = comment.getRouterUrl();


        BlogSettings blogSettings = blogSettingsMapper.selectById(1L);
        String blogName = blogSettings.getName();
        // 博客域名
        String domain = "http://47.120.20.0/#";

        // 判断评论状态
        // 若审核不通过，通知发评论的用户，你的评论未被博主审核通过，原因是什么
        if (Objects.equals(status, CommentStatusEnum.EXAMINE_FAILED.getCode())
                && StringUtils.isNotBlank(mail)) {

            String reason = comment.getReason();
            String title = String.format("你在%s的评论未被审核通过", blogName);

            String html = String.format("<html><body>" +
                            "<h2>你的评论:</h2><p>%s</p>" +
                            "<h2>审核未通过原因:</h2><p>%s</p>" +
                            "<p><a href='%s%s' target='_blank'>查看详情</a></p>" +
                            "</body></html>",
                    content, reason, domain, routerUrl);
            mailHelper.sendHtml(mail, title, html);
        } else if (Objects.equals(status, CommentStatusEnum.NORMAL.getCode())) {
            // 如果是审核通过，通知发评论的用户，你的评论已经被博主审核通过
            String title = String.format("你在%s的评论已被审核通过", blogName);
            String html = String.format("<html><body>" +
                            "<h2>你的评论:</h2><p>%s</p>" +
                            "<p><a href='%s%s' target='_blank'>查看详情</a></p>" +
                            "</body></html>",
                    content, domain, routerUrl);
            mailHelper.sendHtml(mail, title, html);

            // 另外，还得通知被评论人，提示评论被回复了
            notifyBeCommentedUser(replyCommentId, blogName, nickname, content, domain);
        }


    }

    /**
     * 邮件通知被回复的用户
     * @param replyCommentId
     * @param blogName
     * @param nickname
     * @param content
     * @param domain
     */
    private void notifyBeCommentedUser(Long replyCommentId, String blogName, String nickname, String content, String domain) {
        if (Objects.isNull(replyCommentId))
            return;

        // 被回复的评论
        Comment replyComment = commentMapper.selectById(replyCommentId);

        // 邮箱地址
        String to = replyComment.getMail();

        // 邮箱判空
        if (StringUtils.isBlank(to))
            return;

        String routerUrl = replyComment.getRouterUrl();
        String title = String.format("你在%s的评论收到了回复", blogName);

        String html = String.format("<html><body>" +
                        "<h2>你的评论:</h2><p>%s</p>" +
                        "<h2>%s 回复了你:</h2><p>%s</p>" +
                        "<p><a href='%s%s' target='_blank'>查看详情</a></p>" +
                        "</body></html>",
                replyComment.getContent(), nickname, content, domain, routerUrl);
        mailHelper.sendHtml(to, title, html);
    }
}