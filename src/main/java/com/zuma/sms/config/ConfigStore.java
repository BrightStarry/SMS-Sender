package com.zuma.sms.config;

import com.zuma.sms.api.socket.IPPortPair;
import com.zuma.sms.entity.Dict;
import com.zuma.sms.enums.system.ConfigModuleEnum;
import com.zuma.sms.repository.DictRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 10:36
 * 配置常量
 */
@Component
@Slf4j
@ConfigurationProperties(prefix = "smsSender.config")
@Data
public class ConfigStore {



	//短信发送任务
	//运行时拉取任务,每次默认拉取记录数
	public Integer recordPullOnRunNum = 1000;
	//预创建每次默认拉取手机号字符数
	public Integer phoneStrNum = 60000;
	//发送任务中的主队列,最多暂存多少条记录
	public Integer mainQueueLen = 1000;


	//平台调用发送
	//最大发送手机号数
	public Integer maxSendPhoneNum = 100;
	//短信消息分隔符
	public String smsMessageSeparator = "!&";

	//发送任务 号码数 预警 阈值 - 10W
	public Integer sendTaskWarnOfPhoneNum = 100000;
	//发送任务 线程数 预警 阈值 - 50
	public Integer sendTaskWarnOfThreadNum = 50;

	//是否开启CMPP连接
	public Boolean isOpenCMPPConnection = true;


	//异常页面
	public String errorUrl = "common/error";

	//模糊查询,最大显示数目
	public Integer likeSearchMaxNum = 20;

	//所有文件路径前缀
	public String allPathPre = (System.getProperty("os.name").contains("Windows")
			? "E:" : "") + File.separator + "smsSender" + File.separator;

	//号码源前缀(前缀+id)
	public String numberSourcePre = allPathPre + "numberSource" + File.separator;

	//号码组保存前缀(前缀+id)
	public String numberGroupPre = allPathPre + "numberGroup" + File.separator;

	//发送任务异常信息前缀(前缀+id)
	public String sendTaskErrorInfoPre = allPathPre + "sendTaskErrorInfo" + File.separator;

	//批处理异常数据文件路径
	public String batchErrorFilePath = allPathPre + "batchErrorData" + ".txt";


	//Common模块配置map,从数据库中读取
	public Map<String, Map<String, String>> config = new ConcurrentHashMap<>();

	//CMPP
	//cmpp心跳检测间隔,超过x秒未收到对方请求
	public Long cmppActiveTestSecond = 180L;

	//cmpp类型的通道的ip和端口,以通道id为key.
	public Map<String,IPPortPair> cmppIpPortMap = new HashMap<>();



	//url
	//掌游发送短信url
	public  String zhangyouSendSmsUrl = "http://ysms.game2palm.com:8899/smsAccept/sendSms.action";
	//宽信url前缀
//	public String kuanxinUrlPre = "http://114.55.90.98:8808/api";
	public String kuanxinUrlPre = "http://118.178.35.191:8808/api";
	//宽信发送短信url
	public String kuanxinSendSmsUrl = kuanxinUrlPre + "/sms/send";
	//群正发送短信Url
	public String qunzhengSendSmsUrl = "http://sms.uninets.com.cn/Modules/Interface/http/IservicesBSJY.aspx";
	//畅想发送短信url
	public String changxiangSendSmsUrl = "http://api.cxton.com:8080/eums/utf8/send_strong.do";
	//创蓝url前缀
	public String chuanglanUrlPre = "http://smssh1.253.com";
	//创蓝发送短信url
	public String chuanglanSendSmsUrl = chuanglanUrlPre + "/msg/send/json";
	//创蓝发送变量短信url
	public String chuanglanVariateSendSmsUrl = chuanglanUrlPre + "/msg/variable/json";
	//铭锋url前缀
	public String mingfengUrlPre = "http://121.196.208.240";
	//铭锋发送短信url
	public String mingfengSendSmsUrl = mingfengUrlPre + "/smsJson.aspx";
	//助通url前缀
	public String zhutongUrlPre = "http://api.zthysms.com";
	//助通发送短信url
	public String zhutongSendSmsUrl = zhutongUrlPre + "/sendSms.do";




	/**
	 * 根据模块和key从配置map中取值
	 */
	public String get(ConfigModuleEnum configModuleEnum, String key) {
		return config.get(configModuleEnum.getCode()).get(key);
	}

	/**
	 * 根据模块和key从配置map中取值 int
	 */
	public int getInt(ConfigModuleEnum configModuleEnum, String key) {
		return Integer.parseInt(get(configModuleEnum, key));
	}

	/**
	 * 从common模块中取值
	 */
	public String getForCommon(String key) {
		return get(ConfigModuleEnum.COMMON, key);
	}

	/**
	 * 从common模块中取值 int
	 */
	public int getForCommonInt(String key) {
		return getInt(ConfigModuleEnum.COMMON, key);
	}


	/**
	 * 从run模块中取值
	 */
	public String getForRun(String key) {
		return get(ConfigModuleEnum.RUN, key);
	}

	/**
	 * 从run模块中取值 int
	 */
	public int getForRunInt(String key) {
		return getInt(ConfigModuleEnum.RUN, key);
	}


	public void setConfig(Map<String, Map<String, String>> config) {
		this.config = config;
	}

	/**
	 * 加载配置属性到Config
	 */
	public void loadConfig(DictRepository dictRepository) {
		Map<String, Map<String, String>> map = new ConcurrentHashMap<>();
		for (ConfigModuleEnum item : ConfigModuleEnum.class.getEnumConstants()) {
			List<Dict> list = dictRepository.findByModuleEquals(item.getCode());
			Map<String, String> childMap = new ConcurrentHashMap<>();
			for (Dict dict : list) {
				childMap.put(dict.getName(), dict.getValue());
			}
			map.put(item.getCode(), childMap);
		}
		setConfig(map);
		log.info("[configStore]加载配置属性完成.");
	}

	/**
	 * 修改某个属性
	 */
	public void update(Dict dict) {
		Map<String, String> childMap = config.get(dict.getModule());
		childMap.put(dict.getName(), dict.getValue());
		log.info("[配置类]属性修改成功.dict:{},currentValue:{}",dict,childMap.get(dict.getName()));
	}
}



