package com.x.xrpc.fault.retry;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author lsx
 * @date 2024-07-29
 */
public class RetryStrategyTest {


    @Test
    public void doRetry() throws Exception {
        RetryStrategy retry = RetryStrategyFactory.getInstance(RetryStrategyKeys.FIXED_INTERVAL);
        retry.doRetry(() -> {
            System.out.println("执行。。。");
            throw new RuntimeException("异常");
        });
    }
}