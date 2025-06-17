package com.djj.bj.application.netty.tcpsocket.codec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.djj.bj.common.io.constants.Constants;
import com.djj.bj.common.io.model.SendMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * TCP消息解码
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.application.netty.tcpsocket.codec
 * @className TcpSocketMessageProtocolDecoder
 * @date 2025/6/16 21:34
 */
public class TcpSocketMessageProtocolDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < Constants.READ_MINIMUM_BYTES) {
            return; // 确保至少有4个字节可读（消息长度）
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt(); // 读取消息长度
        if(byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex(); // 如果可读字节不足，重置读取索引
            return; // 等待更多数据
        }
        ByteBuf contentBuf = byteBuf.readBytes(length);
        String content = contentBuf.toString(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        SendMessage<?> sendMessage = objectMapper.readValue(content, SendMessage.class);
        list.add(sendMessage); // 将解码后的消息添加到输出列表
    }
}
