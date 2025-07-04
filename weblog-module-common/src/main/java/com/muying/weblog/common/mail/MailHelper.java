package com.muying.weblog.common.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public class MailHelper {

    @Resource
    private MailProperties mailProperties;
    @Resource
    private JavaMailSender javaMailSender;

    public boolean sendHtml(String to, String title, String html) {
        log.info("==> 开始发送邮件 ...");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 邮件发送来源
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 邮件发送目标
            mimeMessageHelper.setTo(to);
            // 设置标题
            mimeMessageHelper.setSubject(title);
            // 设置内容，内容是否为 html 格式，值为 true
            mimeMessageHelper.setText(html, true);

            javaMailSender.send(mimeMessage);
            log.info("==> 邮件发送成功, to: {}, title: {}, content: {}", to, title, html);
        } catch (Exception e) {
            log.error("==> 发送邮件异常: ", e);
            return false;
        }

        return true;
    }


}
