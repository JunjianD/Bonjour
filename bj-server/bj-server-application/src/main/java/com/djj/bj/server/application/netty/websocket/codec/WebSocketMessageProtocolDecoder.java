package com.djj.bj.server.application.netty.websocket.codec;

import com.djj.bj.common.io.model.SendMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * WebSocket消息协议解码器
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.websocket.codec
 * @className WebSocketMessageProtocolDecoder
 * @date 2025/6/16 22:08
 */
public class WebSocketMessageProtocolDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame, List<Object> list) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SendMessage<?> sendMessage = objectMapper.readValue(textWebSocketFrame.text(), SendMessage.class);
        list.add(sendMessage);
    }
}
