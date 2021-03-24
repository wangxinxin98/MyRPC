package com.wang.transport.nacos.server;

import com.wang.handler.NacosRequestHandler;
import com.wang.pojo.RpcRequest;
import com.wang.pojo.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

// Netty服务端侧处理器
// 接收RpcRequest，并且执行调用，将调用结果返回封装成RpcResponse发送出去
public class NacosServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NacosServerHandler.class);
    private static final NacosRequestHandler requestHandler;
    private static final String THREAD_NAME_PROFIX = "nacos-server-handler";
    private static final ExecutorService threadPool;

    static {
        requestHandler = new NacosRequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PROFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest msg) throws Exception {
        threadPool.execute(()->{
            logger.info("#[info] 服务器收到请求：{}", msg);
            Object res = requestHandler.handle(msg);
            ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(res));
            future.addListener(ChannelFutureListener.CLOSE);
            ReferenceCountUtil.release(msg);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("#[error] 处理过程调用时发生错误：");
        cause.printStackTrace();
        ctx.close();
    }
}
