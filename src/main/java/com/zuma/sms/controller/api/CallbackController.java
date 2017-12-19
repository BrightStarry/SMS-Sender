package com.zuma.sms.controller.api;

import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.dto.api.*;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.ZhangYouCallbackMsgTypeEnum;
import com.zuma.sms.enums.error.ChangXiangErrorEnum;
import com.zuma.sms.enums.error.ZhangYouErrorEnum;
import com.zuma.sms.enums.system.ChannelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.zuma.sms.api.processor.CustomProcessorFactory.*;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 11:26
 * 短信平台 回调等
 */
@RestController
@RequestMapping("/api")
public class CallbackController {
	@Autowired
	private ChannelStore channelStore;


	/**
	 * 创蓝异步回调
	 */
	@RequestMapping("/chuanglan/callback")
	public void chuanglanCallback(ChuangLanAPI.AsyncResponse response) {
		Channel channel = channelStore.get(ChannelEnum.CHUANGLAN_YD);
		buildSendSmsCallbackProcessor(channel).process(response, channel);
	}


	/**
	 * 畅想 短信异步回调
	 */
	@RequestMapping("/changxiang/callback")
	public String changxiangCallback(ChangXiangAPI.AsyncResponse response) {
		Channel channel = channelStore.get(ChannelEnum.CHANGXIANG_YD);
		for (ChangXiangAPI.AsyncResponseChild asyncResponseChild : response.getReport()) {
			buildSendSmsCallbackProcessor(channel).process(response, channel);
		}
		return ChangXiangErrorEnum.SUCCESS.getCode();
	}

	/**
	 * 掌游平台异步回调
	 */
	@RequestMapping("/zhangyou/callback")
	public ZhangYouAPI.AsyncResponseReturn zhangyouCallback(ZhangYouAPI.AsyncResponse response) {

		Channel channel = channelStore.get(ChannelEnum.ZHANGYOU_YD);
		//如果为下行结果报告
		if (ZhangYouCallbackMsgTypeEnum.REPORT.getMessage().equals(response.getMsgType())) {
			buildSendSmsCallbackProcessor(channel).process(response, channel);
		}
		//如果为上行推送
		if (ZhangYouCallbackMsgTypeEnum.MO.getMessage().equals(response.getMsgType())) {
			buildSmsUpProcessor(channel).process(response, channel);
		}

		//返回成功
		return new ZhangYouAPI.AsyncResponseReturn(ZhangYouErrorEnum.RESPONSE_SUCCESS, new Date());
	}

	/**
	 * 宽信平台发送短信异步回调
	 */
	@RequestMapping("/kanxin/callback")
	public void kuanxinSendSmsCallback(KuanXinAPI.AsyncResponse response) {
		Channel channel = channelStore.get(ChannelEnum.KUANXIN_YD);
		buildSendSmsCallbackProcessor(channel).process(response,channel);
	}

	/**
	 * 群正平台发送短信异步回调,该回调可能会合并多个平台的数据
	 */
	@RequestMapping("/qunzheng/callback")
	public void qunzhengSendSmsCallback(QunZhengAPI.AsyncResponse response) {
		//循环调用
		Channel channel = channelStore.get(ChannelEnum.QUNZHENG_YD);
		for (QunZhengAPI.AsyncResponseChild asyncResponseChild : response.getSms()) {
			buildSendSmsCallbackProcessor(channel).process(asyncResponseChild,channel);
		}
	}

	/**
	 * 宽信短信上行接口
	 */
	@RequestMapping("/kuanxin/smsup")
	public void kuanxinSmsUp(KuanXinAPI.SmsUpResponse response) {
		Channel channel = channelStore.get(ChannelEnum.KUANXIN_YD);
		buildSmsUpProcessor(channel).process(response,channel);
	}

	/**
	 * 群正短信上行接口
	 */
	@RequestMapping("/qunzheng/smsup")
	public void qunzhengSmsUp(@RequestBody QunZhengAPI.SmsUpResponse response) {
		//循环调用
		Channel channel = channelStore.get(ChannelEnum.QUNZHENG_YD);
		for (QunZhengAPI.SmsUpResponseChild smsUpResponseChild : response.getSms()) {
			buildSmsUpProcessor(channel).process(smsUpResponseChild,channel);
		}

	}

	/**
	 * 畅想短信上行接口
	 */
	@RequestMapping("/changxiang/smsup")
	public String changxiangSmsUp(ChangXiangAPI.SmsUpResponse response) {
		Channel channel = channelStore.get(ChannelEnum.CHANGXIANG_YD);
		buildSmsUpProcessor(channel).process(response,channel);
		return ChangXiangErrorEnum.SUCCESS.getCode();
	}

	/**
	 * 创蓝 短信上行
	 */
	@RequestMapping("/chuanglan/smsup")
	public void chuanglanSmsUp(ChuangLanAPI.SmsUpResponse response) {
		Channel channel = channelStore.get(ChannelEnum.CHUANGLAN_YD);
		buildSmsUpProcessor(channel).process(response,channel);
	}
}
