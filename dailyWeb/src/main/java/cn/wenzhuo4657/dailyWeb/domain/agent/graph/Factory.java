package cn.wenzhuo4657.dailyWeb.domain.agent.graph;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.agent.graph.ReactAgent.AnalyzeLogsAgent;
import cn.wenzhuo4657.dailyWeb.domain.agent.graph.nodeAction.AnalyzeLogsNode;
import cn.wenzhuo4657.dailyWeb.domain.agent.graph.nodeAction.NotifyNode;
import cn.wenzhuo4657.dailyWeb.domain.agent.graph.nodeAction.ReadLogsNode;
import cn.wenzhuo4657.dailyWeb.domain.notifier.NotifierService;
import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

@Component
public class Factory {

    private Logger log = org.slf4j.LoggerFactory.getLogger(Factory.class);




    private ItemEditService itemEditService;

    private AnalyzeLogsAgent analyzeLogsAgent;


    private NotifierService notifierService;


    private final  CompiledGraph compiledGraph;

    public Factory(ItemEditService itemEditService,AnalyzeLogsAgent agent,NotifierService notifierService) throws GraphStateException {

        this.itemEditService=itemEditService;
        this.analyzeLogsAgent=agent;
        this.notifierService=notifierService;
        this.compiledGraph = creatCompiledGraph();
    }

    /**
     * 全局上下文
     * @return
     */
    public static KeyStrategyFactory createKeyStrategyFactory() {
        return ()  -> {
            HashMap<String, KeyStrategy> strategies = new HashMap<>();
            return strategies;
        };
    }

    private   CompiledGraph creatCompiledGraph() throws GraphStateException {
        var readLogs = node_async(new ReadLogsNode(itemEditService));
        var analyzeLogs = node_async(new AnalyzeLogsNode(analyzeLogsAgent));
        var notify = node_async(new NotifyNode(notifierService));

        StateGraph workflow = new StateGraph(createKeyStrategyFactory())
                .addNode(Constant.LOGS_NODE_NAME, readLogs)
                .addNode(Constant.ANALYZE_LOGS_AGENT_NAME, analyzeLogs)
                .addNode(Constant.NOTIFY_NODE_NAME, notify);

        workflow.addEdge(StateGraph.START, Constant.LOGS_NODE_NAME);
        workflow.addEdge(Constant.NOTIFY_NODE_NAME, StateGraph.END);

        workflow.addConditionalEdges(Constant.LOGS_NODE_NAME,
                edge_async( state -> {
                    return (String) state.value("next_node").orElse(StateGraph.END);
                }),
                Map.of(
                        StateGraph.END, StateGraph.END,
                        Constant.ANALYZE_LOGS_AGENT_NAME, Constant.ANALYZE_LOGS_AGENT_NAME
                )
                );


        workflow.addConditionalEdges(Constant.ANALYZE_LOGS_AGENT_NAME,
                edge_async(state -> {
                    return (String) state.value("next_node").orElse(StateGraph.END);
                }),
                Map.of(
                        StateGraph.END, StateGraph.END,
                        Constant.NOTIFY_NODE_NAME, Constant.NOTIFY_NODE_NAME
                )
        );
        return workflow.compile();
    }


    /**
     * 执行代理工作流，返回异步结果
     * @param userId 用户ID
     * @return CompletableFuture<Boolean> true-执行成功, false-执行失败
     */
    public CompletableFuture<Boolean> execute(Long userId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        compiledGraph.stream(Map.of("userId", userId))
                .doOnNext(output -> log.info("节点输出: node:{}, state:{}", output.node(), output.state()))
                .doOnComplete(() -> {
                    log.info("流完成, userId: {}", userId);
                    future.complete(true);
                })
                .doOnError(error -> {
                    log.error("执行错误, userId: {}, error: {}", userId, error.getMessage(), error);
                    future.complete(false);
                })
                .subscribe();

        return future;
    }
}
