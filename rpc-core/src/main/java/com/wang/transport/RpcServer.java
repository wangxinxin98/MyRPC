package com.wang.transport;

public interface RpcServer {

    void start();

    <T> void publishService(T service, String serviceName);

}
