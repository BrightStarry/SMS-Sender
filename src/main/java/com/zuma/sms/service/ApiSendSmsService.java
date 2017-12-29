package com.zuma.sms.service;

import com.zuma.sms.api.processor.PlatformSendSmsProcessor;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.dto.ApiResult;
import com.zuma.sms.dto.PhoneMessagePair;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.SendData;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.PlatformSendSmsRecord;
import com.zuma.sms.enums.SmsAndPhoneRelationEnum;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.enums.system.ChannelEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.enums.system.PhoneOperatorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.PlatformSendSmsForm;
import com.zuma.sms.repository.PlatformRepository;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.EnumUtil;
import com.zuma.sms.util.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * author:ZhengXing
 * datetime:2017/12/25 0025 09:22
 * 其他平台发送短信调用该接口
 */
@Service
@Slf4j
public class ApiSendSmsService {

	@Autowired
	private PlatformRepository platformRepository;
	@Autowired
	private ChannelStore channelStore;
	@Autowired
	private ConfigStore configStore;
	@Autowired
	private PlatformSendSmsRecordService platformSendSmsRecordService;
	@Autowired
	private PlatformSendSmsProcessor platformSendSmsProcessor;
	@Autowired
	private ChannelService channelService;


	/**
	 * 发送短信
	 *
	 * @param sendSmsForm
	 * @return
	 */
	@Transactional
	public ResultDTO<ApiResult> sendSms(PlatformSendSmsForm sendSmsForm) {
		log.info("[平台调用发送短信]接收到请求.form:{}", sendSmsForm);
		PlatformSendSmsRecord platformSendSmsRecord = null;
		ResultDTO<ApiResult> resultDTO = null;
		try {
			/**
			 * 参数校验
			 */
			//获取平台,并验证平台和签名
			Platform platform = getPlatform(sendSmsForm);
			//根据类型获取通道 TODO 注意,这样.所有的type必须区分同一个短信平台的不同帐号
			Channel channel = getChannel(sendSmsForm);
			//获取手机号数组,手机号个数
			String[] phones = getPhones(sendSmsForm);
			//确认短信消息
			String[] smsMessages = StringUtils.split(sendSmsForm.getSmsMessage(), configStore.smsMessageSeparator);
			//获取并验证 短信-手机号 对应关系
			SmsAndPhoneRelationEnum smsAndPhoneRelationEnum = getSmsAndPhoneRelationEnum(phones, smsMessages);




			//新建调用记录
			platformSendSmsRecord = platformSendSmsRecordService.save(new PlatformSendSmsRecord(platform.getId(), sendSmsForm.getPhone(), sendSmsForm.getSmsMessage(),
					CodeUtil.objectToJsonString(sendSmsForm)));

			//如果指定了通道
			if (channel != null) {
				resultDTO = platformSendSmsProcessor.process(channel, platform,
						phones,
						smsMessages, smsAndPhoneRelationEnum,
						platformSendSmsRecord);
				saveRecord(resultDTO,platformSendSmsRecord);
			}


			//如果未指定通道------------------------------------------
			//该手机号数组可用通道
			List<Channel> availableChannel = channelStore.getAllNotCMPP();

			List<ResultDTO<SendData>> errorResults;
			//遍历
			Channel currentChannel = null;
			for (int i = 0; i < availableChannel.size(); i++) {
				//跳过tpye相同的通道
				if (currentChannel != null && currentChannel.getType().equals(availableChannel.get(i).getType()))
					continue;
				currentChannel = availableChannel.get(i);
				//发送
				resultDTO = platformSendSmsProcessor.process(currentChannel, platform,
						phones,
						smsMessages, smsAndPhoneRelationEnum,
						platformSendSmsRecord);
				//获取失败数据
				errorResults = resultDTO.getData().getErrorResults();
				//如果没有失败数据,或者是最后一次循环重试,直接返回
				if (CollectionUtils.isEmpty(errorResults) || i == availableChannel.size() - 1)
					return resultDTO;

				//处理失败数据
				//如果是多对多
				if (smsAndPhoneRelationEnum.equals(SmsAndPhoneRelationEnum.MULTI_MULTI)) {
					phones = new String[0];
					smsMessages = new String[0];
					for (ResultDTO<SendData> item : errorResults) {
						phones = ArrayUtils.addAll(phones, StringUtils.split(item.getData().getPhones(), ","));
						smsMessages = ArrayUtils.add(smsMessages, item.getData().getMessages());
					}
				} else {
					//如果是其他
					phones = new String[0];
					smsMessages = new String[1];
					for (ResultDTO<SendData> item : errorResults) {
						phones = ArrayUtils.addAll(phones, StringUtils.split(item.getData().getPhones(), ","));
					}
					smsMessages[0] = errorResults.get(0).getMessage();
				}
			}
			saveRecord(resultDTO,platformSendSmsRecord);
			return resultDTO;


		} catch (SmsSenderException e) {
			if (resultDTO == null)
				resultDTO = ResultDTO.error(e.getCode(), e.getMessage(), new ApiResult());
			else
				resultDTO.setCode(e.getCode()).setMessage(e.getMessage());

			saveRecord(resultDTO,platformSendSmsRecord);

			return resultDTO;
		} catch (Exception e) {
			log.error("[平台调用发送短信]未知异常.e:{}", e.getMessage(), e);
			if (resultDTO == null)
				resultDTO = ResultDTO.error(ErrorEnum.UNKNOWN_ERROR, new ApiResult());
			else
				resultDTO.setCode(ErrorEnum.UNKNOWN_ERROR.getCode()).setMessage(ErrorEnum.UNKNOWN_ERROR.getMessage());

			saveRecord(resultDTO,platformSendSmsRecord);
			return resultDTO;
		}
	}

