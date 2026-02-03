package cn.wenzhuo4657.dailyWeb.domain.agent.mcp;

import cn.wenzhuo4657.dailyWeb.domain.notifier.NotifierService;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.TgBotNotifierReq;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.TgBotNotifierRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class TgBotNotifierTool {

    private Logger log= LoggerFactory.getLogger(TgBotNotifierTool.class);

    public static class TgBotNotifier implements Function<TgBotNotifierReq, TgBotNotifierRes> {

        private NotifierService notifierService;

        public TgBotNotifier(NotifierService notifierService) {
            this.notifierService = notifierService;
        }

        @Override
        public TgBotNotifierRes apply(TgBotNotifierReq tgBotNotifierReq) {
            return null;
        }
    }


}
