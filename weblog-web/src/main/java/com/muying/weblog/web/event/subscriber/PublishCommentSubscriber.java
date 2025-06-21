package com.muying.weblog.web.event.subscriber;

import com.muying.weblog.common.entity.BlogSettings;
import com.muying.weblog.common.entity.Comment;
import com.muying.weblog.common.mail.MailHelper;
import com.muying.weblog.common.mapper.BlogSettingsMapper;
import com.muying.weblog.common.mapper.CommentMapper;
import com.muying.weblog.common.enums.CommentStatusEnum;
import com.muying.weblog.web.event.PublishCommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class PublishCommentSubscriber implements ApplicationListener<PublishCommentEvent> {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Autowired
    private MailHelper mailHelper;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(PublishCommentEvent event) {
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

        // 获取博客设置相关信息
        BlogSettings blogSettings = blogSettingsMapper.selectById(1L);
        // 博客名称
        String blogName = blogSettings.getName();
        // 博主邮箱
        String authorMail = blogSettings.getMail();
        // 是否需要审核
        boolean isCommentExamineOpen = blogSettings.getIsCommentExamineOpen();
        // 是否开启了敏感词过滤
        boolean isSensiWordOpen = blogSettings.getIsCommentSensiWordOpen();
        // 博客访问地址
        String domain = "http://47.120.20.0/#";

        // 二级评论，并且状态为 “正常”, 邮件通知被评论的用户
        if (Objects.nonNull(replyCommentId)
                && Objects.equals(status, CommentStatusEnum.NORMAL.getCode())) {
            Comment replyComment = commentMapper.selectById(replyCommentId);

            String to = replyComment.getMail();

            String routerUrl = replyComment.getRouterUrl();
            String title = String.format("你在%s的评论收到了回复", blogName);

            // 构建 HTML
            String html = String.format("<html><body>" +
                            "<h2>你的评论:</h2><p>%s</p>" +
                            "<h2>%s 回复了你:</h2><p>%s</p>" +
                            "<p><a href='%s%s' target='_blank'>查看详情</a></p>" +
                            "</body></html>",
                    replyComment.getContent(), nickname, content, domain, routerUrl);
            // 发送邮件
            mailHelper.sendHtml(to, title, html);
        } else if (Objects.isNull(replyCommentId)
                && StringUtils.isNotBlank(authorMail)) { // 一级评论, 需要通知到博主
            String routerUrl = comment.getRouterUrl();
            String title = String.format("%s收到了评论", blogName);

            // 如果开启了评论审核，并且当前评论状态为 "待审核"，标题后缀添加【待审核】标识
            if (isCommentExamineOpen && Objects.equals(status, CommentStatusEnum.WAIT_EXAMINE.getCode())) {
                title = title + "【待审核】";
            }

            // 如果开启了敏感词过滤，并且当前评论状态为 "审核不通过"，标题后缀添加【系统已拦截】标识
            if (isSensiWordOpen && Objects.equals(status, CommentStatusEnum.EXAMINE_FAILED.getCode())) {
                title = title + "【系统已拦截】";
            }

            // 构建 HTML
            String html = String.format("<html><body>" +
                            "<h2>路由:</h2><p>%s</p>" +
                            "<h2>%s 评论了你:</h2><p>%s</p>" +
                            "<p><a href='%s%s' target='_blank'>查看详情</a></p>" +
                            "</body></html>",
                    routerUrl, nickname, comment.getContent(), domain, routerUrl);
            // 发送邮件
            mailHelper.sendHtml(authorMail, title, html);
        }

    }
}
