package com.zuma.sms.api.socket;

import com.zuma.sms.dto.api.cmpp.*;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.CMPPCommandIdEnum;
import com.zuma.sms.enums.error.CMPPSubmitErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.CMPPUtil;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.DateUtil;
import com.zuma.sms.util.TokenUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 10:39
 * CMPP连接管理器
 * 管理若干{@link CMPPConnection}
 */
@Slf4j
public class CMPPConnectionManager {

	//连接器集合
	private List<CMPPConnection> connections = new CopyOnWriteArrayList<>();
	//锁-确保数量为1时,连接不会被关闭完
	private ReentrantLock lock = new ReentrantLock(true);
	//短信通道
	private Channel channel;
	//当前累加值-用于对当前连接数取模
	private AtomicInteger counter = new AtomicInteger(1);



	/**
	 * 轮询获取一个连接通道
	 */
	public io.netty.channel.Channel getChannelFair() {
		for (int i = 0; i < getSize(); i++) {
			try {
				//尝试直接获取,如果失败,会抛出异常
				return connections.get(getCount() % getSize()).getChannelHandlerContext().channel();
			} catch (SmsSenderException e){
				log.error("[CMPP连接管理器]轮询获取通道失败.自定义异常.message:{}",e.getMessage());
			}catch (Exception e) {
				log.error("[CMPP连接管理器]轮询获取通道失败.error:{}",e.getMessage(),e);
			}
		}
		//此处还未获取到.表示失败
		throw new SmsSenderException("该通道暂不可用");

	}

	/**
	 * 获取并累加 ,如果过大,还需重置
	 */
	public int getCount() {
		int i = counter.getAndIncrement();

		//如果超出一定数量,重置.此处使用惰性set.不保证其他线程立即可见.
		if(i > 5000)
			counter.lazySet(1);
		return i;
	}

	/**
	 * 注入通道构造
	 */
	public CMPPConnectionManager(Channel channel) {
		this.channel = channel;
	}

	/**
	 *启动新的连接
	 */
	public void openConnection() {
		try {
			lock.lock();
			connections.add(new CMPPConnection(channel, TokenUtil.getSequenceId(),this));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 关闭一个连接
	 */
	public void closeConnetion() {
		try {
			lock.lock();
			if(getSize() > 1){
				CMPPConnection connection = connections.remove(0);
				//关闭前发送 中断连接请求
				sendTerminate(null,connection.getChannelHandlerContext().channel());
				connection.close();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取当前连接总数
	 */
	public int getSize() {
		try {
			lock.lock();
			return connections.size();
		} finally {
			lock.unlock();
		}
	}

	//发送相关-----------------------------------
	/**
	 * 发送短信,逐条发送
	 * 返回流水号sequenceId
	 */
	public Integer  sendSms(CMPPSubmitAPI.Request request) throws IOException {
		log.info("[CMPP连接管理器]通道:{},发送 发送短信 请求:{}",channel.getName(),request);
		send(request);
		return request.getSequenceId();
	}

	/**
	 * 发送连接请求,指定Netty channel
	 */
	public void sendConnectRequest(io.netty.channel.Channel nettyChannel,CMPPConnectAPI.Request request) throws IOException {
		log.info("[CMPP连接管理器]通道:{},发送 请求连接 请求:{}",channel.getName(),request);
		send1(request,0,nettyChannel);
	}



	/**
	 * 发送短信推送响应
	 */
	public void sendDeliverResponse(CMPPDeliverAPI.Request request, CMPPSubmitErrorEnum errorEnum) throws IOException {
		CMPPDeliverAPI.Response response = CMPPDeliverAPI.Response.builder()
				.msgId(request.getMsgId())
				.result(errorEnum.getCode().byteValue())
				.build();
		response.setCommandId(CMPPCommandIdEnum.CMPP_DELIVER_RESP.getCode());
		response.setSequenceId(request.getSequenceId());

		log.info("[CMPP连接管理器]通道:{},发送 短信推送 响应:{}",channel.getName(),response);
		send(response);
	}

	/**
	 * 发送心跳检测(空) 或 发送心跳检测响应(非空)
	 * 因为响应需要匹配其请求的sequenceId
	 * @param nettyChannel 指定对应的连接对象的通道
	 */
	public void sendActiveTest(io.netty.channel.Channel nettyChannel,Integer sequenceId) throws IOException {
		CMPPActiveTestAPI.Request request =
				new CMPPActiveTestAPI.Request(
						sequenceId == null ?
								CMPPCommandIdEnum.CMPP_ACTIVE_TEST :
								CMPPCommandIdEnum.CMPP_ACTIVE_TEST_RESP,
						sequenceId);
		log.info("[CMPP连接管理器]通道:{},发送 心跳检测{}:{}",channel.getName(),sequenceId == null ? "" : "响应",request);
		send1(request,0,nettyChannel);
	}



	/**
	 * 发送断开连接请求 或发送响应,指定连接通道
	 */
	public void sendTerminate(Integer sequenceId, io.netty.channel.Channel nettyChannel){
		CMPPHeader request = new CMPPHeader()
				.setCommandId(sequenceId == null ?
						CMPPCommandIdEnum.CMPP_TERMINATE.getCode() :
						CMPPCommandIdEnum.CMPP_TERMINATE_RESP.getCode())
				.setSequenceId(CMPPUtil.getSequenceId());
		log.info("[CMPP连接管理器]通道:{},发送 中断连接{}:{}",channel.getName(),sequenceId == null ? "" : "响应",request);
		send1(request,0,nettyChannel);
	}

	/**
	 * 发送断开连接请求 或发送响应
	 */
	public void sendTerminate(Integer sequenceId){
		sendTerminate(sequenceId,null);
	}

	/**
	 * 发送数据,第一次
	 */
	public void send(ToByteArray data) {
		send1(data,0,null);
	}

	/**
	 * 发送数据,可指定使用哪个连接通道发送
	 */
	public void send1(ToByteArray data, Integer retryNum, io.netty.channel.Channel nettyChannel) {
		try {
			//信号量递增
			channel.getConcurrentManager().increment();
			//判断连接是否已经开启
			//是否使用指定通道
			if(nettyChannel == null)
				//轮询获取netty通道,将字节数组转为ByteBuf,并发送
				getChannelFair().writeAndFlush(Unpooled.copiedBuffer(data.toByteArray()));
			else
				//使用指定通道
				nettyChannel.writeAndFlush(Unpooled.copiedBuffer(data.toByteArray()));
		} catch (Exception e) {
			//重试
			log.error("[CMPP连接管理器]通道:{},发送失败.重试次数={},error={}",channel.getName(),retryNum,e.getMessage(),e);
			//超过3次后，不再发送，抛出异常记录
			if(retryNum >= 3){
				throw new SmsSenderException(ErrorEnum.SOCKET_REQUEST_ERROR);
			}
			//未超过三次重试
			send1(data,++retryNum,nettyChannel);
		}
	}
}
