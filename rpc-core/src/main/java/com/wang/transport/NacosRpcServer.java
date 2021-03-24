package com.wang.transport;

import com.wang.serializer.CommonSerializer;

// Nacos服务器类接口
public interface NacosRpcServer {
    void start();

    void setSerializer(CommonSerializer serializer);

    <T> void publishService(Object service, Class<T> serviceClass);
}
