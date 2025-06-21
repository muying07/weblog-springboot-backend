package com.muying.weblog.admin.event.subscriber;

import com.muying.weblog.admin.event.SummaryArticleEvent;
import com.muying.weblog.common.entity.Article;
import com.muying.weblog.common.mapper.ArticleMapper;
import com.muying.weblog.common.model.CommonSumaryVo;
import com.muying.weblog.common.utils.AiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SummaryArticleSubscriber implements ApplicationListener<SummaryArticleEvent> {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private AiUtil aiUtil;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(SummaryArticleEvent event) {
        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();// 在这里处理收到的事件，可以是任何逻辑操作
        Long articleId = event.getArticleId();
        log.info("==> threadName: {}", threadName);
        String summary = event.getSummaryContent();
        log.info("==> 文章AI生成摘要事件消费成功，articleId: {}", articleId);
        String content = event.getContent();
        Article article = new Article();
        article.setId(articleId);
        CommonSumaryVo vo;
        try {
            if (StringUtils.isBlank(summary)) {
                log.info("==> 文章AI摘要生成开始，articleId: {}", articleId);
                vo = aiUtil.send(Boolean.TRUE, content);
            } else {
                log.info("==> 存储文章摘要开始，articleId: {}", articleId);
                vo = aiUtil.send(Boolean.FALSE, content);
            }
            if (StringUtils.isNotBlank(vo.getAiSumaryContent())) {
                article.setAiDescribe(vo.getAiSumaryContent());
                log.info("==> 存储文章AI摘要成功，articleId: {}", articleId);
            }
            if (StringUtils.isNotBlank(vo.getSumaryContent())) {
                article.setSummary(vo.getSumaryContent());
                log.info("==> 存储文章摘要成功，articleId: {}", articleId);
            }
            articleMapper.updateById(article);
            log.info("==> 文章AI摘要生成成功，articleId: {}", articleId);
        } catch (Exception e) {
            log.error("==> 文章AI摘要生成失败，articleId: {}, 原因: {}", articleId, e.getMessage(), e);
        }
    }
}
