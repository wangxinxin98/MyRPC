package com.wang.transport.nacos.server;

import com.wang.codec.CommonDecoder;
import com.wang.codec.CommonEncoder;
import com.wang.registry.general.CommonServiceRegistryImpl;
import com.wang.registry.nacos.NacosServiceRegistryImpl;
import com.wang.serializer.CommonSerializer;
import com.wang.transport.NacosRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;

// NIO方式服务提供侧
// Netty中重要的是设计模式——责任链模式
public class NacosServer implements NacosRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NacosServer.class);

    private final String host;
    private final int port;

    private final NacosServiceRegistryImpl serviceRegistry;
    private final CommonServiceRegistryImpl serviceProvider;
    private CommonSerializer serializer;

    public NacosServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistryImpl();
        serviceProvider = new CommonServiceRegistryImpl();
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if (serializer == null){
            logger.error("#[error] 未设置序列化器");
            return;
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getInterfaces()[0].getName(),
                new InetSocketAddress(host, port));
        logger.info("#[info] NacosServer -> start");
        start();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer));  // 编码器
                            pipeline.addLast(new CommonDecoder());      // 解码器
                            pipeline.addLast(new NacosServerHandler()); // 数据处理器
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("#[error] 启动服务器时发生错误：", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
