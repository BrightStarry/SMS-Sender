package com.zuma.sms.pool;

import com.google.gson.Gson;

import java.util.concurrent.locks.ReentrantLock;

/**
 * author:Administrator
 * datetime:2017/11/13 0013 15:12
 * 实现{@link Gson}对象池,
 */
public class GsonPool extends BaseCommonPool<Gson> {


    @Override
    SimpleObjectFactory<Gson> initCommonPool() {
        return  new SimpleObjectFactory<Gson>() {
            @Override
            Gson create() {
                return new Gson();
            }
        };
    }


    private GsonPool(){}
    private static GsonPool instance;
    private static ReentrantLock lock = new ReentrantLock();
    public static GsonPool getInstance(){
        if(instance == null){
            lock.lock();
            if(instance == null)
                instance = new GsonPool();
            lock.unlock();
        }
        return instance;
    }
}
