package com.djj.bj.platform.common.model.params;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 群组command
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.group.domain.model
 * @className GroupCommand
 * @date 2025/8/4 05:24
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupParams implements Serializable {
    @Serial
    private static final long serialVersionUID = -3228095215331524121L;
    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 群组id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    public boolean isEmpty() {
        return userId == null || groupId == null;
    }
}
