package com.x.xrpc;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author lsx
 * @date 2024-07-23
 */
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Client client = Client.builder()
                .endpoints("http://106.52.233.35:2379")
                .build();
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("/name".getBytes());
        ByteSequence value = ByteSequence.from("name_value".getBytes());
        //put
//        kvClient.put(key, value);
        //get
        CompletableFuture<GetResponse> response = kvClient.get(key);
        GetResponse rs = response.get();
        System.out.println(rs);
        System.out.println("Hello world!");
    }
}