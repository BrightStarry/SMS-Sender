package com.zuma.sms.util;

import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017/11/7 0007 15:43
 * httpClient工具类
 */
@Slf4j
@Component
public class HttpClientUtil {


    //连接池对象
    private PoolingHttpClientConnectionManager pool = null;

    //请求配置
    private RequestConfig requestConfig;

    //连接池连接最大数
    private final Integer maxConnectionNum = 10;
    //最大路由，
    //这里route的概念可以理解为 运行环境机器 到 目标机器的一条线路。
    // 举例来说，我们使用HttpClient的实现来分别请求 www.baidu.com 的资源和 www.bing.com 的资源那么他就会产生两个route。
    //如果设置成200.那么就算上面的MAX_CONNECTION_NUM设置成9999，对同一个网站，也只会有200个可用连接
    private final Integer maxPerRoute = 10;
    //握手超时时间
    private final Integer socketTimeout = 10000;
    //连接请求超时时间
    private final Integer connectionRequestTimeout = 10000;
    //连接超时时间
    private final Integer connectionTimeout = 10000;


    /**
     * 发送post请求，返回String
     */
    public <T> String doPostForString(String url, T obj){
        CloseableHttpResponse response = null;
        String result;
        try {
            //发送请求返回response
            response = doPost(url, obj);
            //response 转 string
            result = responseToString(response);
        } finally {
            //关闭
            closeResponseAndIn(null,response);
        }


        return result;
    }

    /**
     * 发起post请求,根据url，参数实体
     */
    public <T> CloseableHttpResponse doPost(String url, T obj) {
        //实体类转map
        Map<String, String> map = null;
        try {
            map = BeanUtils.describe(obj);
        } catch (IllegalAccessException |InvocationTargetException |NoSuchMethodException e) {
            log.error("【httpClient】实体类转map失败.error={}",e.getMessage(),e);
            throw new SmsSenderException(ErrorEnum.HTTP_ERROR);
        }
        //遍历map将其将如paramList
        List<NameValuePair> params = new ArrayList<>();
        for(Map.Entry<String,String> each : map.entrySet()){
            params.add(new BasicNameValuePair(each.getKey(),each.getValue()));
        }
        //放入请求参数中
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));

        CloseableHttpResponse response;
        try {
            response = getHttpClient().execute(httpPost);
        } catch (Exception e) {
            log.error("【httpClient】发送请求失败.Exception={}", e.getMessage(), e);
            throw new SmsSenderException(ErrorEnum.HTTP_ERROR);
        }
        return response;
    }


    /**
     * 从response 中取出 html String
     * 如果没有访问成功，返回null
     */
    private String responseToString(CloseableHttpResponse response) {
        if (isSuccess(response)) {
            try {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (IOException e) {
                log.error("【httpClient】response转String,发生io异常.error={}",e.getMessage(),e);
                throw new SmsSenderException(ErrorEnum.HTTP_RESPONSE_IO_ERROR);
            }
        }
        //这句不可能执行到...，返回值不会为null
        return null;
    }

    /**
     * 校验是否请求成功
     */
    private boolean isSuccess(CloseableHttpResponse response) {
        boolean flag = null != response && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        //成功直接返回
        if(flag)
            return flag;

        //如果失败，记录日志，关闭response，抛出异常
        log.error("【httpClient】请求成功，但状态码非200，状态码:{}",response.getStatusLine().getStatusCode());
        closeResponseAndIn(null, response);
        throw new SmsSenderException(ErrorEnum.HTTP_STATUS_CODE_ERROR);
    }



    /**
     * 关闭  in 和 response
     */
    public void closeResponseAndIn(InputStream inputStream, CloseableHttpResponse response) {
        try {
            @Cleanup
            InputStream temp1 = inputStream;
            @Cleanup
            CloseableHttpResponse temp2 = response;
        } catch (Exception e) {
            log.error("【httpClient】关闭response失败.error={}",e.getMessage(),e);
            //不抛出异常
        }
    }

    /**
     * 获取HttpClient
     */
    public CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                //设置连接池
                .setConnectionManager(pool)
                //请求配置
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    /**
     * 私有化构造方法，构造时，创建对应的连接池实例
     * 使用连接池管理HttpClient可以提高性能
     */
    private HttpClientUtil() {
        try {
            /**
             * 初始化连接池
             */
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                    sslContextBuilder.build());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", socketFactory)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();
            pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            pool.setMaxTotal(maxConnectionNum);
            pool.setDefaultMaxPerRoute(maxPerRoute);

            /**
             * 初始化请求配置
             */
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeout)
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .build();
        } catch (Exception e) {
            log.error("【httpClient】连接池创建失败!");
        }
    }


}
