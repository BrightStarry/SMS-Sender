package com.zuma.sms.pool;

/**
 * author:Administrator
 * datetime:2017/11/8 0008 13:23
 * 工厂类接口
 */
public interface CommonPool<T> {
    T borrow();

    void returnObj(T obj);
}
