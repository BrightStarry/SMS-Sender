package com.zuma.sms.util;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.PhoneMessagePair;
import com.zuma.sms.enums.system.PhoneOperatorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.pool.CommonPool;
import com.zuma.sms.pool.OperatorPatternPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * author:Administrator
 * datetime:2017/11/8 0008 13:36
 * 手机号工具类
 */
@Slf4j
@Component
public class PhoneUtil {

    private static ConfigStore configStore;
    private static CommonPool<Pattern> phonePatternPool;

    @Autowired
    private void init(ConfigStore configStore,OperatorPatternPoolFactory factory){
        PhoneUtil.configStore = configStore;
        PhoneUtil.phonePatternPool = factory.build();
    }

    /**
     * 验证手机号码格式是否正确
     */
    public static boolean verifyPhoneFormat(String... phones) {
        Pattern pattern = null;
        try {
            pattern = phonePatternPool.borrow();
            for (String item : phones) {
				if (!pattern.matcher(item).matches())
					return false;
			}
            return true;
        }finally {
            phonePatternPool.returnObj(pattern);
        }
    }


    /**
     * 根据手机号判断其运营商
     */
    public static PhoneOperatorEnum[] getPhoneOperator(String... phones){
        //返回数组
        PhoneOperatorEnum[] result = new PhoneOperatorEnum[phones.length];
        for (int i=0 ;i < phones.length; i++){
            // 责任链模式...
            //如果匹配
            result[i] = RegexpUtil.yidongMatch(phones[i]) ? PhoneOperatorEnum.YIDONG :
                    RegexpUtil.liantongMatch(phones[i]) ? PhoneOperatorEnum.LIANTONG :
                            RegexpUtil.dianxinMatch(phones[i]) ? PhoneOperatorEnum.DIANXIN :
                                    PhoneOperatorEnum.UNKNOWN;
//            //如果运营商未知
//            if(result[i].equals(PhoneOperatorEnum.UNKNOWN)){
//                log.error("【根据手机号判断运营商】运营商未知。phone={}",phones[i]);
//                throw new SmsSenderException(ErrorEnum.PHONE_UNKNOWN);
//            }
        }
        return result;
    }

    /**
     * 一批手机号 group by 运营商类型
     */
    public static Map<PhoneOperatorEnum,List<PhoneMessagePair>> getGroupByOperatorForMap(String[] phones,String[] messages) {
        //返回map
        Map<PhoneOperatorEnum,List<PhoneMessagePair>> resultMap = new HashMap<>(4);
        //存入所有数组
        for (PhoneOperatorEnum item : PhoneOperatorEnum.values()) {
            resultMap.put(item, new LinkedList<PhoneMessagePair>());
        }

        //如果只有一个消息- 只插入手机号,,否则消息会存在太多副本
        if (messages.length == 1) {
            for (String item : phones) {
                //运营商判断
                PhoneOperatorEnum flag = RegexpUtil.yidongMatch(item) ? PhoneOperatorEnum.YIDONG :
                        RegexpUtil.liantongMatch(item) ? PhoneOperatorEnum.LIANTONG :
                                RegexpUtil.dianxinMatch(item) ? PhoneOperatorEnum.DIANXIN :
                                        PhoneOperatorEnum.UNKNOWN;
                resultMap.get(flag).add(new PhoneMessagePair(item));
            }
        }else{
            //否则 - 就是多对多
            for (int i = 0; i < phones.length; i++) {
                //运营商判断
                PhoneOperatorEnum flag = RegexpUtil.yidongMatch(phones[i]) ? PhoneOperatorEnum.YIDONG :
                        RegexpUtil.liantongMatch(phones[i]) ? PhoneOperatorEnum.LIANTONG :
                                RegexpUtil.dianxinMatch(phones[i]) ? PhoneOperatorEnum.DIANXIN :
                                        PhoneOperatorEnum.UNKNOWN;
                resultMap.get(flag).add(new PhoneMessagePair(phones[i],messages[i]));
            }
        }

        return resultMap;
    }

    /**
     * 提取手机号中某个运营商的所有号码
     */
    public static String getPhonesByOperator(String phone,PhoneOperatorEnum phoneOperatorEnum) {
        String separator = configStore.getForCommon("PHONES_SEPARATOR");
        //切割为数组
        String[] phones = StringUtils.split(phone, separator);
        //获取每个手机号对应的运营商数组--这样方法复用只是写的方便，效率稍低
        PhoneOperatorEnum[] phoneOperators = getPhoneOperator(phones);
        //返回
        StringBuilder result = new StringBuilder();
        //循环运营商数组
        for (int i = 0; i < phoneOperators.length; i++) {
            //如果该手机运营商和指定的相同，加入返回值
            if(phoneOperators[i].equals(phoneOperatorEnum)){
                result.append(phones[i]).append(separator);
            }
        }
        //去除最后一个逗号
        result = result.delete(result.length()-1, result.length());
        return result.toString();
    }
}