	/**
	 * 根据 resultDTO 修改记录
	 */
	public void saveRecord(ResultDTO<ApiResult> resultDTO, PlatformSendSmsRecord platformSendSmsRecord) {
		//如果到了创建出记录的阶段,更新记录
		if(platformSendSmsRecord != null){
			platformSendSmsRecord.setStatus(ResultDTO.isSuccess(resultDTO) ? IntToBoolEnum.TRUE.getCode() : IntToBoolEnum.FALSE.getCode())
					.setResult(CodeUtil.objectToJsonString(resultDTO));
			platformSendSmsRecordService.save(platformSendSmsRecord);
		}
	}

	/**
	 * 用来统计当前号码数组包含的不同运营商
	 *
	 * @param groupByOperatorMap
	 * @return
	 */
	private PhoneOperatorEnum[] getContainOperators(Map<PhoneOperatorEnum, List<PhoneMessagePair>> groupByOperatorMap) {
		PhoneOperatorEnum[] containOperators = new PhoneOperatorEnum[0];
		//遍历.获取当前号码包含的所有运营商数组
		for (Map.Entry<PhoneOperatorEnum, List<PhoneMessagePair>> item : groupByOperatorMap.entrySet()) {
			//如果该运营商下数组不为空,则表示当前号码包含该运营号码,此处排除未知号码
			if (!CollectionUtils.isEmpty(item.getValue()) && !item.getKey().equals(PhoneOperatorEnum.UNKNOWN))
				containOperators = ArrayUtils.add(containOperators, item.getKey());
		}
		return containOperators;
	}

