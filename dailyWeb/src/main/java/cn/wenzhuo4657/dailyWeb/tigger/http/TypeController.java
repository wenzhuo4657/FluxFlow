package cn.wenzhuo4657.dailyWeb.tigger.http;


import cn.wenzhuo4657.dailyWeb.domain.Types.ITypesService;

import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.DocsDto;
import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.TypeDto;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.GetContentIdsByTypesRequest;
import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller(value = "types")
@ResponseBody
@Validated
@RequestMapping(value = "/types")
public class TypeController {

    @Autowired
    private ITypesService typesService;


    @RequestMapping(value = "/getAllTypes")
    public List<TypeDto> getAllTypes() {
        return typesService.getAllTypes(AuthUtils.getLoginId());
    }


    @RequestMapping(value = "/getContentIdsByTypes")
    public ResponseEntity<?> getTypesWithItems(@Valid @RequestBody GetContentIdsByTypesRequest request) {
        Long typeId = Long.valueOf(request.getId());
        List<DocsDto> result = typesService.getContentNameIdById(typeId, AuthUtils.getLoginId());
        return ResponseEntity.ok(result);
    }

}
