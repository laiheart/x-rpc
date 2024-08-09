package com.yupi.example.provider;

import com.x.xrpc.RpcApplication;
import com.x.xrpc.config.RegistryConfig;
import com.x.xrpc.config.RpcConfig;
import com.x.xrpc.model.ServiceMetaInfo;
import com.x.xrpc.registry.LocalRegistry;
import com.x.xrpc.registry.Registry;
import com.x.xrpc.registry.RegistryFactory;
import com.x.xrpc.server.tcp.x.TcpServer;
import com.yupi.example.common.service.UserService;

/**
 * 服务提供者示例
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class ProviderExample {

    public static void main(String[] args) throws Exception {
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

        // 启动 tcp 服务
        TcpServer tcpServer = new TcpServer();
        tcpServer.start(serverPort);

//        // 要注册的服务
//        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
//        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
//        serviceRegisterInfoList.add(serviceRegisterInfo);
//
//        // 服务提供者初始化
//        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
