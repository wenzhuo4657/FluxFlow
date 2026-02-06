package cn.wenzhuo4657.dailyWeb.domain.agent.graph.nodeAction;

import cn.wenzhuo4657.dailyWeb.domain.agent.graph.Constant;
import cn.wenzhuo4657.dailyWeb.domain.agent.graph.ReactAgent.AnalyzeLogsAgent;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.Map;

public   class  AnalyzeLogsNode implements NodeAction {
    private static final Logger log = LoggerFactory.getLogger(AnalyzeLogsNode.class);



    private static final String notText="忽略";

    private  AnalyzeLogsAgent agent;
    public  AnalyzeLogsNode(AnalyzeLogsAgent agent){
        this.agent=agent;
    }
        @Override
        public Map<String, Object> apply(OverAllState state) throws Exception {
            String message = state.value("message")
                    .map(v -> (String) v)
                    .orElse("");
            Long userId = state.value("userId")
                    .map(
                            a -> (Long) a
                    ).orElse(0L);
            log.info("开始分析用户日志：{}", message);

            String content = analyzeLogs(message,userId);
            log.info("分析结果：{},userId:{}", content,userId);


//            节点走向判断
            if (content.equals(notText)|| StringUtils.isBlank(content)){
                return Map.of();
            }else {
                return Map.of("content", content,"next_node", Constant.NOTIFY_NODE_NAME);
            }

        }

    private String analyzeLogs(String message,Long userId) {

        ReactAgent reactAgent = agent.getAgent();
        try {
            AssistantMessage call = reactAgent.call(message);
            return call.getText();
        }catch (Exception e){
         log.error("分析日志失败：{}",e.getMessage());
         return "";
        }
    }
}
