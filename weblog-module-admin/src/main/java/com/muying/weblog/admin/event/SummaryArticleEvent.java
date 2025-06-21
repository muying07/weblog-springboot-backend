package com.muying.weblog.admin.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class SummaryArticleEvent extends ApplicationEvent {

    /**
     * 评论 ID
     */
    private Long articleId;

    /**
     * 文章内容
     */
    private String content;
    /**
     * 文章摘要
     */
    private String summaryContent;

    public SummaryArticleEvent(Object source, Long articleId,String content, String summaryContent) {
        super(source);
        this.articleId = articleId;
         this.content = content;
         this.summaryContent = summaryContent;
    }
}