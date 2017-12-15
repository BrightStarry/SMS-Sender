package com.zuma.sms.pool;

/**
 * author:Administrator
 * datetime:2017/11/8 0008 13:23
 * 工厂类接口
 * 在项目内部调用普通池对象时,使用该接口调用即可.
 */
public interface CommonPool<T> {
    T borrow();

    void returnObj(T obj);
}
