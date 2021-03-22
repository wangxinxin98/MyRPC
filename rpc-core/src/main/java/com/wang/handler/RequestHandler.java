package com.wang.handler;

import com.wang.enumeration.ResponseCode;
import com.wang.pojo.RpcRequest;
import com.wang.pojo.RpcResponse;
import com.wang.transport.socket.server.RpcServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    private static final Logger logger =  LoggerFactory.getLogger(RpcServerImpl.class);

    public Object handle(RpcRequest rpcRequest, Object service){
        Object res = null;
        try {
            res = invokeTargetMethod(rpcRequest, service);
            logger.info("#[info] 服务：{}成功调用方法：{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("#[error] 调用或发送时发生错误：", e);
        }
        return res;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
