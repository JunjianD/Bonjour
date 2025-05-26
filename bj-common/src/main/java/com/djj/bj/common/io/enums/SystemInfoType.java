package com.djj.bj.common.io.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * 系统命令枚举类型
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.enums
 * @className SystemInfo
 * @date 2025/5/26 17:14
 */
@Getter
@AllArgsConstructor
public enum SystemInfoType {
    LOGIN(0,"登录"),
    HEARTBEAT(1,"心跳"),
    LOGOUT(2,"下线"),
    FORCE_LOGOUT(3,"强制下线"),
    PRIVATE_CHAT(4,"私聊消息"),
    GROUP_CHAT(5,"群聊消息");


    private final Integer code;
    private final String description;

    @Nullable
    public static SystemInfoType fromCode(Integer code){
        for(SystemInfoType tempType :values()){
            if(tempType.code.equals(code)){
                return tempType;
            }
        }
        return null;
    }

}
