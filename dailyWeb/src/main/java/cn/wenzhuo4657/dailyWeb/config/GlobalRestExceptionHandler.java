package cn.wenzhuo4657.dailyWeb.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.wenzhuo4657.dailyWeb.tigger.http.dto.ApiResponse;
import cn.wenzhuo4657.dailyWeb.types.Exception.AppException;
import cn.wenzhuo4657.dailyWeb.types.Exception.ResponseCode;
import cn.wenzhuo4657.dailyWeb.types.utils.ThreadMdcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类，
 */
@RestControllerAdvice
public class GlobalRestExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException e) {
        // 确保当前线程有traceId
        ThreadMdcUtils.setTraceIdIfAbsent();
        log.warn("AppException occurred: {}", e.getMessage(), e);
        return ResponseEntity.ok(ApiResponse.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ApiResponse> handleNotLoginException(NotLoginException e) {
        // 确保当前线程有traceId
        ThreadMdcUtils.setTraceIdIfAbsent();
        log.warn("NotLoginException occurred: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(ApiResponse.error(ResponseCode.NOT_LOGIN.getCode(),ResponseCode.NOT_LOGIN.getInfo()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        // 确保当前线程有traceId
        ThreadMdcUtils.setTraceIdIfAbsent();
        log.error("Exception occurred: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(ApiResponse.error(ResponseCode.programmingError.getCode(), ResponseCode.programmingError.getInfo()));
    }
}
