package com.zuma.sms.pool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * author:Administrator
 * datetime:2017/11/13 0013 14:14
 * 组合CommonPool，简单实现{@link CommonPool}接口
 */
@Slf4j
public abstract class BaseCommonPool<T> implements CommonPool<T> {

    protected GenericObjectPool<T> pool;

    public BaseCommonPool() {
        initPool();
    }

    void initPool(){
        SimpleObjectFactory<T> simpleObjectFactory = initCommonPool();
        pool = new GenericObjectPool<T>(simpleObjectFactory,getPoolConfig());
    }

    //默认获取config类方法，可在子类中重写
    GenericObjectPoolConfig getPoolConfig(){
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(0);
        config.setMaxTotal(30);
        config.setMaxIdle(3);
        return config;
    }

    abstract SimpleObjectFactory<T> initCommonPool();


    @Override
    public T borrow() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            log.error("【pool】对象借用失败.error={}",e.getMessage(),e);
        }
        return null;
    }

    @Override
    public void returnObj(T obj) {
        pool.returnObject(obj);
    }
}
