package cn.wenzhuo4657.dailyWeb.infrastructure.adapter.notifier;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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


@Component
public class ApiServiceHttpImpl  implements  ApiService{

    Logger log= LoggerFactory.getLogger(ApiServiceHttpImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${notifierbot.base-url}")
    private  String apiBase;

    @Override
    public long registerCommunicator(String from, String password, String to) {
        String string = JSONObject.of("from", from, "password", password, "to", to).toString();
        JSONObject jsonObject = JSONObject.of("type", "gmail", "paramsJson", string, "decorators", new String[]{});

        // 创建 ParameterizedTypeReference 来保留完整的泛型类型信息
        ParameterizedTypeReference<ApiResponse<CommunicatorIndexResponse>> typeRef =
                new ParameterizedTypeReference<ApiResponse<CommunicatorIndexResponse>>() {};

        ResponseEntity<ApiResponse<CommunicatorIndexResponse>> responseEntity = restTemplate.exchange(
                apiBase + "/api/v1/notifier/register",
                HttpMethod.POST,
                new HttpEntity<>(jsonObject),
                typeRef
        );

        CommunicatorIndexResponse data = responseEntity.getBody().getData();


        if (responseEntity.getStatusCode().is2xxSuccessful()){
            return data.getCommunicatorIndex();
        }

        return 0;
    }

    @Override
    public boolean sendInfo(long communicatorIndex, String title, String content, File file) {
         // 1. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // 2. 创建 multipart 表单数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 添加普通表单字段
        body.add("communicatorIndex", String.valueOf(communicatorIndex));
        body.add("paramsJson", JSONObject.of("title", title, "content", content).toString());
        body.add("type", "gmail");

        if (file != null) {
            body.add("file", new FileSystemResource(file));
        }

        // 3. 构建完整请求
        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);
        // 4. 发送请求
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                apiBase + "/api/v1/notifier/send",
                requestEntity,
                ApiResponse.class
        );

        // 5. 处理响应
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Notification sent successfully: {}", response.getBody());
            return true;
        }else {
            log.info("Failed to send notification: {}", response.getBody());
            return  false;
        }

    }
}
