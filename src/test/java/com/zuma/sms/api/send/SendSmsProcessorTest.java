package com.zuma.sms.api.send;

import com.zuma.sms.factory.ProcessorFactory;
import com.zuma.sms.api.processor.send.*;
import com.zuma.sms.api.socket.CMPPConnectionManager;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.system.ChannelEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 13:38
 * 发送接口测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class SendSmsProcessorTest {

	@Autowired
	private ChuangLanSendSmsProcessor chuangLanSendSmsProcessor;

	@Autowired
	private ChuangLanVariateSendSmsProcessor chuangLanVariateSendSmsProcessor;

	@Autowired
	private MingFengSendSmsProcessor mingFengSendSmsProcessor;

	@Autowired
	private ZhangYouSendSmsProcessor zhangYouSendSmsProcessor;

	@Autowired
	private QunZhengSendSmsProcessor qunZhengSendSmsProcessor;

	@Autowired
	private ProcessorFactory factory;

	@Autowired
	private ChannelStore channelStore;

	@Test
	public void testSendSms() throws InterruptedException {
		// 创蓝 成功
//		chuangLanVariateSendSmsProcessor.process(channelStore.get(1014L),"17826824998,https://www.baidu.com;13588809885,https://www.baidu.com;","温馨提醒：尊敬的移动用户，您有1份专属壕礼，点击领取{$var} 回N不收此短信【口袋铃声】",1000L);

		//铭锋 用户名错误
//		mingFengSendSmsProcessor.process(channelStore.get(ChannelEnum.MINGFENG_YD), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//掌游 业务代码不存在
//		zhangYouSendSmsProcessor.process(channelStore.get(ChannelEnum.ZHANGYOU_YD), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//群正 成功
//		qunZhengSendSmsProcessor.process(channelStore.get(ChannelEnum.QUNZHENG_YD), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//宽信 用户不存在
//		factory.buildSendSmsProcessor(channelStore.get(ChannelEnum.KUANXIN_YD)).process(channelStore.get(ChannelEnum.KUANXIN_YD), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//畅想 成功
//		factory.buildSendSmsProcessor(channelStore.get(ChannelEnum.CHANGXIANG_YD)).process(channelStore.get(ChannelEnum.CHANGXIANG_YD), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//宽信cmpp
//		factory.buildSendSmsProcessor(channelStore.get(ChannelEnum.KUANXIN_CMPP)).process(channelStore.get(ChannelEnum.KUANXIN_CMPP), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//筑望CMPP 成功
//		factory.buildSendSmsProcessor(channelStore.get(ChannelEnum.ZHUWANG_CMPP)).process(channelStore.get(ChannelEnum.ZHUWANG_CMPP), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);
//		Thread.sleep(10000000);
		//筑望CMPP
	}

	/**
	 * 测试CMPP连接
	 */
	@Test
	public void testCMPP() throws InterruptedException {
		Channel channel = channelStore.get(ChannelEnum.ZHUWANG_CMPP);
		CMPPConnectionManager connectionManager = channel.getCmppConnectionManager();
		connectionManager.openConnection();
		Thread.sleep(10000000);
	}




}