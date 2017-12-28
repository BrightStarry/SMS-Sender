package com.zuma.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * author:Administrator
 * datetime:2017/11/9 0009 14:27
 * 发送数据
 * 此数据需要split
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SendData {
    private Integer count = 1;//手机号个数
    private String phones;//手机号
    private String messages;//短信消息

    public SendData(String phones, String messages) {
        this.phones = phones;
        this.messages = messages;
    }
}
