package com.zuma.sms.config.store;

import com.zuma.sms.api.socket.IPPortPair;
import com.zuma.sms.entity.Dict;
import com.zuma.sms.enums.system.ConfigModuleEnum;
import com.zuma.sms.repository.DictRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 10:36
 * 配置常量
 */
@Component
@ConfigurationProperties(prefix = "smsSender.config")
@Slf4j
public class ConfigStore {


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


	//Common模块配置map,从数据库中读取
	public Map<String, Map<String, String>> config = new ConcurrentHashMap<>();

	//CMPP
	//cmpp心跳检测间隔,超过x秒未收到对方请求
	public Long cmppActiveTestSecond = 180L;

	//cmpp类型的通道的ip和端口,以通道id为key.
	public Map<Long,IPPortPair> cmppIpPortMap;


	//url
	//掌游发送短信url
	public  String zhangyouSendSmsUrl = "http://ysms.game2palm.com:8899/smsAccept";



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
