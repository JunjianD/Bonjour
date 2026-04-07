package com.djj.bj.ai.interfaces;

import com.djj.bj.ai.common.enums.HttpCode;
import com.djj.bj.ai.common.exception.AIException;
import com.djj.bj.ai.common.response.ResponseMessage;
import com.djj.bj.ai.common.response.ResponseMessageFactory;
import com.djj.bj.ai.domain.service.AIInteractiveService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/**
 * AI服务对外提供HTTP接口
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.interfaces
 * @className AIController
 * @date 2026/3/2 23:25
 */
@RestController
@RequestMapping("/ai")
public class AIController {
    @Resource
    private AIInteractiveService aiInteractiveService;

    @RequestMapping("/sendMessage")
    public ResponseMessage<String> sendMessage(String requestData){
        if (StringUtils.isEmpty(requestData)){
            throw new AIException(HttpCode.PARAMS_ERROR);
        }
        try {
            return ResponseMessageFactory.getSuccessResponseMessage(aiInteractiveService.sendMessage(requestData));
        } catch (IOException e) {
            throw new AIException(HttpCode.PROGRAM_ERROR);
        }
    }
}
