package com.djj.bj.ai.domain.chatgpt.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 调用ChatGPT接口的参数
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.domain.chatgpt.model.request
 * @className AIRequest
 * @date 2026/3/5 22:58
 */
@NoArgsConstructor
@Getter
public class AIRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8477135444419025854L;
//    private String model = "text-davinci-003";
//    private String model = "gpt-4o-mini";
//    @Setter
//    private String prompt;
//    private Integer temperature = 0;
//    private Integer max_tokens = 1024;
//
//    public AIRequest(String prompt) {
//        this.prompt = prompt;
//    }

    private String model = "gpt-4o-mini";

    // Chat 接口核心：由消息列表组成，而非单一 prompt
    private List<Message> messages;

    private Double temperature = 0.7; // 通常建议用 Double

    @JsonProperty("max_tokens") // 确保序列化为 JSON 时带下划线
    private Integer maxTokens = 1024;

    public AIRequest(String role, String content) {
        this.messages = Collections.singletonList(new Message(role, content));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;    // "system", "user", 或 "assistant"
        private String content;
    }

}
