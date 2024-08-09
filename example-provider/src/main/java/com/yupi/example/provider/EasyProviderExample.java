package com.yupi.example.provider;

import com.x.xrpc.RpcApplication;
import com.x.xrpc.config.RegistryConfig;
import com.x.xrpc.config.RpcConfig;
import com.x.xrpc.model.ServiceMetaInfo;
import com.x.xrpc.registry.LocalRegistry;
import com.x.xrpc.registry.Registry;
import com.x.xrpc.registry.RegistryFactory;
import com.x.xrpc.server.HttpServer;
import com.x.xrpc.server.VertxHttpServer;
import com.yupi.example.common.service.UserService;

/**
 * 简易服务提供者示例
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class EasyProviderExample {

    public static void main(String[] args) throws Exception {
        //Rpc框架初始化
        RpcApplication.init();
        // 注册服务
        String serverName = UserService.class.getName();
        LocalRegistry.register(serverName, UserServiceImpl.class);

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Integer serverPort = rpcConfig.getServerPort();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        ServiceMetaInfo service = new ServiceMetaInfo();
        service.setServiceName(serverName);
        service.setServiceHost("localhost");
        service.setServicePort(serverPort);
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.register(service);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(serverPort);
    }
}
