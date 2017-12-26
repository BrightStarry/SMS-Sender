package com.zuma.sms;

import com.zuma.sms.dto.IdFieldValuePair;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.service.BatchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsSenderApplicationTests {

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


		batchService.batchInsertSmsSendRecord(l);
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

}
