package com.zuma.sms.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

/**
 * author:ZhengXing
 * datetime:2017/12/29 0029 17:27
 */
public class Md5Util {
	public  static String MD5(String s) {
		if (s == null && "".equals(s))
		{
			return null;
		}

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getHashCode(Object object) throws IOException {
		if(object == null) return "";

		String ss = null;
		ObjectOutputStream s = null;
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try {
			s = new ObjectOutputStream(bo);
			s.writeObject(object);
			s.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(s != null) {
				s.close();
				s = null;
			}
		}
		ss = MD5(bo.toString());
		return ss;
	}

	public static void main(String[] args) {
		String str = "serviceLocator = getDefaultClassLoader().loadClass(SERVICE_LOCATOR_CLASS)serviceLocator = getDefaultClassLoader().loadClass(SERVICE_LOCATOR_CLASS)serviceLocator = getDefaultClassLoader().loadClass(SERVICE_LOCATOR_CLASS)serviceLocator = getDefaultClassLoader().loadClass(SERVICE_LOCATOR_CLASS)serviceLocator = getDefaultClassLoader().loadClass(SERVICE_LOCATOR_CLASS)serviceLocator = getDefaultClassLoader().loadClass(SERVICE_LOCATOR_CLASS)serviceLocator = getDefaultClassLoader().loadClass(SERVICE_LOCATOR_CLASS)";
		System.out.println(MD5(str));
	}
}
