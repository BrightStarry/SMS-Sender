package com.zuma.sms.dto.api.cmpp;

import com.zuma.sms.enums.CMPPCommandIdEnum;
import com.zuma.sms.enums.error.CMPPSubmitErrorEnum;
import com.zuma.sms.util.CMPPUtil;
import com.zuma.sms.util.EnumUtil;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.*;

/**
 * author:ZhengXing
 * datetime:2017/11/23 0023 17:22
 * cmpp 发送短信
 */
public interface CMPPSubmitAPI {


    //请求
    @Data
    @AllArgsConstructor
    @Accessors(chain = true)
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    class Request extends CMPPHeader {
        /**
         * 信息标识，由SP侧短信网关本身产生，本处填空。
         */
        private Long msgId = 0L;

        /**
         * 相同Msg_Id的信息总条数，从1开始
         */
        private byte pkTotal = 0x1;

        /**
         * 相同Msg_Id的信息序号，从1开始
         */
        private byte pkNumber = 0x1;

        /**
         * 是否要求返回状态确认报告：0：不需要  1：需要  2：产生SMC话单（该类型短信仅供网关计费使用，不发送给目的终端)
         */
        private byte registeredDelivery = 0x01;

        /**
         * 信息级别
         */
        private byte msgLevel = 0x01;

        /**
         * 业务类型，是数字、字母和符号的组合(接入号?)
         */
        private String serviceId;
//        private String serviceId = Config.ZHUWANG_JOIN_NUMBER;

        /**
         * 计费用户类型字段
         * 0：对目的终端MSISDN计费；
         * 1：对源终端MSISDN计费；
         * 2：对SP计费;
         * 3：表示本字段无效，对谁计费参见Fee_terminal_Id字段。
         */
        private byte feeUserType = 0x02;

        /**
         * 被计费用户的号码
         * （如本字节填空，则表示本字段无效，对谁计费参见Fee_UserType字段，本字段与Fee_UserType字段互斥）
         */
        private String feeTerminalId = "";

        /**
         * GSM协议类型。详细是解释请参考GSM03.40中的9.2.3.9
         */
        private byte tpPid = 0x00;

        /**
         * GSM协议类型。 详细是解释请参考GSM03.40中的9.2.3.23,仅使用1位，右对齐
         */
        private byte tpUdhi = 0x00;

        /**
         * 信息格式    0：ASCII串    3：短信写卡操作    4：二进制信息    8：UCS2编码15：含GB汉字
         */
        private byte msgFmt = 0x0f;

        /**
         * 信息内容来源(SP_Id)
         */
        private String msgSrc;

        /**
         * 资费类别01：对“计费用户号码”免费
         * 02：对“计费用户号码”按条计信息费
         * 03：对“计费用户号码”按包月收取信息费
         * 04：对“计费用户号码”的信息费封顶
         * 05：对“计费用户号码”的收费是由SP实现
         */
        private String feeType  = "01";

        /**
         * 资费代码（以分为单位）
         */
        private String feeCode = "000000";

        /**
         * 存活有效期，格式遵循SMPP3.3协议
         */
        private String validTime  = "";

        /**
         * 定时发送时间，格式遵循SMPP3.3协议
         */
        private String atTime  = "";

        /**
         * 源号码SP的服务代码或前缀为服务代码的长号码,
         * 网关将该号码完整的填到SMPP协议Submit_SM消息相应的source_addr字段，
         * 该号码最终在用户手机上显示为短消息的主叫号码
         */
        private String srcId;
//        private String srcId = Config.ZHUWANG_JOIN_NUMBER;

        /**
         * 接收信息的用户数量(小于100个用户)
         */
        private byte destUsrTl = 0x01;

        /**
         * 接收短信的MSISDN号码
         */
        private String destTerminalId;

        /**
         * 信息长度(Msg_Fmt值为0时：<160个字节；其它<=140个字节)
         */
        private byte msgLength;

        /**
         * 信息内容
         */
        private byte[] msgContent;

        /**
         * 保留位
         */
        private String reserve = "00000000";

        public Request() {
            setCommandId(CMPPCommandIdEnum.CMPP_SUBMIT.getCode());

        }


        public byte[] toByteArray() throws IOException {
            @Cleanup ByteArrayOutputStream bous = new ByteArrayOutputStream();
            @Cleanup DataOutputStream dous = new DataOutputStream(bous);
            dous.writeInt(this.getCommandId());
            dous.writeInt(this.getSequenceId());
            dous.writeLong(this.msgId);
            dous.writeByte(this.pkTotal);//Pk_total 相同Msg_Id的信息总条数
            dous.writeByte(this.pkNumber);//Pk_number 相同Msg_Id的信息序号，从1开始
            dous.writeByte(this.registeredDelivery);//Registered_Delivery 是否要求返回状态确认报告
            dous.writeByte(this.msgLevel);//Msg_level 信息级别
            CMPPUtil.writeString(dous, this.serviceId, 10);//Service_Id 业务标识，是数字、字母和符号的组合。
            dous.writeByte(this.feeUserType);//Fee_UserType 计费用户类型字段 0：对目的终端MSISDN计费；1：对源终端MSISDN计费；2：对SP计费;3：表示本字段无效，对谁计费参见Fee_terminal_Id字段。
            CMPPUtil.writeString(dous, this.feeTerminalId, 21);//Fee_terminal_Id 被计费用户的号码
            dous.writeByte(this.tpPid);//TP_pId
            dous.writeByte(this.tpUdhi);//TP_udhi
            dous.writeByte(this.msgFmt);//Msg_Fmt
            CMPPUtil.writeString(dous, this.msgSrc, 6);//Msg_src 信息内容来源(SP_Id)
            CMPPUtil.writeString(dous, this.feeType, 2);//FeeType 资费类别
            CMPPUtil.writeString(dous, this.feeCode, 6);//FeeCode
            CMPPUtil.writeString(dous, this.validTime, 17);//存活有效期
            CMPPUtil.writeString(dous, this.atTime, 17);//定时发送时间
            CMPPUtil.writeString(dous, this.srcId, 21);//Src_Id spCode
            dous.writeByte(this.destUsrTl);//DestUsr_tl
            CMPPUtil.writeString(dous, this.destTerminalId, 21);//Dest_terminal_Id
            dous.writeByte(this.msgLength);//Msg_Length
            dous.write(this.msgContent);//信息内容
            CMPPUtil.writeString(dous, this.reserve, 8);// 保留

            return bous.toByteArray();
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
         * 信息标识，生成算法如下：
         * 采用64位（8字节）的整数：
         * （1） 时间（格式为MMDDHHMMSS，即月日时分秒）：bit64~bit39，
         * 其中bit64~bit61：月份的二进制表示；
         * bit60~bit56：日的二进制表示；bit55~bit51：
         * 小时的二进制表示；bit50~bit45：分的二进制表示；
         * bit44~bit39：秒的二进制表示；
         * （2） 短信网关代码：bit38~bit17，把短信网关的代码转换为整数填写到该字段中。
         * （3） 序列号：bit16~bit1，顺序增加，步长为1，循环使用。各部分如不能填满，左补零，右对齐。
         * （SP根据请求和应答消息的Sequence_Id一致性就可得到CMPP_Submit消息的Msg_Id）
         */
        private Long msgId;

        /**
         * 结果0：正确1：消息结构错  2：命令字错  3：消息序号重复4：消息长度错5：资费代码错6：超过最大信息长7：业务代码错8：流量控制错9~ ：其他错误
         */
        private byte result;

        /**
         * 结果
         */
        private String resultString;
        public Response(byte[] data) throws IOException {
            @Cleanup ByteArrayInputStream bins = new ByteArrayInputStream(data);
            @Cleanup DataInputStream dins = new DataInputStream(bins);
            CMPPUtil.setHeader(dins,this);
            this.setMsgId(dins.readLong());
            this.setResult(dins.readByte());
            CMPPSubmitErrorEnum resultEnum = EnumUtil.getByCode((int) this.getResult(), CMPPSubmitErrorEnum.class);
            this.setResultString(resultEnum == null ? CMPPSubmitErrorEnum.OTHER_ERROR.getMessage() : resultEnum.getMessage());
        }
    }
}
