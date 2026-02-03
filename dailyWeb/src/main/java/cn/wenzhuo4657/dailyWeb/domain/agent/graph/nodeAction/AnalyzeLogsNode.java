package cn.wenzhuo4657.dailyWeb.domain.agent.graph.nodeAction;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public   class  AnalyzeLogsNode implements NodeAction {
    private static final Logger log = LoggerFactory.getLogger(AnalyzeLogsNode.class);

    private static final String notText="忽略";
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

            if (content.equals(notText)|| StringUtils.isBlank(content)){
                return Map.of();
            }else {
                return Map.of("content", content,"next_node", "notify");
            }

        }

    private String analyzeLogs(String message,Long userId) {
//            todo 封装agent执行生成逻辑
//        todo 组装图
//        todo 图是否支持并发？

        /**
         * 目前的做法不支持长期记忆，也不会进行长期分析
         * 正常来说，应该是能否对话控制，也就是能够支持用户单位的记忆
         *
         * agent需求，
         * 1，上下文
         *  用户单位激活上下文，自动裁剪
         * 2，装配
         * agent的状态是以用户为单位的，而不是全局统一的
         *
         *
         * 理想需求，
         * 用户可以通过对话调整图的某种参数，可以干涉分析日志的节点，例如agent的system pormpt
         *
         *
         *
         */


        return null;
    }
}
