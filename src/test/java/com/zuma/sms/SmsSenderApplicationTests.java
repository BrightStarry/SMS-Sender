package com.zuma.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuma.sms.dto.IdFieldValuePair;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.form.PlatformSendSmsForm;
import com.zuma.sms.form.SendSmsForm;
import com.zuma.sms.repository.BatchRepository;
import com.zuma.sms.service.BatchService;
import com.zuma.sms.service.SendTaskRecordService;
import com.zuma.sms.util.CodeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SmsSenderApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SendTaskRecordService sendTaskRecordService;

	@Autowired
	private BatchRepository batchRepository;

	@Test
	public void contextLoads() {
	}
	@PersistenceContext
	protected EntityManager em;

	@Autowired
	private BatchService batchService;

	@Test
	public void testBatch() {
		List<SmsSendRecord> l = new ArrayList<>();
		l.add(new SmsSendRecord(1000L,1000L,"aaa","bbb",10,"xxxx"));
		l.add(new SmsSendRecord(1001L,1000L,"aaa","bbb",10,"xxxx"));
		l.add(new SmsSendRecord(1002L,1000L,"aaa","bbb",10,"xxxx"));


		batchService.batchSave(l);
	}

	@Test
	public void testBatch2() {
		List<IdFieldValuePair> l = new ArrayList<>();
		l.add(new IdFieldValuePair(1015L, "channel_name", "xxxx", true));
		l.add(new IdFieldValuePair(1016L, "channel_name", "xxxx", true));
		l.add(new IdFieldValuePair(1017L, "channel_name", "xxxx", true));
		l.add(new IdFieldValuePair(1026L, "channel_name", "xxxx", true));
		l.add(new IdFieldValuePair(1021L, "channel_name", "xxxx", true));
		l.add(new IdFieldValuePair(1022L, "channel_name", "xxxx", true));
		batchService.batchUpdateSmsSendRecordFieldById(l,"channel_name",true);
	}


	@Test
	public void test3() {
		List<SmsSendRecord> l = new LinkedList<>();
		l.add(new SmsSendRecord().setMessage("111").setPhones("aaa").setPhoneCount(11));
		l.add(new SmsSendRecord().setMessage("22").setPhones("aaa").setPhoneCount(11));
		l.add(new SmsSendRecord().setMessage("22").setPhones("aaa").setPhoneCount(11));
		batchService.batchSave(l);

		l.get(0).setErrorInfo("xxx");
		batchService.batchSave(l);
	}


	@Autowired
	private ObjectMapper objectMapper;
	/**
	 * 测试api调用发送短信接口
	 */
	@Test
	public void testApiSendSms() throws Exception {
		Long platformId = 1000L;
		String phone = "17826824998,13325869158";
		Integer channel = 0;
		String smsMessage = "xxxxxx!&aaaaaa";
		Long timestamp = System.currentTimeMillis();
		String sign = CodeUtil.stringToMd5("$2a$10$E7A/X1w9DvxpCCL99P.M/eg6I1ovItS3UxNHRwUlV/ueDXW2GBf4e" + phone + timestamp);
		PlatformSendSmsForm sendSmsForm = new PlatformSendSmsForm(platformId, channel, phone, smsMessage, sign, timestamp);
		String result = mockMvc.perform(
				MockMvcRequestBuilders.post("/api/sendsms")
//						.param("platformId", "1000")
//						.param("phone", phone)
//						.param("smsMessage", )
//						.param("timestamp", timestamp)
//						.param("sign", sign)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(objectMapper.writeValueAsString(sendSmsForm))
						.header("X-Real-IP","127.0.0.1")

		)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();
		System.out.println(result);
	}



}
