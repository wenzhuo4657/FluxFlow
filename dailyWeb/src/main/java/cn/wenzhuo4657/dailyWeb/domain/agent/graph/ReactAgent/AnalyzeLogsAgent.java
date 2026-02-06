package cn.wenzhuo4657.dailyWeb.domain.agent.graph.ReactAgent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AnalyzeLogsAgent {

    private ChatModel chatModel;


    @Autowired
    public AnalyzeLogsAgent(ChatModel chatModel) {
        this.chatModel = chatModel;
        this.agent=getInstance();
    }

    String originalPrompt = """
            你是一个日报分析助手，负责分析用户的今日日报数据并生成简洁的提醒通知。

            ## 输入格式
            你将收到用户的今日日报数据，包含：
            - baseItem: 今日已完成的基础事项
            - planItem: 今日计划事项

            ## 输出要求
            1. 风格：简洁、友好的建议风格
            2. 长度：严格控制在300字以内
            3. 内容：
               - 如果用户有未完成的计划项，温和提醒
               - 如果用户已完成较多事项，给予正向鼓励
               - 如果内容较少或无实质内容，返回"忽略"
            4. 语言：中文
            5. 格式：直接输出通知文本，无需任何标题或额外说明

            ## 示例
            输入: baseItem=[完成代码审查], planItem=[完成API开发, 编写单元测试]
            输出: 你今天已经完成了代码审查，做得不错！还有2个计划项待完成：API开发和单元测试。加油，继续保持！

            输入: baseItem=[], planItem=[]
            输出: 忽略
            """;



    private  final ReactAgent agent;

    private ReactAgent getInstance() {
        ReactAgent agent = ReactAgent.builder()
                .name("日报助手")
                .systemPrompt(originalPrompt)
                .model(chatModel)
                .saver(new MemorySaver())
                .build();
        return agent;
    }

    public ReactAgent getAgent() {
        return agent;
    }
}
