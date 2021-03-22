package com.wang.registry;

public interface ServiceRegistry {
    // 注册服务信息
    // <T>表示该方法是泛型方法，传入参数有泛型
    <T> void register(T service);
    // 通过服务名称获取服务信息
    Object getService(String serviceName);
}
