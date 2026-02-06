package cn.wenzhuo4657.dailyWeb.domain.agent.graph.nodeAction;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.PreViewDto;
import cn.wenzhuo4657.dailyWeb.domain.agent.graph.Constant;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.TodayDataRes;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public   class  ReadLogsNode implements NodeAction {
       private static final Logger log = LoggerFactory.getLogger(ReadLogsNode.class);

        ItemEditService itemEditService;


    public ReadLogsNode(ItemEditService itemEditService) {
        this.itemEditService = itemEditService;
    }

    private static final Long notUserID=-1L;

    @Override
        public Map<String, Object> apply(OverAllState state) throws Exception {
        Long userId = state.value("userId")
                .map(
                        a -> (Long) a
                ).orElse(notUserID);

        if (userId.equals(notUserID)){
            log.error("userId is not valid, userId:{}",userId);
            return Map.of();
        }
        PreViewDto preViewDto = itemEditService.queryUserByToday(userId);
        TodayDataRes res=new TodayDataRes(preViewDto.getBaseItem(),preViewDto.getPlanItem());



//        节点走向判断
        if (res.getBaseItem().isEmpty()&&res.getPlanItem().isEmpty()){
            return Map.of();
        }
        return Map.of("message",res.toString(),"next_node", Constant.ANALYZE_LOGS_AGENT_NAME);
    }
    }
