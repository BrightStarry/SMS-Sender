package com.zuma.sms.pool;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * author:Administrator
 * datetime:2017/11/13 0013 15:12
 * 实现{@link Gson}对象池,
 */
@Component
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

}
