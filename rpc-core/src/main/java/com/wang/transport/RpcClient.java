package com.wang.transport;

import com.wang.pojo.RpcRequest;

// 客户端类通用接口
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
