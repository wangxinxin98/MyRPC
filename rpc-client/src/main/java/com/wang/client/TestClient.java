package com.wang.client;

import com.wang.pojo.HelloObject;
import com.wang.serializer.KryoSerializer;
import com.wang.service.HelloService;
import com.wang.transport.*;
import com.wang.transport.nacos.client.NacosClient;
import com.wang.transport.netty.client.NettyClient;
import org.junit.Test;

public class TestClient {
    // 简单实现，调用方法public String hello(HelloObject obj)
    @Test
    public void testClient01(){
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject hello = new HelloObject(12, "It's a msg!");
        String res = helloService.hello(hello);
        System.out.println(res);
    }

    // 简单实现，调用方法public void hello(String name)
    @Test
    public void testClient02(){
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        String s = "hhh";
        helloService.hello(s);
    }

    // Netty测试
    @Test
    public void testNettyClient(){
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxyByClient proxyByClient = new RpcClientProxyByClient(client);
        HelloService helloService = proxyByClient.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "It's a msg!");
        String res = helloService.hello(object);
        System.out.println(res);
    }

    // Nacos测试
    @Test
    public void testNacosClient(){
        NacosRpcClient client = new NacosClient();
        client.setSerializer(new KryoSerializer());
        NacosRpcClientProxyByClient rpcClientProxy = new NacosRpcClientProxyByClient(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
