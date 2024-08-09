package com.yupi.example.consumer;

import com.x.xrpc.RpcApplication;
import com.x.xrpc.config.RpcConfig;
import com.x.xrpc.model.ServiceMetaInfo;
import com.x.xrpc.proxy.ServiceProxyFactory;
import com.x.xrpc.registry.Registry;
import com.x.xrpc.registry.RegistryFactory;
import com.yupi.example.common.model.User;
import com.yupi.example.common.service.UserService;

import java.net.SocketTimeoutException;

/**
 * 简易服务消费者示例
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class EasyConsumerExample {

    public static void main(String[] args) throws Exception {
        // 静态代理
//        UserService userService = new UserServiceProxy();

//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        Integer serverPort = rpcConfig.getServerPort();
//        ServiceMetaInfo info = new ServiceMetaInfo();
////        info.setServiceName();
//        info.setServiceHost("localhost");
//        info.setServicePort(serverPort);
//        Registry registry = RegistryFactory.getInstance("etcd");
//        registry.register(info);

        RpcApplication.init();
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("lsx");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
