package com.zuma.sms.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;


/**
 * author:Administrator
 * datetime:2017/11/8 0008 09:56
 * 其他平台调用我。发送短信接口表单验证
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsForm {
    @NotNull(message = "平台Id不能为空")
    @Range(min = 1000, max = 9999,message = "平台id不符合规范")
    private Long platformId;//平台id

    private Integer channel;//通道: 0:掌游；1：宽信；2：群正

    @NotBlank(message = "手机号不能为空")
    @Length(min = 11,message = "手机号不符合规范,小于11位")
    private String phone;//手机号[], 逗号间隔

    @NotBlank(message = "短信消息不能为空")
    private String smsMessage;//短信消息[]， !&间隔

    @NotBlank(message = "签名不能为空")
    @Length(min = 32, max = 32, message = "签名必须为32位")
    private String sign;//签名； 平台key + 手机号 + 当前毫秒数,做MD5，32,小写

    @NotNull(message = "毫秒数不能为空")
    private Long timestamp;
}
