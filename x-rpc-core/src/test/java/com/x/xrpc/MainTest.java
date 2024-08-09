package com.x.xrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author lsx
 * @date 2024-07-29
 */
public class MainTest {

    public static void main(String[] args) throws IOException {
        Obj obj = new Obj();
        obj.setA(new Obj());
        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = mapper.writeValueAsBytes(obj);
        Obj obj1 = mapper.readValue(bytes, Obj.class);
        System.out.println(obj1);
    }

    @Test
    public void test() {
    }

    @Data
    static class Obj {
        Object a;
        int b;
    }
}