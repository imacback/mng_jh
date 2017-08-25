package com.jh.mng.util;

import java.util.regex.Matcher;  
import java.util.regex.Pattern;  
  
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jh.mng.common.StrIntBag;
/** 
 * 通过手机号码,获得该号码的归属地 
 *  
 * @author Administrator 
 * 
 */  
public class MobileFromUtil {  
    public static final String REGEX_IS_MOBILE=  
        "^\\d{11}$";  
      
    /** 
     * 获得手机号码归属地 
     *  
     * @param mobileNumber 
     * @return 
     * @throws Exception 
     */  
    public static String getMobileFrom(String mobileNumber) {  
        if(!veriyMobile(mobileNumber)){  
        	
        	return null;
//            throw new Exception("不是完整的11位手机号或者正确的手机号前七位");  
        }  
          
        String htmlSource=null;  
        String result=null;  
          
          
        try {  
            
        	StrIntBag  strIntBag =  HttpClientUtil.executeGet("http://www.ip138.com:8080/search.asp?action=mobile&mobile=" + mobileNumber, "gb2312");
              
            htmlSource = strIntBag._str; 
            
            org.jsoup.nodes.Document doc = Jsoup.parse(htmlSource); 
			Elements table = doc.select(".tdc2");
			
			Element province = table.get(1);
			
			result = province.text().trim();
			
			result = result.replaceAll(" ", ",");
            
        } catch (RuntimeException e) {  
            e.printStackTrace();  
        }
          
        return result;  
          
    }  
      
    /** 
     * 验证手机号 
     * @param mobileNumber 
     * @return 
     */  
    public static boolean veriyMobile(String mobileNumber){  
        Pattern p=null;  
        Matcher m=null;  
          
        p=Pattern.compile(REGEX_IS_MOBILE);  
        m=p.matcher(mobileNumber);  
          
        return m.matches();  
    }  
      
    /** 
     * 测试 
     * @param args 
     * @throws Exception  
     */  
    public static void main(String[] args) throws Exception {  
        System.out.println(getMobileFrom("13800270503"));  
    }  
  
}  