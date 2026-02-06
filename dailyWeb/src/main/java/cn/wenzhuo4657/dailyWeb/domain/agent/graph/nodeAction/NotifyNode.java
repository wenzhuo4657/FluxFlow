package cn.wenzhuo4657.dailyWeb.domain.agent.graph.nodeAction;

import cn.wenzhuo4657.dailyWeb.domain.notifier.NotifierService;
import cn.wenzhuo4657.dailyWeb.domain.notifier.model.dto.NotifierBody;
import cn.wenzhuo4657.dailyWeb.domain.notifier.model.dto.NotifierMessage;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public  class  NotifyNode implements NodeAction {
    private static final Logger log = LoggerFactory.getLogger(NotifyNode.class);

    private NotifierService notifierService;

    public NotifyNode(NotifierService notifierService) {
        this.notifierService = notifierService;
    }

    @Override
        public Map<String, Object> apply(OverAllState state) throws Exception {
            String content = state.value("content")
                    .map(v -> (String) v)
                    .orElse("");
            Long userId = state.value("userId")
                .map(
                        a -> (Long) a
                ).orElse(0L);
            log.info("通知开始：userId: {}", userId);
            String title = "请查看";

            NotifierBody notifierBody    = new NotifierBody();
            notifierBody.setTitle(title);
            notifierBody.setContent(content);

            NotifierMessage message=new NotifierMessage();
            message.setNotifierBody(notifierBody);
            message.setNotifierType(0);

            message.setUserId(userId);

            boolean result = send_notify(message);
            log.info("通知结果：{},userId: {}", result ? "成功" : "失败",userId);
            return Map.of();
        }

    private boolean send_notify(NotifierMessage message) {
        return notifierService.send(message);
    }


}
