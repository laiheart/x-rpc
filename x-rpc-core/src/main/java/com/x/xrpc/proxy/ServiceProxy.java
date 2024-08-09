package com.x.xrpc.proxy;

import cn.hutool.core.util.ObjUtil;
import com.x.xrpc.RpcApplication;
import com.x.xrpc.config.RpcConfig;
import com.x.xrpc.constant.RpcConstant;
import com.x.xrpc.fault.retry.RetryStrategy;
import com.x.xrpc.fault.retry.RetryStrategyFactory;
import com.x.xrpc.fault.tolerant.TolerantStrategy;
import com.x.xrpc.fault.tolerant.TolerantStrategyFactory;
import com.x.xrpc.loadbalancer.LoadBalancer;
import com.x.xrpc.loadbalancer.LoadBalancerFactory;
import com.x.xrpc.model.RpcRequest;
import com.x.xrpc.model.RpcResponse;
import com.x.xrpc.model.ServiceMetaInfo;
import com.x.xrpc.protocol.ProtocolConstant;
import com.x.xrpc.protocol.ProtocolMessage;
import com.x.xrpc.protocol.ProtocolMessageSerializerEnum;
import com.x.xrpc.protocol.ProtocolMessageTypeEnum;
import com.x.xrpc.registry.Registry;
import com.x.xrpc.registry.RegistryFactory;
import com.x.xrpc.serializer.Serializer;
import com.x.xrpc.serializer.SerializerFactory;
import com.x.xrpc.server.tcp.x.TcpClient;
import com.x.xrpc.server.tcp.x.XTcpBufferHandlerWrapper;
import com.x.xrpc.utils.ProtocolMessageUtils;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务代理（JDK 动态代理）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    AtomicInteger atomicInteger = new AtomicInteger(0);
    private final static String[] MOCK_IP = {"127.0.0.1", "127.0.0.2", "127.0.0.3"};

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //rpc 协议
        ProtocolMessage<RpcRequest> message = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        message.setHeader(header);
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        String value = RpcApplication.getRpcConfig().getSerializer();
        header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(value).getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        // 指定序列化器
        Serializer serializer = SerializerFactory.getInstance(value);
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                .build();
        message.setBody(rpcRequest);
        // 发送请求
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(rpcRequest.getServiceVersion());
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        List<ServiceMetaInfo> services = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (ObjUtil.isEmpty(services)) {
            throw new RuntimeException("服务未发现");
        }
        String key = rpcConfig.getLoadBalancer();
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(key);
        //设置请求参数
        //一下是模拟参数
        Map<String, Object> map = new HashMap<>();
        map.put("ip", MOCK_IP[atomicInteger.getAndIncrement() % MOCK_IP.length]);
        ServiceMetaInfo findService = loadBalancer.select(map, services);
        log.info("findService, host={}, port={}", findService.getServiceHost(), findService.getServicePort());
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        TcpClient tcpClient = new TcpClient();
        String retryKey = rpcConfig.getRetryStrategy();
        RetryStrategy retry = RetryStrategyFactory.getInstance(retryKey);
        try {
            //重试
            retry.doRetry(() -> {
                Vertx vertx = tcpClient.start(findService.getServiceHost(), findService.getServicePort(),
                        (socket) -> {
                            log.info("connected...");
                            byte[] protocolBytes = ProtocolMessageUtils.toProtocolBytes(message, serializer);
                            //发送消息
                            socket.write(Buffer.buffer(protocolBytes));
                            //处理消息
                            socket.handler(new XTcpBufferHandlerWrapper(buffer -> {
                                byte[] response = buffer.getBytes(ProtocolConstant.MESSAGE_HEADER_LENGTH, buffer.length());
                                RpcResponse rpcResponse = null;
                                try {
                                    rpcResponse = serializer.deserialize(response, RpcResponse.class);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                log.info("rpcResponse: {}", rpcResponse);
                                future.complete(rpcResponse);
                            }));
                        },
                        (res) -> {
                            log.error("connect failed, ", res.getCause());
                            future.complete(new RpcResponse());
                            throw new RuntimeException("连接失败");
                        });
                future.get();
                vertx.close();
                return null;
            });
        } catch (Exception e) {
            //容错
            String tolerantKey = rpcConfig.getTolerantStrategy();
            TolerantStrategy tolerant = TolerantStrategyFactory.getInstance(tolerantKey);
            Map<String, Object> context = new HashMap<>();
            tolerant.doTolerant(context, e);
        }
        RpcResponse rpcResponse = future.get();
        return rpcResponse.getData();
    }
}
