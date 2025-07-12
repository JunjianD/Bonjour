package com.djj.bj.sdk.domain.annotation;

import com.djj.bj.common.io.enums.ListeningType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监听注解
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.sdk.domain.annotation
 * @interfaceName Listening
 * @date 2025/7/12 15:15
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Listening {
    ListeningType listeningType();
}
