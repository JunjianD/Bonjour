package com.djj.bj.ai.domain.chatgpt.model.message;

import com.djj.bj.ai.domain.chatgpt.model.vo.ChoiceInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 和ChatGPT交互的消息模型
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.domain.chatgpt.model.message
 * @className AIMessage
 * @date 2026/3/4 22:52
 */
@Getter
@Setter
public class AIMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 6790097141133369132L;

    private String id;

    private String object;

    private Integer created;

    private String model;

    private List<ChoiceInfo> choices;
}
