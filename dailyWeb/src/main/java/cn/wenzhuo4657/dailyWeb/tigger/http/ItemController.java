package cn.wenzhuo4657.dailyWeb.tigger.http;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.*;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.req.*;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.GetItemsResponse;
import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemEditService itemEditService;

    @PostMapping("/get")
    public ResponseEntity<?> getItems(@Valid @RequestBody GetItemsRequest params) {
//        todo 在目前的逻辑里，前端的item数据展示当中title是唯一的，似乎将其作为一个单独的数据项更好

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
        return ResponseEntity.ok(collect);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertItem(@Valid @RequestBody InsertItemRequest request) {
        InsertItemDto body = new InsertItemDto();
        body.setDocsId(Long.valueOf(request.getDocsId()));
        body.setType(Integer.valueOf(request.getType()));
        boolean ok = itemEditService.insertItem(body, AuthUtils.getLoginId());
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateItem(@Valid @RequestBody UpdateItemRequest request) {
        UpdateItemDto body = new UpdateItemDto();
        body.setIndex(Long.valueOf(request.getIndex()));
        body.setContent(request.getContent());
        boolean ok = itemEditService.updateItem(body);
        return ResponseEntity.ok(Map.of("success", ok));
    }
    @PostMapping("/delete")
    public ResponseEntity<?> deleteItem(@RequestParam("index") Long index) {
        boolean ok = itemEditService.deleteItem(index);
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @PostMapping("/field/checklist/title")
    public ResponseEntity<?> updateChecklist(@Valid @RequestBody UpdateCheckListRequest request) {
        UpdateCheckListDto body = new UpdateCheckListDto();
        body.setIndex(Long.valueOf(request.getIndex()));
        body.setTitle(request.getTitle());
        boolean ok = itemEditService.CheckList(body);
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @PostMapping("field/checklist/finish")
    public ResponseEntity<?> finishChecklist(@Valid @RequestBody FinishChecklistRequest request) {
        Long id = Long.valueOf(request.getId());
        boolean ok = itemEditService.CheckListFinish(id);
        return ResponseEntity.ok(Map.of("success", ok));
    }
}
