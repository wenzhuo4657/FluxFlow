package cn.wenzhuo4657.dailyWeb.tigger.http;


import cn.dev33.satoken.stp.StpUtil;
import cn.wenzhuo4657.dailyWeb.domain.auth.UserService;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.dto.RegisterByOauthDto;
import cn.wenzhuo4657.dailyWeb.domain.auth.model.dto.UserDto;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.res.UserResponse;

import cn.wenzhuo4657.dailyWeb.types.utils.AuthUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller(value = "oauth")
@RequestMapping(value = "/api/oauth")
@ResponseBody
public class AuthController {
    @Autowired(required = false)
    private AuthRequest authRequest;

    @Autowired
    private UserService userService;

    Logger log= org.slf4j.LoggerFactory.getLogger(AuthController.class);

    /**
     * 重定向到 GitHub 授权页面
     */
    @GetMapping("/render/github")
    public void renderGithub(HttpServletResponse response)  {
        try {
            if (authRequest == null) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                response.getWriter().write("GitHub OAuth not configured");
                return;
            }
            String state = AuthStateUtils.createState();
            response.sendRedirect(authRequest.authorize(state));
        }catch (IOException e){

        }

    }
    @Value("${domain.home}")
    private  String name;

    /**
     * GitHub 授权回调
     */
    @GetMapping("/callback/github")
    public  void callbackGithub(AuthCallback callback, HttpServletResponse response) {
        if (authRequest == null) {
            try {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                response.getWriter().write("GitHub OAuth not configured");
            } catch (IOException ignored) {}
            return;
        }
        // 1. 使用 JustAuth 处理回调，获取用户信息
        AuthResponse authResponse = authRequest.login(callback);

        if (authResponse.getCode() == 2000) {
            // 2. 登录成功，获取用户信息
            AuthUser authUser = (AuthUser) authResponse.getData();


//            3,系统缓存登录信息
            RegisterByOauthDto registerByOauthDto = new RegisterByOauthDto();
            registerByOauthDto.setOauth_provider("github");
            registerByOauthDto.setOauth_provider_avatar(authUser.getAvatar());
            registerByOauthDto.setOauth_provider_username(authUser.getUsername());
            registerByOauthDto.setOauth_provider_user_id(authUser.getUuid());
            UserDto user = userService.registerByOauth(registerByOauthDto);

            StpUtil.login(user.getId());

            // 4. 可以将用户信息存入 Sa-Token 的 Session 中，方便后续获取
            AuthUtils.setUserInfo(user);


            log.info("GitHub 用户 {} 登录成功！", authUser.getUsername());

            try {
                String token = StpUtil.getTokenValue();
                // 将UserDto转换为UserResponse，id字段转为String
                UserResponse userResponse = new UserResponse(
                        user.getId().toString(),
                        user.getUsername(),
                        user.getAvatar()
                );
                String userInfoJson = URLEncoder.encode(
                        new ObjectMapper().writeValueAsString(userResponse),
                        StandardCharsets.UTF_8
                );
                response.sendRedirect(name+"/auth/callback?token=" + token + "&userInfo=" + userInfoJson);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout() {
        try {
            // 执行登出逻辑
            log.info("用户 {} 退出登录", AuthUtils.getLoginId());
            StpUtil.logout();
            log.info("用户 {} 登出成功", AuthUtils.getLoginId());
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
