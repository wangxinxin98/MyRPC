package com.wang.serializer;

/**
 * 通用的序列化反序列化接口
 *
 * @author ziyang
 */
public interface CommonSerializer {

    static CommonSerializer getSerializer() {
        return new KryoSerializer();
    }

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);
}
