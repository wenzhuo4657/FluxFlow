package cn.wenzhuo4657.dailyWeb.tigger.http;

import com.xkcoding.http.support.Http;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController  {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);


    /**
     * vue router处理
     *
     * 将所有的动态路由重定向页面和静态资源
     */

    /**
     * 处理Vue应用的主页
     */
    @GetMapping("/")
    public String index() {
        log.info("默认路由至index.html");
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String login(){
        log.info("默认路由至index.html");
        return "forward:/index.html";
    }

    @GetMapping("home")
    public String home() {
        log.info("默认路由至index.html");
        return "forward:/index.html";
    }

    @GetMapping("/auth/callback")
    public String authCallback(
            @RequestParam("token") String token,
            @RequestParam("userInfo") String userInfo)
    {
        log.info("默认路由至index.html");
        return "forward:/index.html?token=" + token + "&userInfo=" + userInfo;
    }

}