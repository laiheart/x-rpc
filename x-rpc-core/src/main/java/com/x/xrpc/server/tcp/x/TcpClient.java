package com.x.xrpc.server.tcp.x;

import com.x.xrpc.RpcApplication;
import com.x.xrpc.model.RpcRequest;
import com.x.xrpc.protocol.ProtocolConstant;
import com.x.xrpc.protocol.ProtocolMessage;
import com.x.xrpc.protocol.ProtocolMessageSerializerEnum;
import com.x.xrpc.protocol.ProtocolMessageTypeEnum;
import com.x.xrpc.serializer.SerializerFactory;
import com.x.xrpc.utils.BytesUtils;
import com.x.xrpc.utils.ProtocolMessageUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * Vertx TCP 请求客户端
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">程序员鱼皮的编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
public class TcpClient {
    public Vertx start(String host, int port, Handler<NetSocket> handler, Handler<Throwable> fail) {
        //初始化rpc框架
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient()
                .connect(port, host)
                .onComplete(handler, fail)
        ;
        return vertx;
    }
}
