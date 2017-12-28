package com.zuma.sms.controller.api;

import com.zuma.sms.api.processor.callback.SendSmsCallbackProcessor;
import com.zuma.sms.api.processor.smsup.SmsUpProcessor;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.dto.api.*;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.ZhangYouCallbackMsgTypeEnum;
import com.zuma.sms.enums.error.ChangXiangErrorEnum;
import com.zuma.sms.enums.error.MingFengErrorEnum;
import com.zuma.sms.enums.error.ZhangYouErrorEnum;
import com.zuma.sms.enums.system.ChannelEnum;
import com.zuma.sms.factory.ProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.zuma.sms.factory.ProcessorFactory.*;

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
	@Autowired
	private ProcessorFactory processorFactory;


	/**
	 * 铭锋异步回调
	 */
	@RequestMapping("/mingfeng/callback")
	public void mingfengCallback(@RequestBody MingFengAPI.AsyncResponse response) {
		Channel mingfengChannel = channelStore.get(ChannelEnum.MINGFENG_YD);
		processorFactory.buildSendSmsCallbackProcessor(mingfengChannel).process(response, mingfengChannel);
	}

	/**
	 * 创蓝异步回调
	 */
	@RequestMapping("/chuanglan/callback")
	public void chuanglanCallback(ChuangLanAPI.AsyncResponse response) {
		Channel chuanglanChannel = channelStore.get(ChannelEnum.CHUANGLAN_YD);
		processorFactory.buildSendSmsCallbackProcessor(chuanglanChannel).process(response, chuanglanChannel);
	}


	/**
	 * 畅想 短信异步回调
	 */
	@RequestMapping("/changxiang/callback")
	public String changxiangCallback(ChangXiangAPI.AsyncResponse response) {
		Channel changxiangChannel = channelStore.get(ChannelEnum.CHANGXIANG_YD);
		SendSmsCallbackProcessor processor = processorFactory.buildSendSmsCallbackProcessor(changxiangChannel);
		for (ChangXiangAPI.AsyncResponseChild asyncResponseChild : response.getReport()) {
			processor.process(asyncResponseChild, changxiangChannel);
		}
		return ChangXiangErrorEnum.SUCCESS.getCode();
	}

	/**
	 * 掌游平台异步回调
	 */
	@RequestMapping("/zhangyou/callback")
	public ZhangYouAPI.AsyncResponseReturn zhangyouCallback(@RequestBody ZhangYouAPI.AsyncResponse response) {
		Channel zhangyouChannel = channelStore.get(ChannelEnum.ZHANGYOU_YD);

		//如果为下行结果报告
		if (ZhangYouCallbackMsgTypeEnum.REPORT.getMessage().equals(response.getMsgType())) {
			processorFactory.buildSendSmsCallbackProcessor(zhangyouChannel).process(response, zhangyouChannel);
		}
		//如果为上行推送
		if (ZhangYouCallbackMsgTypeEnum.MO.getMessage().equals(response.getMsgType())) {
			processorFactory.buildSmsUpProcessor(zhangyouChannel).process(response, zhangyouChannel);
		}

		//返回成功
		return new ZhangYouAPI.AsyncResponseReturn(ZhangYouErrorEnum.RESPONSE_SUCCESS, new Date());
	}

	/**
	 * 宽信平台发送短信异步回调
	 */
	@RequestMapping("/kanxin/callback")
	public void kuanxinSendSmsCallback(KuanXinAPI.AsyncResponse response) {
		Channel kuanxinChannel = channelStore.get(ChannelEnum.KUANXIN_YD);
		processorFactory.buildSendSmsCallbackProcessor(kuanxinChannel).process(response, kuanxinChannel);
	}

	/**
	 * 群正平台发送短信异步回调,该回调可能会合并多个平台的数据
	 */
	@RequestMapping("/qunzheng/callback")
	public void qunzhengSendSmsCallback(@RequestBody QunZhengAPI.AsyncResponse response) {
		Channel qunzhengChannel = channelStore.get(ChannelEnum.QUNZHENG_YD);
		SendSmsCallbackProcessor processor = processorFactory.buildSendSmsCallbackProcessor(qunzhengChannel);
		//循环调用
		for (QunZhengAPI.AsyncResponseChild asyncResponseChild : response.getSms()) {
			processor.process(asyncResponseChild,qunzhengChannel);
		}
	}

	/**
	 * 宽信短信上行接口
	 */
	@RequestMapping("/kuanxin/smsup")
	public void kuanxinSmsUp(KuanXinAPI.SmsUpResponse response) {
		Channel kuanxinChannel = channelStore.get(ChannelEnum.KUANXIN_YD);

		processorFactory.buildSmsUpProcessor(kuanxinChannel).process(response,kuanxinChannel);
	}

	/**
	 * 群正短信上行接口
	 */
	@RequestMapping("/qunzheng/smsup")
	public void qunzhengSmsUp(@RequestBody QunZhengAPI.SmsUpResponse response) {
		Channel qunzhengChannel = channelStore.get(ChannelEnum.QUNZHENG_YD);
		SmsUpProcessor processor = processorFactory.buildSmsUpProcessor(qunzhengChannel);
		//循环调用
		for (QunZhengAPI.SmsUpResponseChild smsUpResponseChild : response.getSms()) {
			processor.process(smsUpResponseChild,qunzhengChannel);
		}

	}

	/**
	 * 畅想短信上行接口
	 */
	@RequestMapping("/changxiang/smsup")
	public String changxiangSmsUp(ChangXiangAPI.SmsUpResponse response) {
		Channel changxiangChannel = channelStore.get(ChannelEnum.CHANGXIANG_YD);
		processorFactory.buildSmsUpProcessor(changxiangChannel).process(response,changxiangChannel);
		return ChangXiangErrorEnum.SUCCESS.getCode();
	}

	/**
	 * 创蓝 短信上行
	 */
	@RequestMapping("/chuanglan/smsup")
	public void chuanglanSmsUp(ChuangLanAPI.SmsUpResponse response) {
		Channel chuanglanChannel = channelStore.get(ChannelEnum.CHUANGLAN_YD);
		processorFactory.buildSmsUpProcessor(chuanglanChannel).process(response,chuanglanChannel);
	}

	/**
	 * 铭锋短信上行
	 */
	@RequestMapping("/mingfeng/smsup")
	public String mingfengSmsUp(@RequestBody MingFengAPI.SmsUpResponse response) {
		Channel mingfengChannel = channelStore.get(ChannelEnum.MINGFENG_YD);
		SmsUpProcessor processor = processorFactory.buildSmsUpProcessor(mingfengChannel);
		for (MingFengAPI.SmsUpResponseChild item : response.getChilds()) {
			processor.process(item, mingfengChannel);
		}
		//返回1表示成功
		return MingFengErrorEnum.SUCCESS2.getCode();
	}


}
