package cn.wenzhuo4657.noifiterBot.app.tigger.http;

import cn.hutool.core.util.StrUtil;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.INotifierService;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.tigger.http.dto.*;
import cn.wenzhuo4657.noifiterBot.app.types.utils.SnowflakeUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * 通知器核心控制器
 * 提供通知器的注册、消息发送、状态查询等 HTTP 接口
 */
@RestController
@RequestMapping("/api/v1/notifier")
@Validated
public class NotifierCoreController {

    private static final Logger logger = LoggerFactory.getLogger(NotifierCoreController.class);

    @Autowired
    private INotifierService notifierService;

    /**
     * 注册通信器
     */
    @PostMapping("/register")
    public ApiResponse<CommunicatorIndexResponse> registerCommunicator(
            @Validated @RequestBody RegisterCommunicatorRequest request,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("注册通信器请求: requestId={}, request={}", requestId, request);

        try {
            // 验证装饰器数组，防止空数组错误
            String[] decorators = request.getDecorators();
            if (decorators == null || decorators.length == 0) {
                decorators = new String[0];
                logger.debug("未指定装饰器，使用空数组");
            }

            // 注册通信器
            long communicatorIndex = notifierService.registerCommunicator(
                request.getParamsJson(),
                request.getType(),
                decorators
            );

            logger.info("通信器注册成功: requestId={}, communicatorIndex={}", requestId, communicatorIndex);

            CommunicatorIndexResponse response = CommunicatorIndexResponse.success(communicatorIndex);
            ApiResponse<CommunicatorIndexResponse> apiResponse =
                ApiResponse.success("通信器注册成功", response);
            apiResponse.setRequestId(requestId);
            return apiResponse;

        } catch (Exception e) {
            logger.error("注册通信器失败: requestId={}, request={}, error={}",
                        requestId, request, e.getMessage(), e);

            ApiResponse<CommunicatorIndexResponse> errorResponse =
                ApiResponse.error("通信器注册失败: " + e.getMessage());
            errorResponse.setRequestId(requestId);
            return errorResponse;
        }
    }

    /**
     * 发送通信信息
     */
    @PostMapping(value = "/send",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<String> sendInfo(
            @RequestParam("communicatorIndex") Long communicatorIndex,
            @RequestParam("paramsJson") String paramsJson,
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("发送信息请求: requestId={}", requestId);

        File tempFile = null;
        try {
            // 将 MultipartFile 转换为临时文件
            tempFile = Files.createTempFile("upload"+ SnowflakeUtils.getSnowflakeId(), "_" +file.getOriginalFilename()).toFile();
            file.transferTo(tempFile);

            // 直接发送信息，不解析或修改JSON
            boolean result = notifierService.sendInfo(
                communicatorIndex,
                paramsJson,
                type,
                tempFile
            );

            if (result) {
                logger.info("信息发送成功: requestId={}, communicatorIndex={}",
                           requestId, communicatorIndex);
                ApiResponse<String> successResponse = ApiResponse.success("信息发送成功", "发送成功");
                successResponse.setRequestId(requestId);
                return successResponse;
            } else {
                logger.warn("信息发送失败: requestId={}, communicatorIndex={}",
                           requestId, communicatorIndex);
                ApiResponse<String> failResponse = ApiResponse.error("信息发送失败");
                failResponse.setRequestId(requestId);
                return failResponse;
            }

        } catch (Exception e) {
            logger.error("发送信息异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<String> exceptionResponse =
                ApiResponse.error("发送信息异常: " + e.getMessage());
            exceptionResponse.setRequestId(requestId);
            return exceptionResponse;
        } finally {
            // 清理临时文件
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }


    /**
     * 检查通信器状态
     */
    @PostMapping("/status")
    public ApiResponse<Boolean> checkCommunicatorStatus(
            @Validated @RequestBody CommunicatorStatusRequest request,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("检查通信器状态请求: requestId={}, request={}", requestId, request);

        try {
            boolean status = notifierService.checkCommunicatorStatus(
                request.getCommunicatorIndex()
            );

            logger.info("通信器状态检查完成: requestId={}, communicatorIndex={}, status={}",
                       requestId, request.getCommunicatorIndex(), status);

            String message = status ? "通信器正常" : "通信器异常";
            ApiResponse<Boolean> statusResponse = ApiResponse.success(message, status);
            statusResponse.setRequestId(requestId);
            return statusResponse;

        } catch (Exception e) {
            logger.error("检查通信器状态异常: requestId={}, request={}, error={}",
                        requestId, request, e.getMessage(), e);

            ApiResponse<Boolean> statusErrorResponse =
                ApiResponse.error("检查状态异常: " + e.getMessage());
            statusErrorResponse.setRequestId(requestId);
            return statusErrorResponse;
        }
    }





    /**
     * 查询支持的通知器类型
     */
    @GetMapping("/support/notifiers")
    public ApiResponse<Map<String,String>> querySupportNotifiers(HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("查询支持的通知器类型: requestId={}", requestId);

        try {
            Map<String,String> notifiers = notifierService.querySupportNotifier();

            logger.info("查询支持的通知器类型完成: requestId={}, count={}",
                       requestId, notifiers != null ? notifiers.size() : 0);

            ApiResponse<Map<String,String>> notifierResponse =
                ApiResponse.success("查询成功", notifiers);
            notifierResponse.setRequestId(requestId);
            return notifierResponse;

        } catch (Exception e) {
            logger.error("查询支持的通知器类型异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<Map<String,String>> notifierErrorResponse =
                ApiResponse.error("查询异常: " + e.getMessage());
            notifierErrorResponse.setRequestId(requestId);
            return notifierErrorResponse;
        }
    }

    /**
     * 查询支持的装饰器类型
     */
    @GetMapping("/support/decorators")
    public ApiResponse<Map<String,String>> querySupportDecorators(HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("查询支持的装饰器类型: requestId={}", requestId);

        try {
            Map<String,String> decorators = notifierService.querySupportDecorator();

            logger.info("查询支持的装饰器类型完成: requestId={}, count={}",
                       requestId, decorators != null ? decorators.size() : 0);

            ApiResponse<Map<String,String>> decoratorResponse =
                ApiResponse.success("查询成功", decorators);
            decoratorResponse.setRequestId(requestId);
            return decoratorResponse;

        } catch (Exception e) {
            logger.error("查询支持的装饰器类型异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<Map<String,String>> decoratorErrorResponse =
                ApiResponse.error("查询异常: " + e.getMessage());
            decoratorErrorResponse.setRequestId(requestId);
            return decoratorErrorResponse;
        }
    }



    
    /**
     * 生成请求ID
     */
    private String generateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-Id");
        if (StrUtil.isBlank(requestId)) {
            requestId = "req_" + System.currentTimeMillis() + "_" +
                        Thread.currentThread().getId();
        }
        return requestId;
    }
}
