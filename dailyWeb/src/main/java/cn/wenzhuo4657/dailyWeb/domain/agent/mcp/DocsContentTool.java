package cn.wenzhuo4657.dailyWeb.domain.agent.mcp;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.ItemDto;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.DocsContentReq;
import cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto.DocsContentRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class DocsContentTool {

    private static Logger log= LoggerFactory.getLogger(DocsContentTool.class);

    public static class DocsContent implements Function<DocsContentReq, DocsContentRes>{

        ItemEditService itemEditService;

        public DocsContent(ItemEditService itemEditService) {
            this.itemEditService=itemEditService;
        }

        @Override
        public DocsContentRes apply(DocsContentReq docsContentReq) {
            log.info("docsContentReq:{}",docsContentReq);
            List<ItemDto> item =
                    itemEditService.getItem(Long.valueOf(docsContentReq.getDocsId()));
            DocsContentRes res=new DocsContentRes();
            res.setItemDtos(item);
            return res;
        }
    }
}
