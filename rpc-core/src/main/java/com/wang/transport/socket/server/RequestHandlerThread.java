package com.wang.transport.socket.server;

import com.wang.handler.RequestHandler;
import com.wang.pojo.RpcRequest;
import com.wang.pojo.RpcResponse;
import com.wang.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandlerThread implements Runnable{
    private static final Logger logger =  LoggerFactory.getLogger(RpcServerImpl.class);
    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object resultObject = requestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(resultObject));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("#[error] 调用或发送时发生错误：", e);
        }
    }
}
