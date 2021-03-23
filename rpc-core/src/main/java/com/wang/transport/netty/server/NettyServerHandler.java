package com.wang.transport.netty.server;

import com.wang.handler.RequestHandler;
import com.wang.pojo.RpcRequest;
import com.wang.pojo.RpcResponse;
import com.wang.registry.DefaultServiceRegistry;
import com.wang.registry.ServiceRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Netty服务端侧处理器
// 接收RpcRequest，并且执行调用，将调用结果返回封装成RpcResponse发送出去
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest msg) throws Exception {
        logger.info("#[info] 服务器接收到请求：{}", msg);
        String interfaceName = msg.getInterfaceName();
        Object service = serviceRegistry.getService(interfaceName);
        Object res = requestHandler.handle(msg, service);
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(RpcResponse.success(res));
        channelFuture.addListener(ChannelFutureListener.CLOSE);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("#[error] 处理过程调用时发生错误：");
        cause.printStackTrace();
        ctx.close();
    }
}