	/**
	 * 获取并验证 短信-手机号 对应关系
	 *
	 * @param phones
	 * @param smsMessages
	 * @return
	 */
	private SmsAndPhoneRelationEnum getSmsAndPhoneRelationEnum(String[] phones, String[] smsMessages) {
		SmsAndPhoneRelationEnum smsAndPhoneRelationEnum =
				smsMessages.length == 1 ?
						//当短信数为1的情况下， 手机号数也为1，则为one-one；  否则就是 one-multi
						(phones.length == 1 ? SmsAndPhoneRelationEnum.ONE_ONE : SmsAndPhoneRelationEnum.ONE_MULTI) :
						//当短信数为多的情况下， 手机号数和其相等，则Multi-multi, 否则就是 other
						(phones.length == smsMessages.length ? SmsAndPhoneRelationEnum.MULTI_MULTI : SmsAndPhoneRelationEnum.OTHER);
		//如果不符合规范
		if (smsAndPhoneRelationEnum.equals(SmsAndPhoneRelationEnum.OTHER)) {
			log.error("[平台调用发送短信]手机号和短信消息无法对应.短信消息数：{},手机号数:{}", smsMessages.length, phones.length);
			throw new SmsSenderException(ErrorEnum.SMS_LEN_AND_PHONE_LEN_MISMATCH);
		}
		return smsAndPhoneRelationEnum;
	}

	/**
	 * 获取手机号数组,并验证手机号个数
	 *
	 * @param sendSmsForm
	 * @return
	 */
	private String[] getPhones(PlatformSendSmsForm sendSmsForm) {
		String[] phones;
		phones = StringUtils.split(sendSmsForm.getPhone(), ",");
		//如果手机号数超限
		if (phones.length > configStore.maxSendPhoneNum) {
			log.error("[平台调用发送短信]手机号码数目超过最大值,当前数目:{}", phones.length);
			throw new SmsSenderException(ErrorEnum.PHONE_NUMBER_OVER);
		}
		//验证格式
		if (!PhoneUtil.verifyPhoneFormat(phones)) {
			log.error("[平台调用发送短信]有手机号码格式错误.");
			throw new SmsSenderException(ErrorEnum.PHONE_FORMAT_ERROR.getCode(),
					ErrorEnum.PHONE_FORMAT_ERROR.getMessage() + "," + "当前正则:" + configStore.getForCommon("PHONE_NUMBER_REGEXP"));
		}
		return phones;
	}


	/**
	 * 获取通道
	 *
	 * @param sendSmsForm
	 * @return
	 */
	private Channel getChannel(PlatformSendSmsForm sendSmsForm) {
		Channel channel = null;
		if (sendSmsForm.getChannelType() != null) {
			List<Channel> channels = channelStore.getByType(sendSmsForm.getChannelType());
			//确认指定的通道是否存在,并且不为cmpp
			if (CollectionUtils.isEmpty(channels) || channels.get(0).isCMPP()) {
				log.error("[平台调用发送短信]通道不存在.channel={}", sendSmsForm.getChannelType());
				throw new SmsSenderException(ErrorEnum.CHANNEL_NOT_EXIST);
			}
			//默认返回一种类型通道的第一个channel(因为即使是多运营商通用的一个帐号,也都根据运营商划分成了多个channel.即使他们的帐号都一样.)
			channel = channels.get(0);
		}
		return channel;
	}


	/**
	 * 获取平台,并验证平台和签名
	 *
	 * @param sendSmsForm
	 * @return
	 */
	private Platform getPlatform(PlatformSendSmsForm sendSmsForm) {
		//确认平台存在
		Platform platform = platformRepository.findOne(sendSmsForm.getPlatformId());
		if (platform == null) {
			log.error("[平台调用发送短信]平台不存在.currentPlatformId:{}", sendSmsForm.getPlatformId());
			throw new SmsSenderException(ErrorEnum.PLATFORM_NOT_EXIST);
		}

		//确认签名
		String realSign = CodeUtil.stringToMd5(platform.getToken() + sendSmsForm.getPhone() + sendSmsForm.getTimestamp());
		if (!sendSmsForm.getSign().equals(realSign)) {
			log.error("[平台调用发送短信]签名不匹配.currentSign={}", sendSmsForm.getSign());
			throw new SmsSenderException(ErrorEnum.SIGN_NOT_MATCH_ERROR);
		}
		return platform;
	}


}
