package com.zuma.sms.pool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * author:Administrator
 * datetime:2017/11/13 0013 14:03
 * 简单实现{@link PooledObjectFactory }接口
 */
@Slf4j
public abstract class SimpleObjectFactory<T> implements PooledObjectFactory<T> {
    abstract T create();

    @Override
    public PooledObject<T> makeObject() throws Exception {
        return new DefaultPooledObject<>(create());
    }

    @Override
    public void destroyObject(PooledObject<T> pooledObject) throws Exception {
        T obj = pooledObject.getObject();
        obj = null;
    }

    @Override
    public boolean validateObject(PooledObject<T> pooledObject) {
        return true;
    }

    @Override
    public void activateObject(PooledObject<T> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<T> pooledObject) throws Exception {

    }


}
