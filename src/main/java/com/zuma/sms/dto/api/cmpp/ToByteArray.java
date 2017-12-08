package com.zuma.sms.dto.api.cmpp;

import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017/11/30 0030 12:18
 * 对象可输出为字节数组的接口
 */
public interface ToByteArray {
    byte[] toByteArray() throws IOException;
}
