package cn.wenzhuo4657.dailyWeb.infrastructure.adapter.notifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * Notifier服务HTTP客户端实现
 * 使用RestTemplate调用远程notifier服务的REST API
 */
@Component
public class ApiServiceHttpImpl implements ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiServiceHttpImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${notifierbot.base-url}")
    private String apiBase;

    // ============ 注册通信器方法实现 ============

    @Override
    public long registerGmailCommunicator(String from, String password, String to, String[] decorators) {
        String url = apiBase + "/api/v1/notifier/register/gmail";

        RegisterGmailCommunicatorRequest request = new RegisterGmailCommunicatorRequest(
                from, password, to, decorators != null ? decorators : new String[0]
        );

        log.info("注册Gmail通信器: url={}, from={}, to={}", url, from, to);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RegisterGmailCommunicatorRequest> httpEntity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<CommunicatorIndexResponse>> typeRef =
                    new ParameterizedTypeReference<ApiResponse<CommunicatorIndexResponse>>() {};

            ResponseEntity<ApiResponse<CommunicatorIndexResponse>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    typeRef
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                ApiResponse<CommunicatorIndexResponse> responseBody = responseEntity.getBody();
                if (responseBody.getData() != null) {
                    Long index = responseBody.getData().getCommunicatorIndex();
                    log.info("Gmail通信器注册成功: index={}", index);
                    return index;
                }
            }

            log.error("Gmail通信器注册失败: statusCode={}, body={}",
                    responseEntity.getStatusCode(), responseEntity.getBody());
            return 0;

        } catch (Exception e) {
            log.error("注册Gmail通信器异常: from={}, to={}, error={}", from, to, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public long registerTgBotCommunicator(String botToken, String[] decorators) {
        String url = apiBase + "/api/v1/notifier/register/tgbot";

        RegisterTgBotCommunicatorRequest request = new RegisterTgBotCommunicatorRequest(
                botToken, decorators != null ? decorators : new String[0]
        );

        log.info("注册Telegram Bot通信器: url={}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RegisterTgBotCommunicatorRequest> httpEntity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<CommunicatorIndexResponse>> typeRef =
                    new ParameterizedTypeReference<ApiResponse<CommunicatorIndexResponse>>() {};

            ResponseEntity<ApiResponse<CommunicatorIndexResponse>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    typeRef
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                ApiResponse<CommunicatorIndexResponse> responseBody = responseEntity.getBody();
                if (responseBody.getData() != null) {
                    Long index = responseBody.getData().getCommunicatorIndex();
                    log.info("Telegram Bot通信器注册成功: index={}", index);
                    return index;
                }
            }

            log.error("Telegram Bot通信器注册失败: statusCode={}, body={}",
                    responseEntity.getStatusCode(), responseEntity.getBody());
            return 0;

        } catch (Exception e) {
            log.error("注册Telegram Bot通信器异常: error={}", e.getMessage(), e);
            return 0;
        }
    }

    // ============ 发送消息方法实现 ============

    @Override
    public boolean sendGmail(long communicatorIndex, String title, String content, String fileUrl) {
        String url = apiBase + "/api/v1/notifier/send/gmail";

        SendGmailRequest request = new SendGmailRequest(communicatorIndex, title, content, fileUrl);

        log.info("发送Gmail邮件: url={}, communicatorIndex={}, title={}",
                url, communicatorIndex, title);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SendGmailRequest> httpEntity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<String>> typeRef =
                    new ParameterizedTypeReference<ApiResponse<String>>() {};

            ResponseEntity<ApiResponse<String>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    typeRef
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                ApiResponse<String> responseBody = responseEntity.getBody();
                if (responseBody.getCode() != null && responseBody.getCode() == 200) {
                    log.info("Gmail邮件发送成功: communicatorIndex={}", communicatorIndex);
                    return true;
                }
            }

            log.error("Gmail邮件发送失败: statusCode={}, body={}",
                    responseEntity.getStatusCode(), responseEntity.getBody());
            return false;

        } catch (Exception e) {
            log.error("发送Gmail邮件异常: communicatorIndex={}, title={}, error={}",
                    communicatorIndex, title, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendGmailWithFile(long communicatorIndex, String title, String content, File file) {
        String url = apiBase + "/api/v1/notifier/send/gmail/file";

        log.info("发送Gmail邮件（带文件）: url={}, communicatorIndex={}, title={}, hasFile={}",
                url, communicatorIndex, title, file != null);

        try {
            // 设置请求头（不需要手动设置 Content-Type，RestTemplate 会自动设置 multipart/form-data）
            HttpHeaders headers = new HttpHeaders();

            // 创建 multipart 表单数据
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 添加表单字段
            body.add("communicatorIndex", String.valueOf(communicatorIndex));
            body.add("title", title);
            body.add("content", content);

            // 添加文件（如果存在）
            if (file != null && file.exists()) {
                body.add("file", new FileSystemResource(file));
                log.info("添加文件附件: fileName={}, size={}", file.getName(), file.length());
            }

            // 构建请求实体
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 发送请求
            ParameterizedTypeReference<ApiResponse<String>> typeRef =
                    new ParameterizedTypeReference<ApiResponse<String>>() {};

            ResponseEntity<ApiResponse<String>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    typeRef
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                ApiResponse<String> responseBody = responseEntity.getBody();
                if (responseBody.getCode() != null && responseBody.getCode() == 200) {
                    log.info("Gmail邮件（带文件）发送成功: communicatorIndex={}", communicatorIndex);
                    return true;
                }
            }

            log.error("Gmail邮件（带文件）发送失败: statusCode={}, body={}",
                    responseEntity.getStatusCode(), responseEntity.getBody());
            return false;

        } catch (Exception e) {
            log.error("发送Gmail邮件（带文件）异常: communicatorIndex={}, title={}, error={}",
                    communicatorIndex, title, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendTgBotMessage(long communicatorIndex, String title, String content, String chatId) {
        String url = apiBase + "/api/v1/notifier/send/tgbot";

        SendTgBotMessageRequest request = new SendTgBotMessageRequest(
                communicatorIndex, title, content, chatId
        );

        log.info("发送Telegram Bot消息: url={}, communicatorIndex={}, title={}, chatId={}",
                url, communicatorIndex, title, chatId);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SendTgBotMessageRequest> httpEntity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<ApiResponse<String>> typeRef =
                    new ParameterizedTypeReference<ApiResponse<String>>() {};

            ResponseEntity<ApiResponse<String>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    typeRef
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                ApiResponse<String> responseBody = responseEntity.getBody();
                if (responseBody.getCode() != null && responseBody.getCode() == 200) {
                    log.info("Telegram Bot消息发送成功: communicatorIndex={}, chatId={}",
                            communicatorIndex, chatId);
                    return true;
                }
            }

            log.error("Telegram Bot消息发送失败: statusCode={}, body={}",
                    responseEntity.getStatusCode(), responseEntity.getBody());
            return false;

        } catch (Exception e) {
            log.error("发送Telegram Bot消息异常: communicatorIndex={}, title={}, error={}",
                    communicatorIndex, title, e.getMessage(), e);
            return false;
        }
    }
}
