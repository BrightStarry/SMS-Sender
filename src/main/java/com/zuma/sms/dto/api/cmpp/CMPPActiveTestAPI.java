package com.zuma.sms.dto.api.cmpp;

import com.zuma.sms.enums.CMPPCommandIdEnum;
import com.zuma.sms.util.CMPPUtil;
import lombok.*;

import java.io.*;

/**
 * author:ZhengXing
 * datetime:2017/11/24 0024 13:57
 * 链路检测接口api
 */
public interface CMPPActiveTestAPI {

    //请求
    @Data
    @ToString(callSuper = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    class Request extends CMPPHeader {
        private byte reserved = 0;


        //指定sequence,用来响应
        public Request(CMPPCommandIdEnum commandIdEnum, Integer sequenceId){
            this.commandId = commandIdEnum.getCode();
            this.sequenceId = sequenceId== null ? CMPPUtil.getSequenceId() : sequenceId;
        }


        public byte[] toByteArray() throws IOException {
            @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            @Cleanup DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeInt(this.getCommandId());
            dataOutputStream.writeInt(this.getSequenceId());
            dataOutputStream.writeByte(this.reserved);
            return byteArrayOutputStream.toByteArray();
        }

        public Request(byte[] data) throws IOException {
            @Cleanup ByteArrayInputStream bins = new ByteArrayInputStream(data);
            @Cleanup DataInputStream dins = new DataInputStream(bins);
            CMPPUtil.setHeader(dins,this);
        }
    }


    //响应
    @Data
    @ToString(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    class Response extends CMPPHeader {
        //一个1字节的空回复即可,该字节为空,无需解析
        private byte reserved;

        public Response(byte[] data) throws IOException {
            @Cleanup ByteArrayInputStream bins = new ByteArrayInputStream(data);
            @Cleanup DataInputStream dins = new DataInputStream(bins);
            CMPPUtil.setHeader(dins,this);
        }

    }
}