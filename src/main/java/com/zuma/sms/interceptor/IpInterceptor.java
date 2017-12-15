package com.zuma.sms.interceptor;

import com.zuma.sms.annotation.Verify;
import com.zuma.sms.config.store.IpAllowStore;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author:Administrator
 * datetime:2017/11/9 0009 16:17
 * ip白名单拦截器
 */
@Component
@Slf4j
public class IpInterceptor implements HandlerInterceptor {


    private static IpAllowStore ipAllow;
    private static String[] ips;

    @Autowired
    public void setIpAllowStoreStore(IpAllowStore ipAllow) {
        ipAllow = ipAllow;
        ips = StringUtils.split(ipAllow.getAllowIp(),",");
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        //如果访问的不是Controller层的方法
        if (!handler.getClass().isAssignableFrom(HandlerMethod.class))
            return true;
        //获取注解
        Verify verify = ((HandlerMethod) handler).getMethodAnnotation(Verify.class);
        //如果为空
        if (verify == null)
            return true;

        //验证ip是否被包含在白名单中
        String ip = getIp(httpServletRequest);
        if (ArrayUtils.contains(ips, ip))
            return true;
        //如果未包含
        log.info("【ip白名单】拦截到未知主机.ip={}",ip);
        throw new SmsSenderException(ErrorEnum.IP_UNALLOW);
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
}
