package com.zuma.sms.pool;


import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.enums.system.PhoneOperatorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * author:Administrator
 * datetime:2017/11/13 0013 15:55
 * 运营商正则模式对象池工厂
 */
@Component
public class OperatorPatternPoolFactory {

    private static ConfigStore configStore;

    @Autowired
    private void init(ConfigStore configStore){
        OperatorPatternPoolFactory.configStore = configStore;
    }


    //无需线程安全
    private List<CommonPool<Pattern>> pools = new ArrayList<>(3);

    //获取Pattern
    public CommonPool<Pattern> build(PhoneOperatorEnum phoneOperatorEnum){
        return pools.get(phoneOperatorEnum.getCode());
    }


    //构造时,构造三个表达式工厂
    private OperatorPatternPoolFactory(){
        CommonPool<Pattern> dianxinPatternPool = new BaseCommonPool<Pattern>(){
            @Override
            SimpleObjectFactory<Pattern> initCommonPool() {
                return new SimpleObjectFactory<Pattern>() {
                    @Override
                    Pattern create() {
                        return Pattern.compile(configStore.getForCommon("DIANXIN_PHONE_NUMBER_REGEXP"));
                    }
                };
            }
        };

        CommonPool<Pattern> liantongPatternPool = new BaseCommonPool<Pattern>(){
            @Override
            SimpleObjectFactory<Pattern> initCommonPool() {
                return new SimpleObjectFactory<Pattern>() {
                    @Override
                    Pattern create() {
                        return Pattern.compile(configStore.getForCommon("LIANTONG_PHONE_NUMBER_REGEXP"));
                    }
                };
            }
        };

        CommonPool<Pattern> yidongPatternPool = new BaseCommonPool<Pattern>(){
            @Override
            SimpleObjectFactory<Pattern> initCommonPool() {
                return new SimpleObjectFactory<Pattern>() {
                    @Override
                    Pattern create() {
                        return Pattern.compile(configStore.getForCommon("YIDONG_PHONE_NUMBER_REGEXP"));
                    }
                };
            }
        };

        pools.add(yidongPatternPool);
        pools.add(dianxinPatternPool);
        pools.add(liantongPatternPool);
    }

    //单例
    private static OperatorPatternPoolFactory instance;
    private static ReentrantLock lock = new ReentrantLock();
    public static OperatorPatternPoolFactory getInstance(){
        if(instance == null){
            lock.lock();
            if(instance == null)
                instance = new OperatorPatternPoolFactory();
            lock.unlock();
        }
        return instance;
    }
}
