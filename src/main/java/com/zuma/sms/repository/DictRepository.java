package com.zuma.sms.repository;

import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Dict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 字典
 */
public interface DictRepository extends JpaRepository<Dict,Long> {

	//查询指定模块的所有配置
	List<Dict> findByModuleEquals(String module);
}
