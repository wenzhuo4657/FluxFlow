package cn.wenzhuo4657.dailyWeb.tigger.http;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.InsertItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.QueryItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.UpdateCheckListDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.UpdateItemDto;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.*;
import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller("item")
@ResponseBody
@Validated
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemEditService itemEditService;

    @PostMapping("/get")
    public ResponseEntity<?> getItems(@Valid @RequestBody GetItemsRequest params) {

        Long docsId = Long.valueOf(params.getDocsId());
        int type = params.getType();

        QueryItemDto dto=new QueryItemDto();
        dto.setType(type);
        dto.setDocsId(docsId);
        return ResponseEntity.ok(itemEditService.getItem(dto));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertItem(@Valid @RequestBody InsertItemRequest request) {
        InsertItemDto body = new InsertItemDto();
        body.setDocsId(Long.valueOf(request.getDocsId()));
        body.setType(request.getType());
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
