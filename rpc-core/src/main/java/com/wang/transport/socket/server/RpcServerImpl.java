package com.wang.transport.socket.server;

import com.wang.handler.RequestHandler;
import com.wang.registry.general.CommonServiceRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServerImpl {
    private static final Logger logger =  LoggerFactory.getLogger(RpcServerImpl.class);
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private final CommonServiceRegistryImpl serviceRegistry;

    public RpcServerImpl(CommonServiceRegistryImpl serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        ArrayBlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void start(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("#[info] 服务器正在启动中...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("#[info] 消费者连接！IP：{}，PORT：{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
