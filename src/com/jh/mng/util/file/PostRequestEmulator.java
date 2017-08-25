package com.jh.mng.util.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.common.Config;
import com.jh.mng.common.StrIntBag;
import com.jh.mng.controller.ds.HyController;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.ImageUtils;

public class PostRequestEmulator {
	
	private static final Logger logger = Logger.getLogger(HyController.class);
	
	 public static void main(String[] args)throws Exception
     {
               // 设定服务地址
//               String serverUrl ="http://api.yundama.com/api.php?method=upload";
//              
//               // 设定要上传的普通Form Field及其对应的value
//               // 类FormFieldKeyValuePair的定义见后面的代码
//               ArrayList<FormFieldKeyValuePair> ffkvp = new ArrayList<FormFieldKeyValuePair>();
//               ffkvp.add(new FormFieldKeyValuePair("username", "imacback"));
//               ffkvp.add(new FormFieldKeyValuePair("password", "bye_195139"));
//               ffkvp.add(new FormFieldKeyValuePair("codetype", "4004"));
//               ffkvp.add(new FormFieldKeyValuePair("appid", "2568"));
//               ffkvp.add(new FormFieldKeyValuePair("appkey", "5f898280e28bdab641abb8e490ecb12b"));
//               ffkvp.add(new FormFieldKeyValuePair("timeout", "60"));
//              
//               // 设定要上传的文件。UploadFileItem见后面的代码
//               ArrayList<UploadFileItem> ufi = new ArrayList<UploadFileItem>();
//               ufi.add(new UploadFileItem("file", "E:/2.png"));
//               
//               // 类HttpPostEmulator的定义，见后面的代码
//               HttpPostEmulator hpe = new HttpPostEmulator();
//               String response =hpe.sendHttpPostRequest(serverUrl, ffkvp, ufi);
//               System.out.println("Responsefrom server is: " + response);
               
               String imgUrl = "http://wap.dm.10086.cn/capability/capacc/imgCode?session=1002691228&randnum=145060";
               
               yundama(imgUrl);
     }
	 
	 public static String yundama(String imgUrl) {
		 String code = "";
		 
		 try {
			// 设定服务地址
	         String serverUrl ="http://api.yundama.com/api.php?method=upload";
	        
	         // 设定要上传的普通Form Field及其对应的value
	         // 类FormFieldKeyValuePair的定义见后面的代码
	         ArrayList<FormFieldKeyValuePair> ffkvp = new ArrayList<FormFieldKeyValuePair>();
	         ffkvp.add(new FormFieldKeyValuePair("username", "imacback"));
	         ffkvp.add(new FormFieldKeyValuePair("password", "bye_195139"));
	         ffkvp.add(new FormFieldKeyValuePair("codetype", "4004"));
	         ffkvp.add(new FormFieldKeyValuePair("appid", "2568"));
	         ffkvp.add(new FormFieldKeyValuePair("appkey", "5f898280e28bdab641abb8e490ecb12b"));
	         ffkvp.add(new FormFieldKeyValuePair("timeout", "60"));
	        
	         String imgId = String.valueOf(System.nanoTime());
	         
	         ImageUtils.downLoadImg(imgUrl, Config.get().get("imgPath") + imgId + ".jpg");
	         
	         // 设定要上传的文件。UploadFileItem见后面的代码
	         ArrayList<UploadFileItem> ufi = new ArrayList<UploadFileItem>();
	         ufi.add(new UploadFileItem("file", Config.get().get("imgPath") + imgId + ".jpg"));
	         
	         // 类HttpPostEmulator的定义，见后面的代码
	         HttpPostEmulator hpe = new HttpPostEmulator();
	         String response = hpe.sendHttpPostRequest(serverUrl, ffkvp, ufi);
	         
	         if (StringUtils.isNotEmpty(response)) {
	        	 JSONObject jObject = JSONObject.parseObject(response);
	        	 String ret = jObject.getString("ret");
	        	 String text = jObject.getString("text");
	        	 String cid = jObject.getString("cid");
	        	 
	        	 if ("0".equals(ret)) {
	        		 if (StringUtils.isNotEmpty(text)) {
	        			 code = text;
	        		 } else {
	        			 code = tryExecuteGetCode(cid);
	        			 
	        			 if (StringUtils.isEmpty(code)) {
	        				 PostRequestEmulator.report(cid);
	        			 }
	        		 }
	        	 } else { //失败刚上传错误编号
	        		 PostRequestEmulator.report(cid);
	        	 }
	         }
		} catch (Exception e) {
		}
         return code;
	 }
	 
	 public static String getCode(String cid) {
		 String getCodeUrl = "http://api.yundama.com/api.php?method=result&cid=" + cid;
		 
		 StrIntBag bag = HttpClientUtil.executeGet(getCodeUrl);
		 
		 if (bag != null && StringUtils.isNotEmpty(bag._str)) {
			 JSONObject object = JSONObject.parseObject(bag._str);
			 
			 String ret = object.getString("ret");
			 String text = object.getString("text");
			 
			 if ("0".equals(ret)) {
				 return text;
			 }
		 }
		 
		 return null;
	 }
	 
	 public static String tryExecuteGetCode(String cid){
			String code = getCode(cid);
			if(StringUtils.isNotEmpty(code)){
				return code;
			}
			int tryCount = 10;
			while(StringUtils.isEmpty(code) && (tryCount--) > 0 ){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				code = getCode(cid);
			}
			return code;
		}
	 
	 public static void report(String cid) {
		 try {
				// 设定服务地址
		         String serverUrl ="http://api.yundama.com/api.php?method=report";
		         
		         Map<String, String> map = new HashMap<String, String>();
		         map.put("username", "imacback");
		         map.put("password", "bye_195139");
		         map.put("appid", "2568");
		         map.put("appkey", "5f898280e28bdab641abb8e490ecb12b");
		         map.put("flag", "0");
		         map.put("cid", cid);
		        
		         String result = HttpTookit.doPost(serverUrl, map);
		         
		         JSONObject object = JSONObject.parseObject(result);
		         
		         String ret = object.getString("ret");
		         
		         if ("0".equals(ret)) {
		        	 logger.info("cid:" + cid + ", report success"); 
		         } else {
		        	 logger.info("cid:" + cid + ", report failed"); 
		         }
		         
			} catch (Exception e) {
				logger.info("cid:" + cid + ", report failed"); 
			}
	 }
	 
}
