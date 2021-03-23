package com.wang.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// 客户端给服务端的请求信息
// 服务端需要知道接口名称、方法名称、方法参数类型、方法参数值，才能唯一确定需要调用的方法
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {
    // 待调用接口名称
    private String interfaceName;
    // 待调用方法名称
    private String methodName;
    // 待调用方法参数
    private Object[] parameters;
    // 待调用方法参数类型
    private Class<?>[] paramTypes;
}
