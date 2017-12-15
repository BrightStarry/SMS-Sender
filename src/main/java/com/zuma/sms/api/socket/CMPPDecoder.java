package com.zuma.sms.api.socket;

import com.zuma.sms.dto.api.cmpp.*;
import com.zuma.sms.enums.CMPPCommandIdEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.EnumUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/11/23 0023 14:19
 * 消息解码器
 * 所有cmpp连接共享
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class CMPPDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //字节数组
        byte[] buf = new byte[byteBuf.readableBytes()];
        //读取数据到字节数组
        byteBuf.readBytes(buf);


        //开始解析数据,先提取出长度字段标识长度的数据,也就是该条消息
        //4位 消息长度
        int totalLength = CodeUtil.bytesToInt(ArrayUtils.subarray(buf, 0, 4));
        //获取到该长度的字节数组
        byte[] bytes = ArrayUtils.subarray(buf, 0, totalLength);

        //获取到响应类型,也就是哪个接口的响应,4位
        int commandId = CodeUtil.bytesToInt(ArrayUtils.subarray(bytes, 4, 8));
        //获取到对应接口枚举
        CMPPCommandIdEnum commandIdEnum = EnumUtil.getByCode(commandId, CMPPCommandIdEnum.class);


        //如果响应类型为空,表示无此接口
        if (commandIdEnum == null) {
            log.info("[CMPP消息解码器]服务器响应,未知接口.commandId:{},bytes:{},",commandId,bytes);
            throw new SmsSenderException(ErrorEnum.RESPONSE_MESSAGE_ERROR);
        }

        log.debug("[CMPP消息解码器]接收到字节码:{}",bytes);

        //连接请求响应
        switch (commandIdEnum) {
            //如果是发送短信
            case CMPP_SUBMIT_RESP:
                submitResponse(bytes,list);
                break;
            //如果是链路检测-他们发过来的链路检测
            case CMPP_ACTIVE_TEST:
                activeTest(bytes,list);
                break;
            //是链路检测响应-我们发过去,他们发回来的信息
            case CMPP_ACTIVE_TEST_RESP:
                activeTestResponse(bytes,list);
                break;
            //如果是短信推送
            case CMPP_DELIVER:
                deliverRequest(bytes,list);
                break;
            //如果是终止连接
            case CMPP_TERMINATE:
                terminateRequest(bytes, list);
                break;
            //终止连接响应
            case CMPP_TERMINATE_RESP:
                terminateRequestResponse(bytes, list);
                break;
            //如果是连接请求
            case CMPP_CONNECT_RESP:
                connectResponse(bytes,list);
                break;
        }


    }

    /**
     * 连接请求响应
     */
    private void connectResponse(byte[] bytes, List<Object> list) throws Exception {
        //byte[]转对象
        CMPPConnectAPI.Response response = new CMPPConnectAPI.Response(bytes);
        //解码成功,返回该对象
        result(list, response);
    }

    /**
     * 链路检测请求
     */
    private void activeTest(byte[] bytes, List<Object> list) throws IOException {
        CMPPActiveTestAPI.Request response = new CMPPActiveTestAPI.Request(bytes);
        result(list,response);
    }

    /**
     * 链路检测响应
     */
    private void activeTestResponse(byte[] bytes, List<Object> list) throws IOException {
        CMPPActiveTestAPI.Response response = new CMPPActiveTestAPI.Response(bytes);
        result(list,response);
    }

    /**
     * 发送短信响应
     */
    private void submitResponse(byte[] bytes,List<Object> list) throws IOException{
        CMPPSubmitAPI.Response response = new CMPPSubmitAPI.Response(bytes);
        result(list,response);
    }

    /**
     * 短信推送请求
     */
    private void deliverRequest(byte[] bytes,List<Object> list) throws IOException {
        CMPPDeliverAPI.Request request = new CMPPDeliverAPI.Request(bytes);
        result(list,request);
    }

    /**
     * 终止连接
     */
    private void terminateRequest(byte[] bytes,List<Object> list) throws IOException {
        CMPPTerminateAPI.Request request = new CMPPTerminateAPI.Request(bytes);
        result(list,request);
    }

    /**
     * 终止连接响应
     */
    private void terminateRequestResponse(byte[] bytes,List<Object> list) throws IOException {
        CMPPTerminateAPI.Response request = new CMPPTerminateAPI.Response(bytes);
        result(list,request);
    }


    /**
     * 将对象加入list返回给handler方法
     */
    private <T> void result(List<Object> list, T obj) {
        list.add(obj);
    }


}
