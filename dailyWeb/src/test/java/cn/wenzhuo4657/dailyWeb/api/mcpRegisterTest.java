package cn.wenzhuo4657.dailyWeb.api;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.DocsContentTool;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.TodayDataTool;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.DocsContentReq;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.TodayDataReq;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class mcpRegisterTest {

    @Autowired
    private ItemEditService itemEditService;

    @Autowired
    private ChatModel chatModel;

    @Test
    public void SaaTest() throws GraphRunnerException {
        ToolCallback toolCallback1 = FunctionToolCallback
                .builder("getDocsContent", new DocsContentTool.DocsContent(itemEditService))
                .description("获取文档内容")
                .inputType(DocsContentReq.class)
                .build();
        ToolCallback  toolCallback2= FunctionToolCallback
                .builder("todayData", new TodayDataTool.TodayData(itemEditService))
                .description("获取指定用户的今日日志（非不变，可能会更改）")
                .inputType(TodayDataReq.class)
                .build();

        ReactAgent agent = ReactAgent.builder()
                .name("日报助手")
                .systemPrompt("你是一个日报分析助手,在返回答案之前，请：\n1,根据用户id获取当天日志\n2,获取相关文档的全部内容\n3,分析日志，总结出用户行为分析，以及相关建议\n4,将分析结果和建议以JSON格式返回")
                .model(chatModel)
                .saver(new MemorySaver())
                .tools(toolCallback1, toolCallback2)
                .build();
        String input="获取指定用户的今日日志,userID:2014584316373372928,如果返回有日志，请根据文档ID获取全部文档内容并返回(（可能不止一个文档id，请多次调用获取)。如果没有找到文档ID，请说明原因，";
        RunnableConfig config = RunnableConfig.builder()
                .threadId("user_123")
                .build();
        AssistantMessage call = agent.call(input,config);
        String response = call.getText();
        System.out.println(response);

    }
}
