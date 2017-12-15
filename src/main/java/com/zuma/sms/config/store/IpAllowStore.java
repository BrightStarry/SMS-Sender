package com.zuma.sms.config.store;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017-11-09 20:21
 * ip白名单
 */
@ConfigurationProperties(prefix = "smsSender.config")
@Component
@Data
public class IpAllowStore {
    private String allowIp;

}
