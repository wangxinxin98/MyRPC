package com.wang.transport.nacos.client;

import com.wang.pojo.RpcRequest;
import com.wang.pojo.RpcResponse;
import com.wang.registry.nacos.NacosServiceRegistryImpl;
import com.wang.serializer.CommonSerializer;
import com.wang.transport.NacosRpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

// NIO方式消费侧客户端类
// 配置Netty客户端，发送数据时channel将RpcRequest对象写出并且等待服务器端返回结果
public class NacosClient implements NacosRpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NacosClient.class);
    private static final Bootstrap bootstrap;
    private final NacosServiceRegistryImpl serviceRegistry;
    private CommonSerializer serializer;

    public NacosClient() {
        serviceRegistry = new NacosServiceRegistryImpl();
    }

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        logger.info("#[info] 尝试发送请求：NacosClient -> sendRequest");
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive()) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                result.set(rpcResponse.getData());
            } else {
                logger.error("#[error] channel 未激活！");
                System.exit(0);
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
        }
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
