package com.yupi.example.consumer;

import com.x.xrpc.RpcApplication;
import com.x.xrpc.proxy.ServiceProxyFactory;
import com.yupi.example.common.model.User;
import com.yupi.example.common.service.UserService;

import java.util.Scanner;

/**
 * 服务消费者示例
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class ConsumerExample {

    public static void main(String[] args) {
        RpcApplication.init();
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("请输入你的姓名：");
            User user = new User();
//            user.setName("in.nextLine()");
            user.setName(in.nextLine());
            // 调用
            User newUser = userService.getUser(user);
            if (newUser != null) {
                System.out.println(newUser.getName());
            } else {
                System.out.println("user == null");
            }
        }

//        // 服务提供者初始化
//        ConsumerBootstrap.init();
//
//        // 获取代理
//        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
//        User user = new User();
//        user.setName("yupi");
//        // 调用
//        User newUser = userService.getUser(user);
//        if (newUser != null) {
//            System.out.println(newUser.getName());
//        } else {
//            System.out.println("user == null");
//        }
    }
}
