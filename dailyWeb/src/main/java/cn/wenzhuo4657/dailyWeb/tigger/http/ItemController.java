package cn.wenzhuo4657.dailyWeb.tigger.http;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.*;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.ApiResponse;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.req.*;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.GetItemsResponse;
import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller("item")
@ResponseBody
@Validated
@RequestMapping("/api/item")
public class ItemController {

    Logger log= LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemEditService itemEditService;

    @PostMapping("/get")
    public ResponseEntity<ApiResponse<List<GetItemsResponse>>> getItems(@Valid @RequestBody GetItemsRequest params) {
        log.info("userID: {}    getItems params:{}",AuthUtils.getLoginId(),params);
        Long docsId = Long.valueOf(params.getDocsId());
        int type = params.getType();

        QueryItemDto dto=new QueryItemDto();
        dto.setType(type);
        dto.setDocsId(docsId);

        List<ItemDto> items = itemEditService.getItem(dto);

        List<GetItemsResponse> collect = items.stream()
                .map(item -> {
                    GetItemsResponse response = new GetItemsResponse();
                    response.setIndex(item.getIndex());
                    response.setTitle(item.getTitle());
                    response.setContent(item.getContent());
                    response.setExpand(item.getExpand());
                    return response;
                }).collect(Collectors.toList());
        log.info("userID: {}    getItems response:   size={} ",AuthUtils.getLoginId(),collect.size());
        return ResponseEntity.ok(ApiResponse.success(collect));
    }

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertItem(@Valid @RequestBody InsertItemRequest request) {
        log.info("userID: {}    insertItem request:{} ",AuthUtils.getLoginId(),request);
        InsertItemDto body = new InsertItemDto();
        body.setDocsId(Long.valueOf(request.getDocsId()));
        body.setType(Integer.valueOf(request.getType()));
        boolean ok = itemEditService.insertItem(body, AuthUtils.getLoginId());

        log.info("userID: {}    insertItem response:{} ",AuthUtils.getLoginId(),ok);
        if (ok)
        return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse> updateItem(@Valid @RequestBody UpdateItemRequest request) {
        log.info("userID: {}    updateItem request:{} ",AuthUtils.getLoginId(),request);
        UpdateItemDto body = new UpdateItemDto();
        body.setIndex(Long.valueOf(request.getIndex()));
        body.setContent(request.getContent());
        boolean ok = itemEditService.updateItem(body);
        log.info("userID: {}    updateItem response:{} ",AuthUtils.getLoginId(),ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> deleteItem(@RequestParam("index") Long index) {
        log.info("userID: {}    deleteItem request:{} ",AuthUtils.getLoginId(),index);
        boolean ok = itemEditService.deleteItem(index);

        log.info("userID: {}    deleteItem response:{} ",AuthUtils.getLoginId(),ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }

    @PostMapping("/field/checklist/title")
    public ResponseEntity<ApiResponse> updateChecklist(@Valid @RequestBody UpdateCheckListRequest request) {
        log.info("userID: {}    updateChecklist request:{} ",AuthUtils.getLoginId(),request);

        UpdateCheckListDto body = new UpdateCheckListDto();
        body.setIndex(Long.valueOf(request.getIndex()));
        body.setTitle(request.getTitle());
        boolean ok = itemEditService.CheckList(body);
        log.info("userID: {}    updateChecklist response:{} ",AuthUtils.getLoginId(),ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }

    @PostMapping("field/checklist/finish")
    public ResponseEntity<ApiResponse> finishChecklist(@Valid @RequestBody FinishChecklistRequest request) {
        log.info("userID: {}    finishChecklist request:{} ",AuthUtils.getLoginId(),request);
        Long id = Long.valueOf(request.getId());
        boolean ok = itemEditService.CheckListFinish(id);

        log.info("userID: {}    finishChecklist response:{} ",AuthUtils.getLoginId(),ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }
}
