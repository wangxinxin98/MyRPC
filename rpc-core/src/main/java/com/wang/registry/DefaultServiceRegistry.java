package com.wang.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// 默认的注册表类
public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    @Override
    public synchronized <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            logger.error("#[error] 该服务未实现任何接口");
            return;
        }
        for (Class<?> i: interfaces){
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("#[info] 向接口：{}注册服务：{}", interfaces, serviceName);
    }

    @Override
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null){
            logger.error("#[error] 找不到该服务");
            return null;
        }
        return service;
    }
}
