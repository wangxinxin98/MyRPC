package com.wang.server;

import com.wang.registry.DefaultServiceRegistry;
import com.wang.transport.netty.server.NettyServer;
import com.wang.transport.socket.server.RpcServerImpl;
import com.wang.transport.socket.server.RpcServerSimpleImpl;
import org.junit.Test;

public class TestServer {
    // 简单实现
    @Test
    public void testServer01(){
        HelloServiceImpl helloService = new HelloServiceImpl();
        RpcServerSimpleImpl rpcServer = new RpcServerSimpleImpl();
        rpcServer.register(9000, helloService);
    }

    // 注册多个服务
    @Test
    public void testServer02(){
        HelloServiceImpl helloService = new HelloServiceImpl();
        DefaultServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServerImpl rpcServer = new RpcServerImpl(serviceRegistry);
        rpcServer.start(9000);
    }

    // Netty测试
    @Test
    public void testNettyServer(){
        HelloServiceImpl helloService = new HelloServiceImpl();
        DefaultServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }
}
