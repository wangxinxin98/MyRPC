package com.wang.transport;

import com.wang.pojo.RpcRequest;
import com.wang.serializer.CommonSerializer;

// 客户端类通用接口
public interface NacosRpcClient {
    Object sendRequest(RpcRequest rpcRequest);
    void setSerializer(CommonSerializer serializer);
}
