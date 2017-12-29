package com.zuma.sms.util;

import com.google.gson.JsonElement;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.PhoneMessagePair;
import com.zuma.sms.enums.system.PhoneOperatorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.pool.CommonPool;
import com.zuma.sms.pool.OperatorPatternPoolFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
    private static HttpClientUtil httpClientUtil;

    @Autowired
    private void init(ConfigStore configStore,OperatorPatternPoolFactory factory, HttpClientUtil httpClientUtil){
        PhoneUtil.configStore = configStore;
        PhoneUtil.phonePatternPool = factory.build();
        PhoneUtil.httpClientUtil = httpClientUtil;
    }

    /**
     * 将手机号加****
     */
    public static String encryptPhone(String phone) {
        return phone.substring(0,3) + "****" + phone.substring(7,11);
    }

    /**
     * 生成短连接
     */
    /**
     * 获取短连接
     *
     * @param urls 完整地址
     * @param key 短链关键字
     * @param types 0
     * @return
     * @throws Exception
     */
    public static String getShortUrl(String urls, String key, String types) throws Exception {




        String url = "http://127.0.0.1:8080/ShortLink/action/";// 读写分离本地短链内网地址
        // String url = "http://120.26.88.59:8080/ShortLink/action/";//读写分离本地短链外网地址
        // String url = "http://10.161.159.205:8810/ShortLink/action/";//阿里云内网
        // String url = "http://s.9fan.cn/action/";
        String hash = CodeUtil.stringToMd5(key + types + urls + "9sky");
        ShortUrlObj shortUrlObj = new ShortUrlObj(types, urls, key, hash);
        String jsonResult = httpClientUtil.doPostForString(url, shortUrlObj);


        // long st = new Date().getTime();
        InputStream io = null;
        InputStreamReader ir = null;
        HttpURLConnection conn = null;
        StringBuffer jsonInfo = new StringBuffer();// 存放最终返回的字符串
        try {

            String hash = CodeUtil.stringToMd5(key + types + urls + "9sky");
            URL web = new URL(url);
            conn = (HttpURLConnection) web.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            urls = URLEncoder.encode(urls, "UTF-8");
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            String content = "types=" + types + "&urls=" + urls + "&key=" + key + "&hash=" + hash;
            out.writeBytes(content);
            out.flush();
            out.close();

            io = conn.getInputStream();
            ir = new InputStreamReader(io, "utf-8");
            char[] buf = new char[200];
            int cnt = 0;
            while ((cnt = ir.read(buf, 0, 200)) != -1) {
                jsonInfo.append(buf, 0, cnt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ir != null) {
                ir.close();
            }
            if (io != null) {
                io.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        ObjectMapper om = new ObjectMapper();
        JsonNode json = om.readTree(jsonInfo.toString());
        String address = "";
        try {
            String code = json.get("res").getValueAsText();
            if (code.equalsIgnoreCase("1")) {
                address = json.get("key").getValueAsText();
            }
        } catch (Exception e) {
        }
        return address;
    }

    /**
     * 获取短链接口的参数
     */
    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShortUrlObj {
        private String types;
        private String urls;
        private String key;
        private String hash;
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
