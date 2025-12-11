package cn.wenzhuo4657.dailyWeb.tigger.http;


import cn.wenzhuo4657.dailyWeb.domain.Types.ITypesService;

import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.DocsDto;
import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.TypeDto;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.ApiResponse;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.req.GetContentIdsByTypesRequest;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.DocsResponse;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.TypeResponse;
import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
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
    public ResponseEntity<ApiResponse<List<TypeResponse>>> getAllTypes() {
        log.info("userID:{}", AuthUtils.getLoginId());
        List<TypeDto> typeDtos = typesService.getAllTypes(AuthUtils.getLoginId());


        List<TypeResponse> collect = typeDtos.stream()
                .map(dto -> new TypeResponse(dto.getId().toString(), dto.getName()))
                .collect(Collectors.toList());

        ApiResponse<List<TypeResponse>> listApiResponse = ApiResponse.success();
        listApiResponse.setData(collect);

        log.info("userID:{}getAllTypes response:{}", AuthUtils.getLoginId(),collect);
        return ResponseEntity.ok(listApiResponse);
    }


    @RequestMapping(value = "/getContentIdsByTypes")
    public ResponseEntity<ApiResponse<List<DocsResponse>>> getTypesWithItems(@Valid @RequestBody GetContentIdsByTypesRequest request) {

        log.info("userID:{} getContentIdsByTypes request:{}", AuthUtils.getLoginId(),request);
        Long typeId = Long.valueOf(request.getId());
        List<DocsDto> docsDtos = typesService.getContentNameIdById(typeId, AuthUtils.getLoginId());
        List<DocsResponse> docsResponses = docsDtos.stream()
                .map(dto -> new DocsResponse(dto.getId().toString(), dto.getName()))
                .collect(Collectors.toList());
        ApiResponse<List<DocsResponse>> listApiResponse = ApiResponse.success();
        listApiResponse.setData(docsResponses);

        log.info("userID:{}getContentIdsByTypes response:{}", AuthUtils.getLoginId(),docsResponses);
        return ResponseEntity.ok(listApiResponse);
    }

}
