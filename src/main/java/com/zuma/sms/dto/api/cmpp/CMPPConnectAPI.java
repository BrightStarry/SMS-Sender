package com.zuma.sms.dto.api.cmpp;

import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.CMPPCommandIdEnum;
import com.zuma.sms.enums.error.CMPPConnectErrorEnum;
import com.zuma.sms.util.CMPPUtil;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.DateUtil;
import com.zuma.sms.util.EnumUtil;
import lombok.*;

import java.io.*;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/11/23 0023 13:25
 * cmpp 连接 接口
 */
public interface CMPPConnectAPI {
    //请求
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    class Request extends CMPPHeader {
        /**
         * 源地址，此处为SP_Id，即SP的企业代码,6位
         */
        private String sourceAddr;

        /**
         * 用于鉴别源地址。其值通过单向MD5 hash计算得出，表示如下：
         * AuthenticatorSource =  MD5（
         * Source_Addr
         * +9 字节的0
         * +shared secret
         * +timestamp）
         * Shared  secret  由中国移动与源地址实;
         * 体事先商定，timestamp格式为：MMDDHHMMSS，即月日时分秒，10位。
         * <p>
         * 16位
         */
        private byte[] authenticatorSource;

        /**
         * 双方协商的版本号(高位4bit表示主版本号,低位4bit表示次版本号)
         */
        private byte version;

        /**
         * 时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐。
         */
        private Integer timestamp;

        public byte[] toByteArray() throws IOException {
            @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            @Cleanup DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeInt(this.getCommandId());
            dataOutputStream.writeInt(this.getSequenceId());
            CMPPUtil.writeString(dataOutputStream, this.sourceAddr, 6);
            dataOutputStream.write(this.authenticatorSource);
            dataOutputStream.writeByte(this.version);
            dataOutputStream.writeInt(this.timestamp);
            return byteArrayOutputStream.toByteArray();
        }

        /**
         * 根据短信通道 构建连接请求
         * @param channel
         * @return
         */
        public static CMPPConnectAPI.Request build(Channel channel) {
            String serviceId = channel.getAKey();
            String timestamp = DateUtil.dateToString(new Date(), DateUtil.FORMAT_C);
            CMPPConnectAPI.Request request = CMPPConnectAPI.Request.builder()
                    .sourceAddr(serviceId)
                    .authenticatorSource(CodeUtil.byteToMd5(
                            (serviceId + "\0\0\0\0\0\0\0\0\0" + channel.getCKey() + timestamp).getBytes()))
                    .version((byte)32)
                    .timestamp(Integer.parseInt(timestamp))
                    .build();
            request.setCommandId(CMPPCommandIdEnum.CMPP_CONNECT.getCode());
            request.setSequenceId(CMPPUtil.getSequenceId());
            return request;
        }
    }

    //响应
    @Data
    @ToString(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    class Response extends CMPPHeader {
        /**
         * 状态0：正确1：消息结构错  2：非法源地址  3：认证错  4：版本太高    5~ ：其他错误
         */
        private int status;

        /**
         * 状态信息.自定义的
         */
        private String statusString;

        /**
         * ISMG认证码，用于鉴别ISMG。其值通过单向MD5 hash计算得出，表示如下：
         * AuthenticatorISMG =MD5（
         * Status
         * +AuthenticatorSource
         * +shared secret），
         * Shared secret 由中国移动与源地址实体事先商定，
         * AuthenticatorSource为源地址实体发送给ISMG的对应消息CMPP_Connect中的值。认证出错时，此项为空
         */
        private byte[] authenticatorISMG;

        /**
         * 服务器支持的最高版本号
         */
        private byte version;

        public Response(byte[] data) throws IOException {
            @Cleanup ByteArrayInputStream bins = new ByteArrayInputStream(data);
            @Cleanup DataInputStream dins = new DataInputStream(bins);
            CMPPUtil.setHeader(dins,this);
            this.setStatus(dins.readByte());
            CMPPConnectErrorEnum statusEnum = EnumUtil.getByCode(this.getStatus(), CMPPConnectErrorEnum.class);
            this.setStatusString(statusEnum == null ? CMPPConnectErrorEnum.OTHER_ERROR.getMessage() : statusEnum.getMessage());
            this.authenticatorISMG = CMPPUtil.getBytesForInput(dins,16);
            this.version = dins.readByte();
        }
    }

}
