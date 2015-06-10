package com.adchina.dp.rpc.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class SerializationHelper {

    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static Objenesis objenesis = new ObjenesisStd();

    private SerializationHelper() {
    }

    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> clz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(1024);

        try {
            Schema<T> schema = getSchema(clz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException("", e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> clz) {
        Schema<T> schema = getSchema(clz);

        try {
            T instance = objenesis.newInstance(clz);
            ProtostuffIOUtil.mergeFrom(data, instance, schema);
            return instance;
        } catch (Exception e) {
            throw new IllegalStateException("", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clz) {
        if (schemaCache.containsKey(clz)) {
            return (Schema<T>) schemaCache.get(clz);
        } else {
            Schema<T> schema = RuntimeSchema.getSchema(clz);
            schemaCache.put(clz, schema);

            return schema;
        }
    }
}
