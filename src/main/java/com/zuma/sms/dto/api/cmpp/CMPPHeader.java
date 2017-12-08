package com.zuma.sms.dto.api.cmpp;

import com.zuma.sms.enums.CMPPCommandIdEnum;
import com.zuma.sms.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017/11/23 0023 11:30
 * 筑望 请求对象 请求头
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public  class CMPPHeader implements ToByteArray{
    //message header

    //消息总长度(含消息头及消息体)-unsignedInteger-4
    protected Integer totalLength;

    //命令或响应类型-UnsignedInteger-4
    protected Integer commandId;

    //消息流水号,顺序累加,步长为1,循环使用（一对请求和应答消息的流水号必须相同-UnsignedInteger-4
    protected Integer sequenceId;

    //自定义.commandId枚举string
    private String commandStr;

    public CMPPHeader setCommandId(Integer commandId){
        this.commandId = commandId;
        CMPPCommandIdEnum commandEnum = EnumUtil.getByCode(commandId, CMPPCommandIdEnum.class);
        commandEnum = commandEnum == null ? CMPPCommandIdEnum.UNKNOWN : commandEnum;
        this.commandStr = commandEnum.getMessage();
        return this;
    }


    @Override
    public byte[] toByteArray()  throws IOException{
        return new byte[0];
    }
}
