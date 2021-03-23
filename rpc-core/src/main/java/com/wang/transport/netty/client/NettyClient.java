package com.wang.transport.netty.client;

import com.wang.codec.CommonDecoder;
import com.wang.codec.CommonEncoder;
import com.wang.pojo.RpcRequest;
import com.wang.pojo.RpcResponse;
import com.wang.serializer.KryoSerializer;
import com.wang.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// NIO方式消费侧客户端类
// 配置Netty客户端，发送数据时channel将RpcRequest对象写出并且等待服务器端返回结果
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new KryoSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("#[info] 客户端连接到服务器{}: {}", host, port);
            Channel channel = future.channel();
            if (channel != null){
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                   if (future1.isSuccess()){
                       logger.info("#[info] 客户端发送消息：{}", rpcRequest.toString());
                   } else {
                       logger.error("#[error] 发送消息时发生错误：", future1.cause());
                   }
                });
                channel.closeFuture().sync();
                // 发送是非阻塞的，即发送后会立刻返回而无法获得返回结果
                // 通过AttributeKey的方式阻塞获得返回结果
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            logger.error("#[error] 发送消息时发生错误：", e);
        }
        return null;
    }
}
