package com.wang.pojo;

import com.wang.enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

// 服务端给客户端的响应信息
@Data
public class RpcResponse<T> implements Serializable {
    // 响应状态码
    private Integer statusCode;
    // 响应状态补充信息（失败时）
    private String msg;
    // 响应数据（成功时）
    private T data;

    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code){
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(code.getCode());
        response.setMsg(code.getMessage());
        return response;
    }
}
