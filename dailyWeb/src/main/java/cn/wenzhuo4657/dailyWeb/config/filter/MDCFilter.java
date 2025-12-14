package cn.wenzhuo4657.dailyWeb.config.filter;

import cn.wenzhuo4657.dailyWeb.types.utils.ThreadMdcUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class MDCFilter extends OncePerRequestFilter {

//获取具有唯一性的traaceid，用于在日志中标识http请求

    private  final static  String MDC_TRACE_ID="traceId";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        setMdcTreaceID(request);
        filterChain.doFilter(request,response);
        MDC.clear();
    }

//    todo 待更正
//    1,可以从前端透传   2， 使用UUID: 无需业务特性，仅仅是唯一的一串数字
//    MDC似乎是多线程变量副本的，了解底层机制
    private void setMdcTreaceID(HttpServletRequest request) {
        if (getMdcTraceId(request)==null){
            ThreadMdcUtils.setTraceIdIfAbsent();
        }else {
            ThreadMdcUtils.setTraceId(getMdcTraceId(request));
        }
    }

    @Value("${traceID.header.name}")
    private  String  name;

    private  String getMdcTraceId(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(name)).orElse(null);
    }


}
