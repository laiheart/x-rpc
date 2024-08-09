package com.x.xrpc.utils;

import com.x.xrpc.protocol.ProtocolConstant;
import com.x.xrpc.protocol.ProtocolMessage;
import com.x.xrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author lsx
 * @date 2024-07-27
 */
@Slf4j
public class ProtocolMessageUtils {
    public static byte[] toProtocolBytes(ProtocolMessage<?> protocolMessage, Serializer serializer) {
        Object bodyObj = protocolMessage.getBody();
        byte[] body;
        try {
            body = serializer.serialize(bodyObj);
        } catch (IOException e) {
            log.error("序列化失败", e);
            throw new RuntimeException("序列化失败");
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        header.setBodyLength(body.length);
        byte[] data = new byte[ProtocolConstant.MESSAGE_HEADER_LENGTH + body.length];
        int size = 0;
        data[size++] = header.getMagic();
        data[size++] = header.getVersion();
        data[size++] = header.getSerializer();
        data[size++] = header.getType();
        data[size++] = header.getStatus();
        for (byte b : BytesUtils.longToBytes(header.getRequestId())) {
            data[size++] = b;
        }
        for (byte b : BytesUtils.intToBytes(header.getBodyLength())) {
            data[size++] = b;
        }
        for (byte b : body) {
            data[size++] = b;
        }
        return data;
    }
}
