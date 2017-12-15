package com.zuma.sms.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * author:Administrator
 * datetime:2017/11/9 0009 15:15
 * 缓存工具类
 */
public class CacheUtil {
    private static final Cache<String,String> cache = CacheBuilder.newBuilder()
            //缓存初始大小
            .initialCapacity(100)
            //缓存并发数,同一时间最多x个线程执行写入操作
            .concurrencyLevel(30)
            //过期时间,在写入后的-可设置为读写后的每次读写刷新过期时间
            .expireAfterWrite(3, TimeUnit.HOURS)
            .build();

    /**
     * 存入对象
     */
    public static <T> void put(String key, T obj){
        cache.put(key,CodeUtil.objectToJsonString(obj));
    }

    /**
     * 取出并删除对象
     */
    public static <T> T getAndDelete(String key, Class<T> tClass){
        T t = get(key, tClass);
        delete(key);
        return t;
    }

    /**
     * 取出对象
     */
    public static <T> T get(String key, Class<T> tClass) {
        return CodeUtil.jsonStringToObject(cache.getIfPresent(key), tClass);
    }


    /**
     * 删除对象
     */
    public static void delete(String key){
        cache.invalidate(key);
    }
}
