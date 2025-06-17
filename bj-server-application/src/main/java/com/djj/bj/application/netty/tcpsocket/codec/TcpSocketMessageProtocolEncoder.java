package com.djj.bj.application.netty.tcpsocket.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import com.djj.bj.common.io.model.SendMessage;

import java.nio.charset.StandardCharsets;

/**
 * TCP消息编码
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.tcpsocket.codec
 * @className TcpSocketMessageProtocolEncoder
 * @date 2025/6/16 21:16
 */
public class TcpSocketMessageProtocolEncoder extends MessageToByteEncoder<SendMessage<?>> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SendMessage<?> sendMessage, ByteBuf byteBuf) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(sendMessage);
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        // 先写入消息长度，再写入消息内容，避免粘包问题
        byteBuf.writeInt(bytes.length); // 写入消息长度
        byteBuf.writeBytes(bytes); // 写入消息内容
    }
}
