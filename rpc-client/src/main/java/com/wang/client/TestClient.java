package com.wang.client;

import com.wang.pojo.HelloObject;
import com.wang.service.HelloService;
import com.wang.transport.RpcClientProxy;
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
}
