package cn.wenzhuo4657.dailyWeb.tigger.http;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.ItemEditService;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.*;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.ApiResponse;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.req.*;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.GetItemsResponse;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.PreViewResponse;
import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ApiResponse<List<GetItemsResponse>>> getItems(@Valid @RequestBody GetItemsRequest params, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    getItems params:{}", loginId, params);
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
                })
                .collect(Collectors.toList());
        log.info("userID: {}    getItems response:   size={} ", loginId, collect.size());
        return ResponseEntity.ok(ApiResponse.success(collect));
    }

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertItem(@Valid @RequestBody InsertItemRequest request, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    insertItem request:{} ", loginId, request);
        InsertItemDto body = new InsertItemDto();
        body.setDocsId(Long.valueOf(request.getDocsId()));
        body.setType(Long.valueOf(request.getType()));
        boolean ok = itemEditService.insertItem(body, loginId);

        log.info("userID: {}    insertItem response:{} ", loginId, ok);
        if (ok)
        return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }

    @PostMapping("/insertWithFields")
    public ResponseEntity<ApiResponse> insertItemWithFields(@Valid @RequestBody InsertItemWithFieldsRequest request, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    insertItemWithFields request:{} ", loginId, request);
        InsertItemDto body = new InsertItemDto();
        body.setDocsId(Long.valueOf(request.getDocsId()));
        body.setType(request.getType());
        boolean ok = itemEditService.insertItem_II(body, loginId, request.getFields());

        log.info("userID: {}    insertItemWithFields response:{} ", loginId, ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse> updateItem(@Valid @RequestBody UpdateItemRequest request, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    updateItem request:{} ", loginId, request);
        UpdateItemDto body = new UpdateItemDto();
        body.setIndex(Long.valueOf(request.getIndex()));
        body.setContent(request.getContent());
        boolean ok = itemEditService.updateItem(body);
        log.info("userID: {}    updateItem response:{} ", loginId, ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> deleteItem(@RequestParam("index") Long index, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    deleteItem request:{} ", loginId, index);
        boolean ok = itemEditService.deleteItem(index);

        log.info("userID: {}    deleteItem response:{} ", loginId, ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }





    @PostMapping("/task/update")
    public ResponseEntity<ApiResponse> updateTask(@Valid @RequestBody UpdateTaskRequest request, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    updateTask request: {}", loginId, request);
        boolean ok = itemEditService.updateTask(request.getTaskId(), request.getTaskStatus(), request.getScore());
        log.info("userID: {}    updateTask response: {}", loginId, ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }

    @PostMapping("/task/finish")
    public ResponseEntity<ApiResponse> finishTask(@RequestParam("taskId") Long taskId, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    finishTask request: taskId={}", loginId, taskId);
        boolean ok = itemEditService.finishTask(taskId);
        log.info("userID: {}    finishTask response: {}", loginId, ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }

    @PostMapping("/task/connectionBase")
    public ResponseEntity<ApiResponse> connectionBase(@RequestParam("taskId") Long taskId, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    connectionBase request: taskId={}", loginId, taskId);
        boolean ok = itemEditService.connectionBase(taskId, loginId);
        log.info("userID: {}    connectionBase response: {}", loginId, ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<PreViewResponse>> queryUserByToday(HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    queryUserByToday request", loginId);
        PreViewDto result = itemEditService.queryUserByToday(loginId);
        log.info("userID: {}    queryUserByToday response: baseItemCount={}, planItemCount={}",
                loginId, result.getBaseItem().size(), result.getPlanItem().size());
        PreViewResponse response=new PreViewResponse(result.getBaseItem(),result.getPlanItem());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/tailadd")
    public ResponseEntity<ApiResponse> tailAdd(@Valid @RequestBody TailAddRequest request, HttpServletRequest httpRequest) throws ClassNotFoundException {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID: {}    tailAdd request: {}", loginId, request);
        boolean ok = itemEditService.tailAdd(Long.valueOf(request.getDocsId()), request.getContent());
        log.info("userID: {}    tailAdd response: {}", loginId, ok);
        if (ok)
            return ResponseEntity.ok(ApiResponse.success());
        else {
            return ResponseEntity.ok(ApiResponse.error());
        }
    }
}
