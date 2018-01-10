package com.zuma.sms.api.resolver;

import com.zuma.sms.dto.PhoneMessagePair;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.PhoneEncryption;
import com.zuma.sms.util.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * author:ZhengXing
 * datetime:2018/1/2 0002 11:40
 * 通用解析器
 */
@Slf4j
public class CommonMessageResolver implements MessageResolver{
	@Override
	public PhoneMessagePair resolve(String phone, String message) {
		try {
			//{phone} 将url中的{phone}替换为加密的手机号.最多支持{url:{xxx}{xxx}{phone}} .url中再内嵌两个以内的变量
			message = message.replaceAll("(.*\\{url:)([^}]*|[^{]*\\{[^}]*}[^{}]*|[^{]*\\{[^}]*}[^{}]*[^{]*\\{[^}]*}[^{}]*)(\\{phone})(.*)",
					"$1$2" + PhoneEncryption.toCiphertext(phone) + "$4");
			//然后再将所有其他位置的{phone}替换为普通手机号
			message = StringUtils.replace(message, "{phone}", phone);
			//{phone:halfshow}替换为中间4位带*的手机号
			message = StringUtils.replace(message, "{phone:halfshow}", PhoneUtil.encryptPhone(phone));
			//{url:xxx}url后面的地址生成短链接
			String url;
			if (StringUtils.isNotBlank((url = resolveShortUrl(message)))) {
				message = message.replaceAll("\\{url:.+}", url);
			}
			return new PhoneMessagePair(phone, message);
		} catch (SmsSenderException e) {
			throw e;
		} catch (Exception e) {
			log.error("[话术解析]未知错误.phone:{},message:{],e:{}", phone, message, e.getMessage(), e);
			throw new SmsSenderException("话术解析异常");
		}
	}

	/**
	 * 截取出{url:},并生成短链
	 * @param message
	 * @return
	 */
	private static String resolveShortUrl(String message){
		int startIndex;
		if((startIndex = message.indexOf("{url:")) != -1){
			int endIndex = message.indexOf("}");
			String url = message.substring(startIndex, endIndex);
			//截取出纯粹的url
			url = url.substring(5, url.length()).trim();
			return PhoneUtil.getShortUrl(url,"","0");
		}
		//如果不存在该表达式,返回空串
		return "";
	}

	public static void main(String[] args) {
		PhoneMessagePair a = new CommonMessageResolver().resolve("17826824998",
				"尊敬的{phone:halfshow}:{phone} 您的12月福利已到账，点击领" +
						"{url:https://tianyiring.com/m/pop/18&02340710130052.html?a={phone}}" +
						" 回N不收此短信【翼铃】");
		System.out.println(a.getMessage());
	}
}
