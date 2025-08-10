package com.djj.bj.platform.message.application.listener;

import com.djj.bj.common.cache.distribute.DistributeCacheService;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.enums.ListeningType;
import com.djj.bj.common.io.enums.ResponseType;
import com.djj.bj.common.io.model.SendResult;
import com.djj.bj.platform.common.model.vo.GroupMessageVO;
import com.djj.bj.sdk.domain.Listener.MessageListener;
import com.djj.bj.sdk.domain.annotation.Listening;
import jakarta.annotation.Resource;

/**
 * 监听群聊消息
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.message.application.listener
 * @className GroupMessageListener
 * @date 2025/8/9 22:54
 */
@Listening(listeningType = ListeningType.GROUP_MESSAGE)
public class GroupMessageListener implements MessageListener<GroupMessageVO> {
    @Resource
    private DistributeCacheService distributeCacheService;

    @Override
    public void doProcess(SendResult<GroupMessageVO> result) {
        GroupMessageVO messageInfo = result.getData();
        if (ResponseType.SUCCESS.getCode().equals(result.getCode())) {
            String redisKey = String.join(Constants.REDIS_KEY_SPLIT, Constants.GROUP_MESSAGE_READ_POSITION, messageInfo.getGroupId().toString(), result.getReceiver().getUserId().toString());
            distributeCacheService.set(redisKey, messageInfo.getId());
        }
    }
}
