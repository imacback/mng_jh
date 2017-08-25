package com.jh.mng.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.common.Config;
import com.jh.mng.common.StrIntBag;


public class Methods {

	public static String[] orderByDictionary(String[] arrary) {
		if (arrary != null && arrary.length > 0) {
			String tmp = "";
			for (int i = 0; i < arrary.length; i++) {
				for (int j = 0; j < i; j++) {
					if (arrary[j].compareTo(arrary[i]) > 0) {
						tmp = arrary[i];
						arrary[i] = arrary[j];
						arrary[j] = tmp;
					}
				}
			}
			return arrary;
		}
		return null;
	}
	
	public static String getConfigString ( String fileName,String keyword ) {
		String valueString = "";
		Properties props = new Properties();
		InputStream is = Methods.class.getResourceAsStream(fileName);
		try {
			try {
				props.load(is);
				valueString = props.getProperty(keyword);
			} finally {
				if (null != is)
					is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return valueString;
	}
	
	public static boolean isEmpty (String str) {
		if ( str == null || "".equals(str) ) {
			return true;
		} else {
			return false;
		}
	}
	
    public static String getProvinceByIp(String ip) {
    	String provinceName = "未知";
    	
    	try {
			StrIntBag jsonStr = HttpClientUtil.executeGet(Config.get().get("getIpUrl") + "=" + ip);
			
			if (jsonStr != null) {
				JSONObject jObject = JSONObject.parseObject(jsonStr._str);
				JSONObject dataObject = JSONObject.parseObject(jObject.get("data").toString());
				String name = dataObject.getString("region");
				provinceName = GetState(getStateId(name));
			}
		} catch (Exception e) {
		}
		
		return provinceName;
    }
    
    public static String getStateId(String name) {
		String id = "00";

		if (name.startsWith("北京")) {
			id = "01";
		}

		if (name.startsWith("天津")) {
			id = "02";
		}

		if (name.startsWith("上海")) {
			id = "03";
		}

		if (name.startsWith("重庆")) {
			id = "04";
		}

		if (name.startsWith("河北")) {
			id = "05";
		}

		if (name.startsWith("山西")) {
			id = "06";
		}

		if (name.startsWith("河南")) {
			id = "07";
		}

		if (name.startsWith("辽宁")) {
			id = "08";
		}

		if (name.startsWith("吉林")) {
			id = "09";
		}

		if (name.startsWith("黑龙江")) {
			id = "10";
		}

		if (name.startsWith("内蒙古")) {
			id = "11";
		}

		if (name.startsWith("江苏")) {
			id = "12";
		}

		if (name.startsWith("山东")) {
			id = "13";
		}

		if (name.startsWith("安徽")) {
			id = "14";
		}

		if (name.startsWith("浙江")) {
			id = "15";
		}

		if (name.startsWith("福建")) {
			id = "16";
		}

		if (name.startsWith("湖北")) {
			id = "17";
		}

		if (name.startsWith("湖南")) {
			id = "18";
		}

		if (name.startsWith("广东")) {
			id = "19";
		}

		if (name.startsWith("广西")) {
			id = "20";
		}

		if (name.startsWith("江西")) {
			id = "21";
		}

		if (name.startsWith("四川")) {
			id = "22";
		}

		if (name.startsWith("贵州")) {
			id = "23";
		}

		if (name.startsWith("云南")) {
			id = "24";
		}

		if (name.startsWith("西藏")) {
			id = "25";
		}

		if (name.startsWith("海南")) {
			id = "26";
		}

		if (name.startsWith("陕西")) {
			id = "27";
		}

		if (name.startsWith("甘肃")) {
			id = "28";
		}

		if (name.startsWith("宁夏")) {
			id = "29";
		}

		if (name.startsWith("青海")) {
			id = "30";
		}

		if (name.startsWith("新疆")) {
			id = "31";
		}

		if (name.startsWith("未知")) {
			id = "00";
		}

		return id;
	}
	
	static String states[] = { "北京", "天津", "上海", "重庆", "河北", "山西", "河南", "辽宁", "吉林",
			"黑龙江", "内蒙古", "江苏", "山东", "安徽", "浙江", "福建", "湖北", "湖南", "广东", "广西",
			"江西", "四川", "贵州", "云南", "西藏", "海南", "陕西", "甘肃", "宁夏", "青海", "新疆" };

	public static String GetState(String idx) {
		String ret = "未知";
		try {
			int i = Integer.parseInt(idx);
			if (i > 0 && i < 32) {
				ret = states[i - 1];
			}
		} catch (Exception e) {
		}

		return ret;
	}
	
	/**
	 * @param urlAll
	 *            :请求接口
	 * @param httpArg
	 *            :参数
	 * @return 返回结果
	 */
	public static String request(String httpArg) {
	    BufferedReader reader = null;
	    String result = "未知";
	    StringBuffer sbf = new StringBuffer();
	    String httpUrl = "http://apis.baidu.com/apistore/mobilephoneservice/mobilephone?tel=" + httpArg;

	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        // 填入apikey到HTTP header
	        connection.setRequestProperty("apikey",  "76e074a0d0ff2c54dfde229b7e5d60d0");
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        String resp = sbf.toString();
	        
	        JSONObject respJson = JSONObject.parseObject(resp);
	        
	        if ("0".equals(respJson.getString("errNum"))) {
	        	JSONObject object = JSONObject.parseObject(respJson.get("retData").toString());
	        	result = object.getString("province");
	        	
	        	if ("-".equals(result)) {
	        		result = "未知";
	        	}
	        	
	        	if (result.startsWith("内蒙")) {
	        		result = "内蒙古";
	        	}
	        	
	        	if (result.startsWith("全国")) {
	        		result = "未知";
	        	}
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
}
