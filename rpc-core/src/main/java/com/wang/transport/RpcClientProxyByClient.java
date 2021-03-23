package com.wang.transport;

import com.wang.pojo.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxyByClient implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxyByClient.class);

    private final RpcClient client;

    public RpcClientProxyByClient(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("#[info] 调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(), method.getName(),
                args, method.getParameterTypes());
        return client.sendRequest(rpcRequest);
    }
}

