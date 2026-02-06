package cn.wenzhuo4657.dailyWeb.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.wenzhuo4657.dailyWeb.types.Exception.AppException;
import cn.wenzhuo4657.dailyWeb.types.Exception.ResponseCode;
import cn.wenzhuo4657.dailyWeb.types.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射到assets目录
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        registry.addResourceHandler("auth/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        registry.addResourceHandler("/home/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        registry.addResourceHandler("/login/assets/**")
                .addResourceLocations("classpath:/static/assets/");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 直接检查请求方法，如果是OPTIONS就放行

            try {
                // 使用SaHolder获取请求信息
                if (handler.getClass().getSimpleName().contains("PreFlight")) {
                    return; // OPTIONS预检请求直接放行
                }
                // 其他请求检查access token
                String accessToken = SaHolder.getRequest().getHeader("ACCESS_TOKEN").replace("Bearer ", "").trim();;
                if (accessToken == null || accessToken.isEmpty()) {
                    throw new AppException(ResponseCode.ACCESS_TOKEN_INVALID);
                }
                boolean expired = JwtUtils.isExpired(accessToken);
                if (expired){
                    throw new AppException(ResponseCode.ACCESS_TOKEN_INVALID);
                }
            }catch (Exception e)
            {
                throw new AppException(ResponseCode.ACCESS_TOKEN_INVALID,e.getMessage());
            }

                }))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/oauth/**",
                        "/api/error",
                        "/",
                        "/index.html",
                        "/login/**",
                        "/home/**",
                        "/auth/**",
                        "/assets/**",
                        "/favicon.jpg",
                        "/images/**"
                );
    }
}
