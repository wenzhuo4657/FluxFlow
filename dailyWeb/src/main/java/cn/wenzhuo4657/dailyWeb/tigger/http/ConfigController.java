package cn.wenzhuo4657.dailyWeb.tigger.http;


import cn.wenzhuo4657.dailyWeb.tigger.http.dto.ApiResponse;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.GetItemsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/config")
public class ConfigController {


    private  String backgroundUrl="https://cdn.wenzhuo4657.org/img/2025/12/a1a61cd9c40ef9634219fe41ea93706b.jpg";


    @GetMapping("/background")
    public ResponseEntity<ApiResponse<String>>  getBackgroundUrl(){
        return  ResponseEntity.ok(ApiResponse.success(backgroundUrl));
    }



}
