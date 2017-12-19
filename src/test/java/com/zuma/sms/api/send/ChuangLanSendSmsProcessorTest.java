package com.zuma.sms.api.send;

import com.zuma.sms.api.processor.send.ChuangLanSendSmsProcessor;
import com.zuma.sms.api.processor.send.ChuangLanVariateSendSmsProcessor;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.dto.api.MingFengAPI;
import com.zuma.sms.util.CodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 13:38
 * 创蓝接口测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ChuangLanSendSmsProcessorTest {

	@Autowired
	private ChuangLanSendSmsProcessor chuangLanSendSmsProcessor;

	@Autowired
	private ChuangLanVariateSendSmsProcessor chuangLanVariateSendSmsProcessor;
	@Autowired
	private ChannelStore channelStore;

	@Test
	public void testSendSms() {

		chuangLanVariateSendSmsProcessor.process(channelStore.get(1014L),"17826824998,https://www.baidu.com;13588809885,https://www.baidu.com;","温馨提醒：尊敬的移动用户，您有1份专属壕礼，点击领取{$var} 回N不收此短信【口袋铃声】",1000L);
	}



}