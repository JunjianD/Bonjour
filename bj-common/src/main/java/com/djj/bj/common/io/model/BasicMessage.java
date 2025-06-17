package com.djj.bj.common.io.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 基础消息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.model
 * @className BasicMessage
 * @date 2025/5/26 21:22
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BasicMessage implements Serializable {

    private static final long serialVersionUID = 5206995993787592565L;
    /**
     * 消息目的地
     */
    private String destination;
}
