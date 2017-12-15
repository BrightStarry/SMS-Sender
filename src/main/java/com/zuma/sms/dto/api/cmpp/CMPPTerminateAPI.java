package com.zuma.sms.dto.api.cmpp;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 15:03
 * cmpp 中断请求
 */
public interface CMPPTerminateAPI {

	@Data
	@Accessors(chain = true)
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = false)
	@NoArgsConstructor
	class Request extends CMPPHeader{
		public Request(byte[] data) throws IOException {
			super(data);
		}
	}

	@Data
	@Accessors(chain = true)
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = false)
	@NoArgsConstructor
	class Response extends CMPPHeader{
		public Response(byte[] data) throws IOException {
			super(data);
		}
	}
}
