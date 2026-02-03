package cn.wenzhuo4657.dailyWeb.domain.agent.mcp;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.PreViewDto;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.TodayDataReq;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.TodayDataRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;



public class TodayDataTool {

    private static  Logger log= LoggerFactory.getLogger(TodayDataTool.class);


    public static  class TodayData  implements Function<TodayDataReq, TodayDataRes> {
        ItemEditService itemEditService;

        public TodayData(ItemEditService itemEditService) {
          this.itemEditService=itemEditService;
        }


        @Override
        public TodayDataRes apply(TodayDataReq todayDataReq) {
            log.info("today data req:{}",todayDataReq);
            PreViewDto preViewDto = itemEditService.queryUserByToday(todayDataReq.getUserId());
            TodayDataRes res=new TodayDataRes(preViewDto.getBaseItem(),preViewDto.getPlanItem());
            log.info("today data success");
            return res;
        }
    }



}
