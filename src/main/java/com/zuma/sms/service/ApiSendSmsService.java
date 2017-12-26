package com.zuma.sms.service;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.dto.ApiSendSmsResultData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.PlatformSendSmsRecord;
import com.zuma.sms.enums.SmsAndPhoneRelationEnum;
import com.zuma.sms.enums.system.ChannelEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.enums.system.PhoneOperatorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.form.PlatformSendSmsForm;
import com.zuma.sms.repository.PlatformRepository;
import com.zuma.sms.repository.PlatformSendSmsRecordRepository;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.EnumUtil;
import com.zuma.sms.util.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/25 0025 09:22
 * 其他平台发送短信调用该接口
 */
@Service
@Slf4j
public class ApiSendSmsService {

	@Autowired
	private PlatformService platformService;
	@Autowired
	private PlatformRepository platformRepository;
	@Autowired
	private ChannelStore channelStore;
	@Autowired
	private ConfigStore configStore;

//	public ResultDTO<ApiSendSmsResultData> sendSms(PlatformSendSmsForm sendSmsForm) {
//		/**
//		 * 参数校验
//		 */
//		//确认平台存在
//		Platform platform = platformRepository.findOne(sendSmsForm.getPlatformId());
//		if (platform == null) {
//			log.error("[平台调用发送短信]平台id不存在.currentPlatformId:{}",sendSmsForm.getPlatformId());
//			throw new SmsSenderException(ErrorEnum.PLATFORM_NOT_EXIST);
//		}
//
//		//确认签名
//		String realSign = CodeUtil.stringToMd5(platform.getToken() + sendSmsForm.getPhone() + sendSmsForm.getTimestamp());
//		if (!sendSmsForm.getSign().equals(realSign)) {
//			log.error("[平台调用发送短信]签名不匹配.currentSign={}", sendSmsForm.getSign());
//			throw new SmsSenderException(ErrorEnum.SIGN_NOT_MATCH_ERROR);
//		}
//
//		//获取通道
//		//通道是否指定
//		boolean isAssignChannel = ArrayUtils.isNotEmpty(sendSmsForm.getChannel());
//		//当前指定的所有通道
//		Channel[] channels = new Channel[sendSmsForm.getChannel().length];
//		//当前通道支持的运营商s
//		PhoneOperatorEnum[] supportPhoneOperatorEnums = new PhoneOperatorEnum[sendSmsForm.getChannel().length];
//		//如果指定了通道id.则查询出通道
//		if(isAssignChannel){
//			for (int i =0; i < sendSmsForm.getChannel().length; i++) {
//				Channel channel = channelStore.get(sendSmsForm.getChannel()[i]);
//				//确认指定的通道是否存在
//				if (channel == null) {
//					log.error("[平台调用发送短信]通道不存在.channel={}",sendSmsForm.getChannel()[i]);
//					throw new SmsSenderException(ErrorEnum.CHANNEL_NOT_EXIST);
//				}
//				channels[i] = channel;
//				supportPhoneOperatorEnums[i] = EnumUtil.getByCode(channel.getSupportOperator(),
//						PhoneOperatorEnum.class,
//						"系统异常,通道包含不存在的运营商枚举code");
//			}
//		}
//
//
//		//确认手机号码
//		String[] phones = StringUtils.split(sendSmsForm.getPhone(), ",");
//		//如果手机号数超限
//		if (phones.length > configStore.maxSendPhoneNum) {
//			log.error("[平台调用发送短信]手机号码数目超过最大值,当前数目:{}", phones.length);
//			throw new SmsSenderException(ErrorEnum.PHONE_NUMBER_OVER);
//		}
//
//		//确认短信消息
//		String[] smsMessages = StringUtils.split(sendSmsForm.getSmsMessage(), configStore.smsMessageSeparator);
//		//判断 短信消息-手机号  一对一 或 一对多 或多对多
//		SmsAndPhoneRelationEnum smsAndPhoneRelationEnum =
//				smsMessages.length == 1 ?
//						//当短信数为1的情况下， 手机号数也为1，则为one-one；  否则就是 one-multi
//						(phones.length == 1 ? SmsAndPhoneRelationEnum.ONE_ONE : SmsAndPhoneRelationEnum.ONE_MULTI) :
//						//当短信数为多的情况下， 手机号数和其相等，则Multi-multi, 否则就是 other
//						(phones.length == smsMessages.length ? SmsAndPhoneRelationEnum.MULTI_MULTI : SmsAndPhoneRelationEnum.OTHER);
//		//如果不符合规范
//		if (smsAndPhoneRelationEnum.equals(SmsAndPhoneRelationEnum.OTHER)) {
//			log.error("[平台调用发送短信]手机号和短信消息无法对应.短信消息数：{},手机号数:{}", smsMessages.length, phones.length);
//			throw new SmsSenderException(ErrorEnum.SMS_LEN_AND_PHONE_LEN_MISMATCH);
//		}
//		//--------------------参数校验END-------------------------------
//
//
//
//		//用来统计当前号码数组包含的不同运营商
//		PhoneOperatorEnum[] containOperators = new PhoneOperatorEnum[0];
//
//		//获取每个手机号的运营商枚举
//		PhoneOperatorEnum[] phoneOperators = PhoneUtil.getPhoneOperator(phones);
//
//		//确认channel和手机运营商是否吻合,并统计包含的所有不同运营商，遍历每个手机号的运营商枚举数组
//		for (PhoneOperatorEnum temp : phoneOperators) {
//			//如果统计数组不存在该运营商，就加入,以此统计出所有不同运营商
//			if (containOperators.length < 3 && !ArrayUtils.contains(containOperators, temp)) {
//				//该工具类方法和arraylist相同，都是创建新数组，并在末尾加上元素
//				containOperators = ArrayUtils.add(containOperators, temp);
//			}
//		}
//
//		//如果指定了通道
//		if (isAssignChannel) {
//			//遍历当前号码数组包含的不同运营商
//			for (PhoneOperatorEnum item : containOperators) {
//				//如果通道支持的运营商数组不包含某运营商，则失败
//				if (!ArrayUtils.contains(supportPhoneOperatorEnums, item)) {
//					log.error("[平台调用发送短信]包含通道数组不支持的运营商手机号");
//					throw new SmsSenderException(ErrorEnum.UNSUPPORTED_OPERATOR);
//				}
//			}
//
//			//调用指定通道对应的发送短信策略
//			return sendSmsTemplateMap.get(channelEnum.getKey() + "SendSmsTemplate").sendSms(
//					channelEnum,
//					sendSmsForm.getPhone(),
//					sendSmsForm.getSmsMessage(),
//					sendSmsForm,
//					containOperators,
//					smsAndPhoneRelationEnum,
//					smsSendRecordService,
//					smsSendRecord.getId()
//			);
//		}
//
//
//		//如果未指定通道
//		//该手机号数组可用通道
//		List<ChannelEnum> availableChannel = new ArrayList<>();
//		//遍历所有通道,提取可用通道集合
//		for (ChannelEnum channelEach : ChannelEnum.values()) {
//			if (channelEach.equals(ChannelEnum.UNKNOWN))
//				continue;
//			//标识，该通道是否支持当前手机号数组
//			boolean flag = true;
//			//遍历该手机号数组包含的所有运营商
//			for (PhoneOperatorEnum operatorEach : containOperators) {
//				//如果不包含该运营商，则表示该通道不支持该手机号数组
//				if (!ArrayUtils.contains(channelEach.getPhoneOperatorSupport(), operatorEach))
//					flag = false;
//			}
//			//将校验通过的channel加入可用通道数组
//			if (flag)
//				availableChannel.add(channelEach);
//		}
//
//		//失败，更换通道重新发送机制
//		return retry(sendSmsForm, smsAndPhoneRelationEnum, containOperators, availableChannel,smsSendRecord.getId());
//	}
//
//	/**
//	 * 失败重试机制
//	 *
//	 * @param sendSmsForm
//	 * @param smsAndPhoneRelationEnum
//	 * @param containOperators
//	 * @param availableChannel
//	 * @return
//	 */
//	private ResultDTO retry(SendSmsForm sendSmsForm,
//							SmsAndPhoneRelationEnum smsAndPhoneRelationEnum,
//							PhoneOperatorEnum[] containOperators, List<ChannelEnum> availableChannel,
//							Long recordId) {
//		//每次的phones
//		String eachPhones = sendSmsForm.getPhone();
//		//每次的smsMessage
//		String eachSmsMessage = sendSmsForm.getSmsMessage();
//		ResultDTO resultDTO = null;
//		//循环所有可用通道
//		for (ChannelEnum channelEach : availableChannel) {
//			if(channelEach.equals(ChannelEnum.UNKNOWN))
//				continue;
//			//发送短信
//			resultDTO = sendSmsTemplateMap.get(channelEach.getKey() + "SendSmsTemplate")
//					.sendSms(
//							channelEach,
//							eachPhones,
//							eachSmsMessage,
//							sendSmsForm,
//							containOperators,
//							smsAndPhoneRelationEnum,
//							smsSendRecordService,
//							recordId
//					);
//
//			//如果成功
//			if (resultDTO.getCode().equals(ErrorEnum.SUCCESS.getCode()))
//				return resultDTO;
//
//			//如果有失败的数据,使用失败的phones和smsMessage重试
//			//获取失败数据集合
//			CommonResult commonResult = (CommonResult) resultDTO.getData();
//
//			//循环本次失败的所有失败数据的集合
//			//失败数据拼接
//			StringBuilder failedPhones = new StringBuilder();
//			StringBuilder failedMessages = new StringBuilder();
//			for (ResultDTO<ErrorData> each : commonResult.getErrorResults()) {
//				failedPhones.append(each.getData().getPhones()).append(Config.PHONES_SEPARATOR);
//				failedMessages.append(each.getData().getMessages()).append(Config.SMS_MESSAGE_SEPARATOR);
//			}
//			//截取末尾分隔符
//			failedPhones.deleteCharAt(failedPhones.length() - Config.PHONES_SEPARATOR.length());
//			failedMessages.delete(failedMessages.length() - Config.SMS_MESSAGE_SEPARATOR.length(), failedMessages.length());
//			//如果不是一对多，因为该情况，不能叠加失败短信消息
//			if (!smsAndPhoneRelationEnum.equals(SmsAndPhoneRelationEnum.ONE_MULTI))
//				eachSmsMessage = failedMessages.toString();
//			eachPhones = failedPhones.toString();
//		}
//		return resultDTO;
//	}
//
//	/**
//	 * 保存短信发送记录
//	 *
//	 * @return
//	 */
//	private PlatformSendSmsRecord saveSmsSendRecord(PlatformSendSmsForm sendSmsForm, Platform platform) {
//		return  new PlatformSendSmsRecord(sendSmsForm.getPlatformId(),
//				sendSmsForm.getPhone(),
//				sendSmsForm.getSmsMessage());
//	}

}
