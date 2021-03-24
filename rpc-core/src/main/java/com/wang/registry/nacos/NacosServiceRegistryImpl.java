package com.wang.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.List;

// Nacos服务注册中心
public class NacosServiceRegistryImpl implements NacosServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistryImpl.class);
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.error("#[error] 注册服务时发生错误：", e);
            return;
        }
        logger.info("#[info] 注册服务：{}", serviceName);
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            logger.info("#[info] 尝试发现服务：{}", serviceName);
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = instances.get(0);
            logger.info("#[info] IP-Port: {}-{}", instance.getIp(), instance.getPort());
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("#[error] 获取服务时发生错误：", e);
        }
        return null;
    }
}
