package com.x.xrpc.serializer;


import com.x.xrpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 序列化器工厂（工厂模式，用于获取序列化器对象）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        log.info("get serializer by key: {}", key);
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
