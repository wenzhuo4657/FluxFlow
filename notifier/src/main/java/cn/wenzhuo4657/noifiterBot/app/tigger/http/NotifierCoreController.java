package cn.wenzhuo4657.noifiterBot.app.tigger.http;

import cn.hutool.core.util.StrUtil;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.INotifierService;
import cn.wenzhuo4657.noifiterBot.app.tigger.http.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 通知器核心控制器
 * 提供通知器的注册、消息发送、状态查询等 HTTP 接口
 */
@RestController
@RequestMapping("/api/v1/notifier")
public class NotifierCoreController {

    private static final Logger logger = LoggerFactory.getLogger(NotifierCoreController.class);

    @Autowired
    private INotifierService notifierService;

    // ============ 注册通信器接口 ============

    /**
     * 注册Gmail通信器
     *
     * @param request 注册请求参数
     * @param httpRequest HTTP请求
     * @return 通信器索引
     */
    @PostMapping("/register/gmail")
    public ApiResponse<CommunicatorIndexResponse> registerGmailCommunicator(
            @RequestBody RegisterGmailCommunicatorRequest request,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("注册Gmail通信器请求: requestId={}, from={}, to={}",
                    requestId, request.getFrom(), request.getTo());

        try {
            String[] decorators = request.getDecorators();
            if (decorators == null) {
                decorators = new String[0];
            }

            long communicatorIndex = notifierService.registerGmailCommunicator(
                request.getFrom(),
                request.getPassword(),
                request.getTo(),
                decorators
            );

            logger.info("Gmail通信器注册成功: requestId={}, communicatorIndex={}",
                       requestId, communicatorIndex);

            CommunicatorIndexResponse response = CommunicatorIndexResponse.success(communicatorIndex);
            ApiResponse<CommunicatorIndexResponse> apiResponse =
                ApiResponse.success("Gmail通信器注册成功", response);
            apiResponse.setRequestId(requestId);
            return apiResponse;

        } catch (Exception e) {
            logger.error("注册Gmail通信器失败: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<CommunicatorIndexResponse> errorResponse =
                ApiResponse.error("Gmail通信器注册失败: " + e.getMessage());
            errorResponse.setRequestId(requestId);
            return errorResponse;
        }
    }

    /**
     * 注册Telegram Bot通信器
     *
     * @param request 注册请求参数
     * @param httpRequest HTTP请求
     * @return 通信器索引
     */
    @PostMapping("/register/tgbot")
    public ApiResponse<CommunicatorIndexResponse> registerTgBotCommunicator(
            @RequestBody RegisterTgBotCommunicatorRequest request,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("注册Telegram Bot通信器请求: requestId={}", requestId);

        try {
            String[] decorators = request.getDecorators();
            if (decorators == null) {
                decorators = new String[0];
            }

            long communicatorIndex = notifierService.registerTgBotCommunicator(
                request.getBotToken(),
                decorators
            );

            logger.info("Telegram Bot通信器注册成功: requestId={}, communicatorIndex={}",
                       requestId, communicatorIndex);

            CommunicatorIndexResponse response = CommunicatorIndexResponse.success(communicatorIndex);
            ApiResponse<CommunicatorIndexResponse> apiResponse =
                ApiResponse.success("Telegram Bot通信器注册成功", response);
            apiResponse.setRequestId(requestId);
            return apiResponse;

        } catch (Exception e) {
            logger.error("注册Telegram Bot通信器失败: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<CommunicatorIndexResponse> errorResponse =
                ApiResponse.error("Telegram Bot通信器注册失败: " + e.getMessage());
            errorResponse.setRequestId(requestId);
            return errorResponse;
        }
    }

    // ============ 发送信息接口 ============

    /**
     * 发送Gmail邮件
     *
     * @param request 发送请求参数
     * @param httpRequest HTTP请求
     * @return 发送结果
     */
    @PostMapping("/send/gmail")
    public ApiResponse<String> sendGmail(
            @RequestBody SendGmailRequest request,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("发送Gmail邮件请求: requestId={}, communicatorIndex={}, title={}",
                    requestId, request.getCommunicatorIndex(), request.getTitle());

        try {
            boolean result = notifierService.sendGmail(
                request.getCommunicatorIndex(),
                request.getTitle(),
                request.getContent(),
                request.getFileUrl()
            );

            if (result) {
                logger.info("Gmail邮件发送成功: requestId={}, communicatorIndex={}",
                           requestId, request.getCommunicatorIndex());
                ApiResponse<String> successResponse = ApiResponse.success("Gmail邮件发送成功", "发送成功");
                successResponse.setRequestId(requestId);
                return successResponse;
            } else {
                logger.warn("Gmail邮件发送失败: requestId={}, communicatorIndex={}",
                           requestId, request.getCommunicatorIndex());
                ApiResponse<String> failResponse = ApiResponse.error("Gmail邮件发送失败");
                failResponse.setRequestId(requestId);
                return failResponse;
            }

        } catch (Exception e) {
            logger.error("发送Gmail邮件异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<String> exceptionResponse =
                ApiResponse.error("发送Gmail邮件异常: " + e.getMessage());
            exceptionResponse.setRequestId(requestId);
            return exceptionResponse;
        }
    }

    /**
     * 发送Gmail邮件（带文件附件）
     * 使用multipart/form-data格式接收文件上传
     *
     * @param communicatorIndex 通信器索引
     * @param title 邮件标题
     * @param content 邮件内容
     * @param file 邮件附件文件
     * @param httpRequest HTTP请求
     * @return 发送结果
     */
    @PostMapping("/send/gmail/file")
    public ApiResponse<String> sendGmailWithFile(
            @RequestParam("communicatorIndex") Long communicatorIndex,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("发送Gmail邮件（带文件）请求: requestId={}, communicatorIndex={}, title={}, hasFile={}",
                    requestId, communicatorIndex, title, file != null && !file.isEmpty());

        try {
            File tempFile = null;
            if (file != null && !file.isEmpty()) {
                // 将上传的文件保存到临时文件
                String originalFilename = file.getOriginalFilename();
                String suffix = originalFilename != null ?
                        originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
                tempFile = File.createTempFile("gmail_upload_", suffix);
                file.transferTo(tempFile);
                logger.info("临时文件已创建: requestId={}, tempFile={}", requestId, tempFile.getAbsolutePath());
            }

            boolean result = notifierService.sendGmailWithFile(
                communicatorIndex,
                title,
                content,
                tempFile
            );

            // 清理临时文件
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                logger.info("临时文件清理: requestId={}, deleted={}", requestId, deleted);
            }

            if (result) {
                logger.info("Gmail邮件（带文件）发送成功: requestId={}, communicatorIndex={}",
                           requestId, communicatorIndex);
                ApiResponse<String> successResponse = ApiResponse.success("Gmail邮件发送成功", "发送成功");
                successResponse.setRequestId(requestId);
                return successResponse;
            } else {
                logger.warn("Gmail邮件（带文件）发送失败: requestId={}, communicatorIndex={}",
                           requestId, communicatorIndex);
                ApiResponse<String> failResponse = ApiResponse.error("Gmail邮件发送失败");
                failResponse.setRequestId(requestId);
                return failResponse;
            }

        } catch (IOException e) {
            logger.error("发送Gmail邮件（带文件）IO异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<String> exceptionResponse =
                ApiResponse.error("文件处理异常: " + e.getMessage());
            exceptionResponse.setRequestId(requestId);
            return exceptionResponse;
        } catch (Exception e) {
            logger.error("发送Gmail邮件（带文件）异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<String> exceptionResponse =
                ApiResponse.error("发送Gmail邮件异常: " + e.getMessage());
            exceptionResponse.setRequestId(requestId);
            return exceptionResponse;
        }
    }

    /**
     * 发送Telegram Bot消息
     *
     * @param request 发送请求参数
     * @param httpRequest HTTP请求
     * @return 发送结果
     */
    @PostMapping("/send/tgbot")
    public ApiResponse<String> sendTgBotMessage(
            @RequestBody SendTgBotMessageRequest request,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("发送Telegram Bot消息请求: requestId={}, communicatorIndex={}, title={}",
                    requestId, request.getCommunicatorIndex(), request.getTitle());

        try {
            boolean result = notifierService.sendTgBotMessage(
                request.getCommunicatorIndex(),
                request.getTitle(),
                request.getContent(),
                request.getChatId()
            );

            if (result) {
                logger.info("Telegram Bot消息发送成功: requestId={}, communicatorIndex={}",
                           requestId, request.getCommunicatorIndex());
                ApiResponse<String> successResponse = ApiResponse.success("Telegram Bot消息发送成功", "发送成功");
                successResponse.setRequestId(requestId);
                return successResponse;
            } else {
                logger.warn("Telegram Bot消息发送失败: requestId={}, communicatorIndex={}",
                           requestId, request.getCommunicatorIndex());
                ApiResponse<String> failResponse = ApiResponse.error("Telegram Bot消息发送失败");
                failResponse.setRequestId(requestId);
                return failResponse;
            }

        } catch (Exception e) {
            logger.error("发送Telegram Bot消息异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<String> exceptionResponse =
                ApiResponse.error("发送Telegram Bot消息异常: " + e.getMessage());
            exceptionResponse.setRequestId(requestId);
            return exceptionResponse;
        }
    }

    // ============ 查询接口 ============

    /**
     * 检查通信器状态
     *
     * @param request 状态查询请求
     * @param httpRequest HTTP请求
     * @return 通信器状态
     */
    @PostMapping("/status")
    public ApiResponse<Boolean> checkCommunicatorStatus(
            @RequestBody CommunicatorStatusRequest request,
            HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("检查通信器状态请求: requestId={}, communicatorIndex={}",
                    requestId, request.getCommunicatorIndex());

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
            logger.error("检查通信器状态异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<Boolean> statusErrorResponse =
                ApiResponse.error("检查状态异常: " + e.getMessage());
            statusErrorResponse.setRequestId(requestId);
            return statusErrorResponse;
        }
    }

    /**
     * 查询支持的通知器类型
     *
     * @param httpRequest HTTP请求
     * @return 支持的通知器列表
     */
    @GetMapping("/support/notifiers")
    public ApiResponse<Map<String, String>> querySupportNotifiers(HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("查询支持的通知器类型: requestId={}", requestId);

        try {
            Map<String, String> notifiers = notifierService.querySupportNotifier();

            logger.info("查询支持的通知器类型完成: requestId={}, count={}",
                       requestId, notifiers != null ? notifiers.size() : 0);

            ApiResponse<Map<String, String>> notifierResponse =
                ApiResponse.success("查询成功", notifiers);
            notifierResponse.setRequestId(requestId);
            return notifierResponse;

        } catch (Exception e) {
            logger.error("查询支持的通知器类型异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<Map<String, String>> notifierErrorResponse =
                ApiResponse.error("查询异常: " + e.getMessage());
            notifierErrorResponse.setRequestId(requestId);
            return notifierErrorResponse;
        }
    }

    /**
     * 查询支持的装饰器类型
     *
     * @param httpRequest HTTP请求
     * @return 支持的装饰器列表
     */
    @GetMapping("/support/decorators")
    public ApiResponse<Map<String, String>> querySupportDecorators(HttpServletRequest httpRequest) {

        String requestId = generateRequestId(httpRequest);
        logger.info("查询支持的装饰器类型: requestId={}", requestId);

        try {
            Map<String, String> decorators = notifierService.querySupportDecorator();

            logger.info("查询支持的装饰器类型完成: requestId={}, count={}",
                       requestId, decorators != null ? decorators.size() : 0);

            ApiResponse<Map<String, String>> decoratorResponse =
                ApiResponse.success("查询成功", decorators);
            decoratorResponse.setRequestId(requestId);
            return decoratorResponse;

        } catch (Exception e) {
            logger.error("查询支持的装饰器类型异常: requestId={}, error={}",
                        requestId, e.getMessage(), e);

            ApiResponse<Map<String, String>> decoratorErrorResponse =
                ApiResponse.error("查询异常: " + e.getMessage());
            decoratorErrorResponse.setRequestId(requestId);
            return decoratorErrorResponse;
        }
    }

    // ============ 工具方法 ============

    /**
     * 生成请求ID
     *
     * @param request HTTP请求
     * @return 请求ID
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
