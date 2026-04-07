import com.alibaba.fastjson2.JSON;
import com.djj.bj.ai.common.constants.AIConstants;
import com.djj.bj.ai.common.enums.HttpCode;
import com.djj.bj.ai.common.exception.AIException;
import com.djj.bj.ai.common.httpclient.HttpClient;
import com.djj.bj.ai.domain.chatgpt.model.message.AIMessage;
import com.djj.bj.ai.domain.chatgpt.model.request.AIRequest;
import com.djj.bj.ai.domain.chatgpt.model.vo.ChoiceInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.List;

/**
 * TODO
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package PACKAGE_NAME
 * @className Test
 * @date 2026/3/6 00:22
 */
public class Test {
    public static String sendMessage(String requestData) throws IOException {
        if (StringUtils.isEmpty(requestData)) {
            throw new AIException(HttpCode.PARAMS_ERROR);
        }

        try (CloseableHttpClient httpClient = HttpClient.createSSLClientDefault()) {

//            HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");
            HttpPost httpPost = new HttpPost("https://z.apiyihe.org/v1/chat/completions");
            httpPost.addHeader(AIConstants.CONTENT_TYPE, AIConstants.APPLICATION_JSON);
            httpPost.addHeader(AIConstants.AUTHORIZATION, AIConstants.BEARER.concat("sk-JEB1U9tnjorHT8dH7aP2fSL5ZCTZ4ixRjAjD5gtwvbcI8Gks"));

//            HttpHost proxy = new HttpHost("http","127.0.0.1",7890);
//            RequestConfig config = RequestConfig.custom()
//                    .setProxy(proxy)
//                    .build();
//            httpPost.setConfig(config);

            AIRequest request = new AIRequest("user",requestData);

            StringEntity entity = new StringEntity(
                    JSON.toJSONString(request),
                    ContentType.create(AIConstants.TEXT_JSON, AIConstants.CHARSET_UTF_8)
            );
            httpPost.setEntity(entity);

            return httpClient.execute(httpPost, response -> {
//                String responseStr = EntityUtils.toString(response.getEntity());
                if (response.getCode() == HttpStatus.SC_OK) {
                    String responseStr = EntityUtils.toString(response.getEntity());
//                    // 打印一下日志，确保收到了数据
//                    System.out.println("接口原始返回: " + responseStr);
                    AIMessage aiMessage = JSON.parseObject(responseStr, AIMessage.class);
                    List<ChoiceInfo> choices = aiMessage.getChoices();

                    if(choices == null || choices.isEmpty()){
                        return "未获取到回复内容";
                    }

                    StringBuilder resultBuilder = new StringBuilder();
                    for (ChoiceInfo choice : choices) {
//                        resultBuilder.append(choice.getText());
                        if(choice.getMessage() != null){
                            resultBuilder.append(choice.getMessage().getContent());
                        }
                    }
                    return resultBuilder.toString();
                } else {
                    throw new AIException(
                            "调用ChatGPT接口异常, 返回的状态码为: " + response.getCode()
//                                    + ", 返回的内容为: " + responseStr
                    );
                }
            });
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(sendMessage("你好"));
    }
}
