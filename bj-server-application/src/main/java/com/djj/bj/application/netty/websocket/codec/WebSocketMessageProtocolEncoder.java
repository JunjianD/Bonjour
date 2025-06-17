package com.djj.bj.application.netty.websocket.codec;

import com.djj.bj.common.io.model.SendMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * websocket消息协议编码器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.websocket.codec
 * @className WebSocketMessageProtocolEncoder
 * @date 2025/6/16 21:58
 */
public class WebSocketMessageProtocolEncoder extends MessageToMessageEncoder<SendMessage<?>> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SendMessage<?> sendMessage, List<Object> list) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String text = objectMapper.writeValueAsString(sendMessage);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(text);
        list.add(textWebSocketFrame);
    }
}
