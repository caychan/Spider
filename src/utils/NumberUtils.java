package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yihua.huang@dianping.com
 */
public class NumberUtils {

	public static String getTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		String now = sdf.format(new Date());
		return now;
	}
    
    public static byte[] xor(int len, byte[] a, byte[] b){
    	byte[] c = new byte[len];
    	for (int i = 0; i < len; i++) {
			c[i] = (byte) (a[i] ^ b[i]);
		}
    	return c;
    }
    
    public static char[] xor(int len, String s){
    	char[] c = new char[len];
    	char[] t = new char[len];
    	
    	String temp = "";
    	if (s.length() < len * 2) {//如果字符串不够长，补'0'
    		int i = 0; 
    		while(i ++ < (len*2-s.length())){
    			temp += "0";
			}
    		s = s + temp;
    	}
    	int left = s.length() % len;
    	if (left != 0) {
    		int i = 0; 
    		while(i ++ < (len - left)){
    			temp += "0";
			}
    		s = s + temp;
		}
    	
    	c = s.substring(0, len).toCharArray();
    	t = s.substring(len, len*2).toCharArray();
    	
    	int length = s.length();
    	int n = length / len;//字符串的长度一共有n个len长
    	int m = 2;	//现在处理了n个len长
    
    	do{	//至少执行一次异或	
    		for (int i = 0; i < len; i++) {
    			c[i] = (char) (c[i] ^ t[i]);
    		}
    		if(m < n){//最后一次只做异或，没有新的赋给t
    			t = s.substring(len * m, len * (m + 1)).toCharArray();
    		}
    		m ++;
    	}while(m <= n);// 共循环 n-m+1次

    	return c;
    }
    
    public static int compareLong(long o1, long o2) {
        if (o1 < o2) {
            return -1;
        } else if (o1 == o2) {
            return 0;
        } else {
            return 1;
        }
    }
}
