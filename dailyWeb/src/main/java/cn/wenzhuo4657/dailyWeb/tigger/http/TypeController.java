package cn.wenzhuo4657.dailyWeb.tigger.http;


import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemType;
import cn.wenzhuo4657.dailyWeb.domain.Types.ITypesService;
import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.DocsDto;
import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.TypeDto;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.ApiResponse;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.req.AddDocsRequest;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.req.DeleteDocsRequest;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.req.GetContentIdsByTypesRequest;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.DocsResponse;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.TypeResponse;
import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller(value = "/types")
@ResponseBody
@Validated
@RequestMapping(value = "/api/types")
public class TypeController {

    private static final Logger log = LoggerFactory.getLogger(TypeController.class);
    @Autowired
    private ITypesService typesService;



    @RequestMapping(value = "/getAllTypes")
    public ResponseEntity<ApiResponse<List<TypeResponse>>> getAllTypes(HttpServletRequest request) {
        Long loginId = AuthUtils.getLoginId(request);
        log.info("userID:{}", loginId);
        List<TypeDto> typeDtos = typesService.getAllTypes(loginId);
        List<TypeResponse> collect = typeDtos.stream()
                .filter(
                        dto->{

//                            这个类型不用展示，在其他地方直接使用
                            if (dto.getId().equals(DocsItemType.ItemType.StickyNote.getCode())){
                                return false;
                            }
                            return  true;
                        }

                )
                .map(dto -> new TypeResponse(dto.getId().toString(), dto.getName()))

                .collect(Collectors.toList());

        ApiResponse<List<TypeResponse>> listApiResponse = ApiResponse.success();
        listApiResponse.setData(collect);

        log.info("userID:{}getAllTypes response:{}", loginId,collect);
        return ResponseEntity.ok(listApiResponse);
    }


    @RequestMapping(value = "/getContentIdsByTypes")
    public ResponseEntity<ApiResponse<List<DocsResponse>>> getTypesWithItems(@Valid @RequestBody GetContentIdsByTypesRequest request, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID:{} getContentIdsByTypes request:{}", loginId, request);
        Long typeId = Long.valueOf(request.getId());
        List<DocsDto> docsDtos = typesService.getContentNameIdById(typeId, loginId);
        List<DocsResponse> docsResponses = docsDtos.stream()
                .map(dto -> new DocsResponse(dto.getId().toString(), dto.getName()))
                .collect(Collectors.toList());
        ApiResponse<List<DocsResponse>> listApiResponse = ApiResponse.success();
        listApiResponse.setData(docsResponses);

        log.info("userID:{}getContentIdsByTypes response:{}", loginId, docsResponses);
        return ResponseEntity.ok(listApiResponse);
    }

    @RequestMapping(value = "/addDocs")
    public ResponseEntity<ApiResponse<Boolean>> addDocs(@Valid @RequestBody AddDocsRequest request, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID:{} addDocs request:{}", loginId, request);
        Long typeId = Long.valueOf(request.getTypeId());
        boolean result = typesService.addDocs(typeId, loginId, request.getDocsName());
        ApiResponse<Boolean> apiResponse = ApiResponse.success();
        apiResponse.setData(result);
        log.info("userID:{} addDocs response:{}", loginId, result);
        return ResponseEntity.ok(apiResponse);
    }

    @RequestMapping(value = "/deleteDocs")
    public ResponseEntity<ApiResponse<Boolean>> deleteDocs(@Valid @RequestBody DeleteDocsRequest request, HttpServletRequest httpRequest) {
        Long loginId = AuthUtils.getLoginId(httpRequest);
        log.info("userID:{} deleteDocs request:{}", loginId, request);
        Long docsId = Long.valueOf(request.getDocsId());
        boolean result = typesService.deleteDocs(docsId, loginId);
        ApiResponse<Boolean> apiResponse = ApiResponse.success();
        apiResponse.setData(result);
        log.info("userID:{} deleteDocs response:{}", loginId, result);
        return ResponseEntity.ok(apiResponse);
    }




}
