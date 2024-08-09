package com.x.xrpc.utils;

import java.nio.ByteBuffer;

/**
 * @author lsx
 * @date 2024-07-27
 */
public class BytesUtils {
    public static byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(value);
        return buffer.array();
    }

    public static byte[] intToBytes(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(value);
        return buffer.array();
    }
}
