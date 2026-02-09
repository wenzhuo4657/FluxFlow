package cn.wenzhuo4657.dailyWeb.config.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;


import java.io.IOException;
import java.util.*;

/**
 * 打印http请求和响应的header, 不包含实际数据，
 */
@Component
public class HeaderLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HeaderLoggingFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {



        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(req, res);
        } finally {
            long cost = System.currentTimeMillis() - start;


            String method = request.getMethod();
            String uri = request.getRequestURI() +
                    (request.getQueryString() != null ? "?" + request.getQueryString() : "");


            Map<String, String> reqHeaders = collectHeaders(request);


            int status = res.getStatus();
            Map<String, String> respHeaders = collectHeaders(res);

            log.info("HTTP {} {} | {} ms\nRequestHeaders: \n{}\nResponse({}) Headers:\n{}",
                    method, uri, cost, getHearders(reqHeaders), status, getHearders(respHeaders));

            res.copyBodyToResponse();


        }
    }

    private Map<String, String> collectHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, maskIfSensitive(name, Collections.list(request.getHeaders(name))));
        }
        return map;
    }

    private Map<String, String> collectHeaders(HttpServletResponse response) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String name : response.getHeaderNames()) {
            map.put(name, maskIfSensitive(name, new ArrayList<>(response.getHeaders(name))));
        }
        return map;
    }



    private String maskIfSensitive(String name, List<String> values) {
        return String.join(", ", values);
    }

    private  String   getHearders(Map<String, String> map){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
