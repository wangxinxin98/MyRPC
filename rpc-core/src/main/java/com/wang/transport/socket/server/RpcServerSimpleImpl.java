package com.wang.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServerSimpleImpl {
    private final ExecutorService threadPool;
    private static final Logger logger =  LoggerFactory.getLogger(RpcServerImpl.class);

    public RpcServerSimpleImpl() {
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        ArrayBlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void register(int port, Object service) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("#[info] 服务器正在启动中...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("#[info] 客户端连接！IP为：" + socket.getInetAddress());
                threadPool.execute(new WorkerThread(socket, service));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
