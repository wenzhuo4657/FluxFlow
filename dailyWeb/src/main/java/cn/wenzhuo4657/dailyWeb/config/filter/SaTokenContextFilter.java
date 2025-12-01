package cn.wenzhuo4657.dailyWeb.config.filter;

import cn.dev33.satoken.servlet.util.SaTokenContextJakartaServletUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * SaTokenContext 上下文初始化过滤器
 */
@Order(SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER)
@WebFilter(urlPatterns = "/*")
@Component
public class SaTokenContextFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        try {
            // 初始化 Sa-Token 上下文
            SaTokenContextJakartaServletUtil.setContext(
                (HttpServletRequest) request,
                (HttpServletResponse) response
            );
            chain.doFilter(request, response);
        } finally {
            // 清理上下文
            SaTokenContextJakartaServletUtil.clearContext();
        }
    }
}
