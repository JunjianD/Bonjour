package com.djj.bj.ai.domain.chatgpt.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 封装ChatGPT返回的结果中的choices字段的内容
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.domain.chatgpt.model.vo
 * @className ChoiceInfo
 * @date 2026/3/4 22:53
 */
@Getter
@Setter
public class ChoiceInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -1638992890093877363L;

//    @Serial
//    private static final long serialVersionUID = 5370484240556050658L;
//
//    private String text;
//
//    private Integer index;
//
//    private String finish_reason;
//
//    private String logprobs;

    private MessageInfo message; // 对应 JSON 中的 "message" 对象

    // 内部类或独立类
    @Setter
    @Getter
    public static class MessageInfo {
        private String content; // 对应 "content"
    }
}
