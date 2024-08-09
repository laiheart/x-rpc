package com.x.xrpc.server.tcp.x;

import com.x.xrpc.model.RpcRequest;
import com.x.xrpc.model.RpcResponse;
import com.x.xrpc.protocol.*;
import com.x.xrpc.registry.LocalRegistry;
import com.x.xrpc.serializer.Serializer;
import com.x.xrpc.serializer.SerializerFactory;
import com.x.xrpc.utils.ProtocolMessageUtils;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author lsx
 * @date 2024-07-26
 */
@Slf4j
public class XTcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket socket) {
        socket.handler(new XTcpBufferHandlerWrapper((buffer) -> {
            byte[] data = buffer.getBytes();
            byte serializeKey = data[MessageHeaderIndexEnum.SERIALIZE_INDEX.getIndex()];
            ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(serializeKey);
            if (serializerEnum == null) {
                throw new RuntimeException("没有此序列化");
            }
            //处理请求体
            byte[] body = new byte[data.length - ProtocolConstant.MESSAGE_HEADER_LENGTH];
            System.arraycopy(data, ProtocolConstant.MESSAGE_HEADER_LENGTH, body, 0, body.length);
            Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
            RpcRequest rpcRequest;
            try {
                rpcRequest = serializer.deserialize(body, RpcRequest.class);
            } catch (IOException e) {
                log.error("反序列化失败", e);
                throw new RuntimeException("反序列化失败");
            }
            //响应数据
            RpcResponse rpcResponse = new RpcResponse();
            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            //返回协议消息
            ProtocolMessage.Header responseHeader = new ProtocolMessage.Header();
            ProtocolMessage<RpcResponse> responseMsg = new ProtocolMessage<>(responseHeader, rpcResponse);
            responseMsg.setBody(rpcResponse);
            responseHeader.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            responseHeader.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
            byte[] protocolBytes = ProtocolMessageUtils.toProtocolBytes(responseMsg, serializer);
            socket.write(Buffer.buffer(protocolBytes));
        }));
    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
