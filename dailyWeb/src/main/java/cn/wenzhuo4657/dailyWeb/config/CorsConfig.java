package cn.wenzhuo4657.dailyWeb.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

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
                        // 其他请求检查登录
                        StpUtil.checkLogin();
                    } catch (Exception e) {
                        // 如果获取请求方法失败，也尝试从异常信息判断
                        if (e.getMessage() != null && e.getMessage().contains("上下文尚未初始化")) {
                            // 如果是上下文问题，且可能是OPTIONS请求，放行
                            return;
                        }
                        throw e;
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
                        "/favicon.jpg"
                );
    }
}
