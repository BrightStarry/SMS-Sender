package com.zuma.sms.api.send;

import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.system.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 10:30
 * 修改短信发送记录所需信息
 * See {@link AbstractSendSmsProcessor#updateRecord(Object, SmsSendRecord)}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateRecordInfo<E extends CodeEnum> {
	//响应对象中的唯一记录号
	private String id;
	//响应对象中的响应码
	private String code;
	//响应对象中的异常消息
	private String message  = "";
	//异常枚举类类型对象
	private Class<E> eClass;
	//异常枚举的成功枚举
	private E successEnum;

	public UpdateRecordInfo(String id, String code, Class<E> eClass, E successEnum) {
		this.id = id;
		this.code = code;
		this.eClass = eClass;
		this.successEnum = successEnum;
	}
}
