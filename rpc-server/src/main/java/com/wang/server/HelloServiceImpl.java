package com.wang.server;

import com.wang.pojo.HelloObject;
import com.wang.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 服务器端对接口方法的实现
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    public String hello(HelloObject obj) {
        logger.info("#[info] 接收到消息：{}", obj.getMsg());
        return "id = " + obj.getId();
    }

    public void hello(String name) {
        logger.info("#[info] 接收到消息：{}", name);
    }
}
