package com.zuma.sms.api.send;

import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.SendData;
import com.zuma.sms.entity.SmsSendRecord;
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

		//群正 成功
//		qunZhengSendSmsProcessor.process(channelStore.get(ChannelEnum.QUNZHENG_YD), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//畅想 成功
//		factory.buildSendSmsProcessor(channelStore.get(ChannelEnum.CHANGXIANG_YD)).process(channelStore.get(ChannelEnum.CHANGXIANG_YD), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//宽信cmpp
//		factory.buildSendSmsProcessor(channelStore.get(ChannelEnum.KUANXIN_CMPP)).process(channelStore.get(ChannelEnum.KUANXIN_CMPP), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);

		//筑望CMPP 成功
//		factory.buildSendSmsProcessor(channelStore.get(ChannelEnum.ZHUWANG_CMPP)).process(channelStore.get(ChannelEnum.ZHUWANG_CMPP), "17826824998", "【口袋铃声】您的验证码：1439", 1000L);
//		Thread.sleep(10000000);


		//
		/**
		 * 铭锋  用户名错误 ip
		 *
		 * http://121.196.208.240/smsJson.aspx
		 *
		 * MingFengAPI.Request(userId=zmkjhy, account=zmkjhy,
		 * password=zmkjhy01, mobile=17826824998,
		 * content=【口袋铃声】您的验证码：1439, sendTime=null, action=send, extno=null)
		 *
		 */
//		Channel channel = channelStore.get(ChannelEnum.MINGFENG_YD);
//		SmsSendRecord record = new SmsSendRecord(1000L, channel.getId(), channel.getName(), "17826824998", 1, "【口袋铃声】您的验证码：1439");
//		ResultDTO<SendData> resultDTO = mingFengSendSmsProcessor.process(channel, record);
//		System.out.println(resultDTO);

		/**
		 * 掌游 业务代码不存在
		 *
		 *
		 * http://ysms.game2palm.com:8899/smsAccept/sendSms.action
		 *
		 * ZhangYouAPI.Request(sid=10010317, cpid=710317,
		 * mobi=17826824998, sign=00b78619e5a4c38764ab17edfd678309,
		 * msg=44CQ5Y%2Bj6KKL6ZOD5aOw44CR5oKo55qE6aqM6K%2BB56CB77yaMTQzOQ%3D%3D, spcode=null)
		 */
//		Channel channel = channelStore.get(ChannelEnum.ZHANGYOU_YD);
//		SmsSendRecord record = new SmsSendRecord(1000L, channel.getId(), channel.getName(), "17826824998", 1, "【口袋铃声】您的验证码：1439");
//		ResultDTO<SendData> resultDTO = zhangYouSendSmsProcessor.process(channel, record);
//		System.out.println(resultDTO);


		/**
		 * 宽信 成功
		 * **/
//		Channel channel = channelStore.get(ChannelEnum.KUANXIN_YD);
//		SmsSendRecord record = new SmsSendRecord(1000L, channel.getId(), channel.getName(), "13588809885", 1, "【口袋铃声】您的验证码：1439");
//		ResultDTO<SendData> result = factory.buildSendSmsProcessor(channel).process(channel, record);
//		System.out.println(result);


		/**
		 * 宽信CMPP ...蹦了
		 *
		 * 944027 SVPOUXJLYD 1069026427
		 *
		 * ip: 118.178.35.191
		 * port: 7892
		 */

		Thread.sleep(100000000);

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