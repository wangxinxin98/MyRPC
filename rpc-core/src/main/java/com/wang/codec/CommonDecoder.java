package com.wang.codec;

import com.wang.enumeration.PackageType;
import com.wang.pojo.RpcRequest;
import com.wang.pojo.RpcResponse;
import com.wang.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// 通用的解码拦截器：把收到的字节序列还原为实际对象
// 包括字段校验、获取数据包类型、取出序列化器的编号以获取正确的反序列化方式、读入length字段来确定数据包的长度、读入正确大小的字节数组并反序列化为对象
public class CommonDecoder extends ReplayingDecoder {
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 1. 字段校验
        int magic = byteBuf.readInt();
        if (magic != MAGIC_NUMBER){
            logger.error("#[error] 不识别的协议包：{}", magic);
            return;
        }
        // 2. 获取数据包类型
        int packageCode = byteBuf.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        } else {
            logger.error("#[error] 不识别的数据包：{}", packageCode);
            return;
        }
        // 3. 取出序列化器的编号以获取正确的反序列化方式
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getSerializer();     // 此处没有使用到code
        if (serializer == null){
            logger.error("#[error] 不识别的反序列化器：{}", packageCode);
            return;
        }
        // 4. 读入length字段来确定数据包的长度
        int len = byteBuf.readInt();
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        Object o = serializer.deserialize(bytes, packageClass);
        list.add(o);
    }
}
