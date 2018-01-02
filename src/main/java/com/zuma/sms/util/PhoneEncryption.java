package com.zuma.sms.util;

public class PhoneEncryption {
	/**
	 * 码表
	 * 第一位 对应首字母
	 * 第二、三位 效验码
	 * 第四、五位 效验码位置
	 * 对位交换规则  
	 */
	private static String [][] sklist={{"abcdefg","13","17","3","7","1:4","2:7","3:9","5:6","8:10","11:11","1"},
			{"hijklmmo","3","23","4","6","1:5","2:8","3:9","7:6","4:11","10:10","2"},
			{"pqrstu","11","23","2","5","1:10","2:11","3:9","4:8","5:6","7:7","3"},
			{"vwxyz","17","19","3","5","1:11","2:10","3:8","4:9","5:6","7:7","4"}};
	
	public static void main(String[] args) {
		String phone = "18552460091";
//		for(int i=0;i<100;i++){
//			String phone="1";
//			for(int j=0;j<10;j++){
//				phone+=String.valueOf((int)(1+Math.random()*(9)));
//			}
//			System.out.print(phone+" ");
//			long t1 = System.nanoTime();
//			String s = toCiphertext(phone);
//			long t2 = System.nanoTime();
//			System.out.println((t2-t1)+" "+phone+" "+s+" "+getCiphertext(s));
//		}
//		System.out.println(phone+" "+s+" "+getCiphertext(s));
	
		System.out.println(toCiphertext(phone));
	}
	
	/**
	 * 加密
	 * @param phone
	 * @return
	 */
	public static String toCiphertext(String phone){
		
		int i = Integer.valueOf(String.valueOf((System.nanoTime()%sklist.length)));
		String[] sk = sklist[i];
		//效验码
		String x1 = chang10To26(Long.valueOf(phone)%Long.valueOf(sk[1]));
		String x2 = chang10To26(Long.valueOf(phone)%Long.valueOf(sk[2]));
		//换位
		char[] p = phone.toCharArray();
		char[] np = new char[p.length] ;
		for(String c:sk){
			if(c.indexOf(":")!=-1){
				String[] cc = c.split(":");
				np[Integer.valueOf(cc[1])-1] = p[Integer.valueOf(cc[0])-1] ;
				np[Integer.valueOf(cc[0])-1] = p[Integer.valueOf(cc[1])-1] ;
			}
		}
		String nc = String.valueOf(np);
		nc=String.valueOf((int)(1+Math.random()*(9)))+nc;
//		System.out.println("10 进制："+nc);
		//转26进制
		nc = chang10To26(Long.valueOf(nc));
//		System.out.println("26进制："+nc);
		//插入效验码
		nc = nc.substring(0,Integer.valueOf(sk[3]))+x1+nc.substring(Integer.valueOf(sk[3]), Integer.valueOf(sk[4]))+x2+nc.substring(Integer.valueOf(sk[4]),nc.length());
//		System.out.println("插入效验："+nc);
		//加上第一位
		char[] fs = sk[0].toCharArray();
		nc=String.valueOf(fs[Integer.valueOf(String.valueOf((System.nanoTime()%fs.length)))])+nc;
		return nc.toUpperCase();
	}
	
	/**
	 * 解密
	 * @param ciptext
	 * @return
	 */
	public static String getCiphertext(String ciptext){
		char[] s = ciptext.toCharArray();
		String[] sk = null;
		for(int i=0;i<sklist.length;i++){
			if(sklist[i][0].toUpperCase().indexOf(s[0])!=-1){
				sk = sklist[i];
			}
		}
		//提取效验码
		String x1 = String.valueOf(s[Integer.valueOf(sk[3])+1]);
		String x2 = String.valueOf(s[Integer.valueOf(sk[4])+2]);
		String nstr = "";
//		System.out.println("原始："+ciptext+" "+sk[3]+" "+sk[4]);
		//去除效验码
		for(int i=1;i<s.length;i++){
			if(i!=(Integer.valueOf(sk[3])+1)&&i!=Integer.valueOf(sk[4])+2){
				nstr += String.valueOf(s[i]);
			}
//			else{
//				System.out.println(i);
//			}
		}
		
		//转10进制
//		System.out.println("去效验："+nstr);
		ciptext = chang26To10(nstr);
//		System.out.println("转进制："+ciptext);
		ciptext = ciptext.substring(1);
//		System.out.println(ciptext);
		//反换位
		char[] p = ciptext.toCharArray();
		char[] np = new char[p.length] ;
		for(String c:sk){
			if(c.indexOf(":")!=-1){
				String[] cc = c.split(":");
				np[Integer.valueOf(cc[1])-1] = p[Integer.valueOf(cc[0])-1] ;
				np[Integer.valueOf(cc[0])-1] = p[Integer.valueOf(cc[1])-1] ;
			}
		}
		
		ciptext = String.valueOf(np);
//		System.out.println(ciptext);
		
		//计算效验码
		String xn1 = chang10To26(Long.valueOf(ciptext)%Long.valueOf(sk[1]));
		String xn2 = chang10To26(Long.valueOf(ciptext)%Long.valueOf(sk[2]));
		//效验
		if(!xn1.equalsIgnoreCase(x1)||!xn2.equalsIgnoreCase(x2)){
			return "-1";
		}
		
		return ciptext;
	}
	
    public static String chang26To10(String str){
    	char[] list = str.toCharArray();
    	Long c=0L;
    	for(int i=0;i<list.length;i++){
    		String r = String.valueOf(list[i]);
    		
    		int a = (int)list[i]-64;
    		if(i!=list.length-1){
    			c=(long)(c+a*Math.pow(26,(list.length-i-1))) ;
    		}
    		else{
    			c=c+a;
    		}
    		
    	}
    	return String.valueOf(c);
    }
    
	public static String chang10To26(long colIndex)

	{

		String strRtn = "";

		if (colIndex < 0)

			return "";

		long numMod = 0;

		long numTemp = colIndex;

		char ch = 'A';

		do

		{

			numMod = numTemp % 26;

			numTemp = numTemp / 26;

			if (numMod == 0)

			{

				ch = 'Z';

				numTemp--;

			}

			else

				ch = (char) ('A' + numMod - 1);

			strRtn = ch + strRtn;

		}

		while (numTemp > 0);

		return strRtn;

	}
}
