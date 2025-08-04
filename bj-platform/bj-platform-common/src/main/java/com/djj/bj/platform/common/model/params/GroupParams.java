package com.djj.bj.platform.common.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class GroupParams {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 群组id
     */
    private Long groupId;

    public boolean isEmpty() {
        return userId == null || groupId == null;
    }
}
