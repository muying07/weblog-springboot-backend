package com.muying.weblog.common.utils;

import com.muying.weblog.common.model.CommonSumaryVo;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class AiUtil {


    @Value("${ai.apiKey}")
    private String apiKey;

    @Value("${ai.baseUrl}")
    private String baseUrl;

    @Value("${ai.model}")
    private String model;

    private volatile ArkService arkService = null;


    private void initService() {
        if (arkService == null) {
            synchronized (this) {
                if (arkService == null) {
                    arkService = ArkService.builder()
                            .apiKey(apiKey)
                            .timeout(Duration.ofMinutes(30))
                            .connectTimeout(Duration.ofSeconds(20))
                            .retryTimes(3)
                            .baseUrl(baseUrl)
                            .build();
                }
            }
        }
    }


    public String send(String content) {
        // 初始化消息列表
        List<ChatMessage> messages = new ArrayList<>();
        // 创建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER) // 设置消息角色为用户
                .content(content) // 设置消息内容
                .build();
        // 将用户消息添加到消息列表
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .build();

        // 获取响应并收集每个选择的消息内容
        List<Object> responses = new ArrayList<>();

        // 发送聊天完成请求并打印响应
        try {
            // 获取响应并打印每个选择的消息内容
            arkService.createChatCompletion(chatCompletionRequest)
                    .getChoices()
                    .forEach(choice -> {
                        // 校验是否触发了深度思考，打印思维链内容
                        if (choice.getMessage().getReasoningContent() != null) {
                            log.info("推理内容: " + choice.getMessage().getReasoningContent());
                        } else {
                            System.out.println("推理内容为空");
                        }
                        // 打印消息内容
                        log.info("消息内容: " + choice.getMessage().getContent());
                        responses.add(choice.getMessage().getContent());
                    });
        } catch (Exception e) {
            log.warn("请求失败: " + e.getMessage());
        }
        return responses.get(0).toString();
    }

    public CommonSumaryVo send(Boolean isSummary, String content) {
        initService();
        CommonSumaryVo resultVo = new CommonSumaryVo();
        try {
            // 始终添加总结文章的提示词
            String problem = "请提供一段简短的介绍描述该文章的内容：" + content;

            String aiSummary = send(problem);
            resultVo.setAiSumaryContent(aiSummary);
            if (isSummary != null && isSummary) {
                problem += "\n要求：\n1.总结内容精炼且不超过30个字；" +
                        "\n2.可以在 在恰当的位置添加相关的 Emoji 表情，以增强文本的表现力，但注意适度使用，不影响整体阅读体验；" +
                        "\n3.请不要出现换行，分割符、序号等元素影响整体布局，保持内容是一段完整的语句，句子中切勿出现第一人称叙述。";
                String fullSummary = send(problem);
                resultVo.setSumaryContent(fullSummary);
            }
            return resultVo;
        } catch (Exception e) {
            log.error("生成文章摘要失败", e);
            resultVo.setAiSumaryContent("摘要生成失败");
        }
        shutdown();
        return resultVo;

    }

    // 添加应用关闭时的清理方法
    public void shutdown() {
        if (arkService != null) {
            try {
                arkService.shutdownExecutor();
                log.info("AI服务线程池已关闭");
            } catch (Exception e) {
                log.error("关闭AI服务线程池失败", e);
            }
        }
    }


}
