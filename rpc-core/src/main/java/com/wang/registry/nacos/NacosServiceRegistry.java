package com.wang.registry.nacos;

import java.net.InetSocketAddress;

public interface NacosServiceRegistry {
    // 将服务名称和地址注册进服务注册中心
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    // 根据服务名称从注册中心获取一个服务提供者的地址
    InetSocketAddress lookupService(String serviceName);
}
