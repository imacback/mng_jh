package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.DdoPhone;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.RequestUtil;
import com.jh.mng.util.file.PostRequestEmulator;

/**
 * 动漫wap计费
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class DmWapController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(DmWapController.class);

	@RequestMapping("/dmWapOrder.do")
	public ModelAndView dmWapOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("动漫WAP业务新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String mobile = RequestUtil.GetParamString(request, "mobile", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String transido = String.valueOf(System.currentTimeMillis() /1000);
			
			Long id = orderService.getNextOrderId();
			String ext = "";
			String versionid = "none";
			String chnlId = cpparam.substring(0,2);
			
			
			//生成本地订单
			String userid = mobile;
			String cpserviceid = "none";
			
			if (StringUtils.isEmpty(transido)) {
				transido = String.valueOf(System.currentTimeMillis());
			}
			
			String phoneno = mobile;
			String packageid = "1000";
			
			String province = getStateByMobile(phoneno);
			province = GetState(province);
			
			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setId(id);
			receiveSms.setUserid(userid);
			receiveSms.setCpserviceid(cpserviceid);
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(HRET_FIRST);
			receiveSms.setStatus(STATUS_FIRST);
			receiveSms.setTransido(transido);
			receiveSms.setVersionid(versionid);
			receiveSms.setPackageid(packageid);
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(Integer.parseInt(chnlId));
			receiveSms.setFee(0);
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phoneno);
			receiveSms.setExt(ext);
			
			orderService.createReceiveSms(receiveSms);
			
			HttpClient httpClient = new HttpClient();
			
			GetMethod getMethod0 = new GetMethod("http://wap.dm.10086.cn/portalone/u.do?id=" + feecode + "&cc=800001623");
			
			httpClient.executeMethod(getMethod0);
			
			String text0 = getMethod0.getResponseBodyAsString();
			String urlStr = getMethod0.getQueryString();
			
			String[] array = urlStr.split("&");
			
			String opus_id = "";
			String chann_id = "";
			
			for (int i = 0; i < array.length; i++) {
				String tmp = array[i];
				String[] tmpArray = tmp.split("=");
				
				if (tmpArray.length == 2) {
					if (tmpArray[0].equals("opus_id")) {
						opus_id = tmpArray[1];
					}
					if (tmpArray[0].equals("chann_id")) {
						chann_id = tmpArray[1];
					}
				}
			}
			
			org.jsoup.nodes.Document doc = Jsoup.parse(text0); 
			org.jsoup.nodes.Element element = doc.getElementById("btnBuyWhole");
			
			if (element != null) {
				String click = element.attr("onclick");
				//toBuyAll('000000166849',5.00,'MDSP2000397074',5.00,5.00)
				int index1 = click.indexOf("(") + 1;
				int index2 = click.indexOf(")");
				
				String pams = click.substring(index1, index2);
				
				String[] p = pams.split(",");
				
				String hwId = p[0].replaceAll("'", "");
				int p1 = ((Double) Double.parseDouble(p[1])).intValue();
				String p0 = p[2].replaceAll("'", "");
				int p2 = ((Double) Double.parseDouble(p[3])).intValue();
				int p3 = ((Double) Double.parseDouble(p[4])).intValue();
				
				String url = "http://wap.dm.10086.cn/waph5/auth/query_spend_tip.html?product_type=1&isPoint=1&pmy=" + p1 + "&whole_product_price=" + p2 + "&product_id=" + p0 + "&opus_id=" + opus_id + "&chann_id=" + chann_id + "&fromLogin=1&hwId=" + hwId + "&whole_product_discount=" + p3;
				
				DdoPhone ddoPhone = orderService.queryByPhone(mobile); //处理过ddo业务
				
				if (ddoPhone != null) {
					
				}
				
				getMethod0 = new GetMethod(url);
				httpClient.executeMethod(getMethod0);
				
				text0 = getMethod0.getResponseBodyAsString();
				
				 doc = Jsoup.parse(text0); 
				 
				 String CheckImgCode = PostRequestEmulator.yundama("https://passport.migu.cn/captcha/graph?t=" + System.currentTimeMillis());
				 
				 if (CheckImgCode != null) {
					 PostMethod postMethod1 = new PostMethod("https://passport.migu.cn/captcha/graph/check");
					 
					 NameValuePair[] data1 = {
				                new NameValuePair("isAsync", "true"),
				                new NameValuePair("captcha", CheckImgCode),
				        };
					 
					    Map<String, String> headMap = getHeadMap();
					    
					    headMap.put("Referer", "https://passport.migu.cn/login?" + getMethod0.getQueryString());
					    
					    for (Map.Entry<String, String> entry : headMap.entrySet()) {  
					    	postMethod1.addRequestHeader(entry.getKey(),entry.getValue());  
					    }
					 
				        postMethod1.setRequestBody(data1);
//				        int statusCode = httpClient.executeMethod(postMethod1);
				        
				        String text = postMethod1.getResponseBodyAsString();
				        
				        if (StringUtils.isNotEmpty(text)) {
				        	JSONObject object = JSONObject.parseObject(text);
				        	
				        	String status= object.getString("status");
				        	
				        	if ("2000".equals(status)) {
				        		
				        		url = "https://passport.migu.cn/login/dynamicpassword?isAsync=true&msisdn=" + mobile + "&captcha=" + CheckImgCode + "&sourceID=205020&_=" + System.currentTimeMillis();
				        		getMethod0 = new GetMethod(url);
								httpClient.executeMethod(getMethod0);
								
								text0 = getMethod0.getResponseBodyAsString();
								
								if (StringUtils.isNotEmpty(text0)) {
									object = JSONObject.parseObject(text0);
									
									status = object.getString("status");
									String header = object.getString("header");
									
									object = JSONObject.parseObject(header);
									
									String resultcode = object.getString("resultcode");
									
									if ("2000".equals(status) && "104000".equals(resultcode)) {
										
									}
								}
								
				        	}
				        }
		
					 
				 }
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
//				String loginUrl = "http://218.207.208.20:1884/ClientOP/hessian/UserAuthService";
//				
//				PostMethod postMethod1 = new PostMethod("http://218.207.208.20:1884/ClientOP/hessian/UserAuthService");
//			   	
//				String appId = "mgdm_hdm";
//				String appKey = "bJ451dgW";
//				long l = System.currentTimeMillis();
//				
//				
//				
//		        //设置登陆时要求的信息，一般就用户名和密码，验证码自己处理了
//		        NameValuePair[] data1 = {
//		                new NameValuePair("mobile_num", this.Encrypt("pokjkmhfgqzdftry", "13811455759")),
//		                new NameValuePair("password", this.Encrypt("pokjkmhfgqzdftry", "123_456")),
//		                new NameValuePair("is_encrypted", "true"),
//		                new NameValuePair("is_supportIDMP", "True"),
//		                new NameValuePair("disable_migu", "0"),
//		                new NameValuePair("app_id", appId),
//		                new NameValuePair("timestamp", String.valueOf(l)),
//		                new NameValuePair("md5str", MD5Util.getMD5(appId + appKey + String.valueOf(l))),
//		                new NameValuePair("channel_id", "0000"),
//		                new NameValuePair("version", "5.0.160929"),
//		                new NameValuePair("loginType", "3"),
//		                new NameValuePair("ua", "Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I"),
//		        };
//		        postMethod1.setRequestBody(data1);
//		        int statusCode = httpClient.executeMethod(postMethod1);
//		        
//		        String text = postMethod1.getResponseBodyAsString();
//		        
//		        System.out.println(text);
//		        
//		        String location = "";
//	            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY  
//	                    || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {  
//	            	
//	            	System.out.println(postMethod1.getResponseHeader("location").getValue());
//	                // 从头中取出转向的地址  
////	                 locationHeader = postMethod1.getResponseHeader("location");  
////	                if (postMethod1.getResponseHeader("location")) {  
////	                     location = locationHeader.getValue();  
////	                } else {  
////	                    logger.error("Location field value is null.");  
////	                }  
//	            } else {
//	            	System.out.println("" + postMethod1.getStatusLine());
//	            }
			}
			
//			getMethod0 = new GetMethod("http://223.111.8.142:8080/migunetsdk/webJs/frame.html?netId=502e681dccaf41358f3c6d3408237d89");
//			httpClient.executeMethod(getMethod0);
//			
//			text0 = getMethod0.getResponseBodyAsString();
//			
//			int begin = text0.indexOf("var __url_bin") + 15;
//			int endIndex = text0.indexOf(";", text0.indexOf("var __url_bin"));
//			
//			String binUrl = text0.substring(begin, endIndex - 1);
//			
//			getMethod0 = new GetMethod(binUrl);
//			httpClient.executeMethod(getMethod0);
//			
//			System.out.println(binUrl);
			
//			String ssoUrl = "";
//			
//			if (text0.indexOf("var ssoUrl") > 0) {
//				int beginIndex = text0.indexOf("var ssoUrl") + 14;
//				int endIndex = text0.indexOf(";", text0.indexOf("var ssoUrl"));
//				ssoUrl = text0.substring(beginIndex, endIndex - 1);
//			}
//			
//			GetMethod getMethod1 = new GetMethod(ssoUrl);
//			httpClient.executeMethod(getMethod1);
//			String text3 = getMethod1.getResponseBodyAsString();
			
			
//			int beginIndex = text0.indexOf("var paycode") + 15;
//			int endIndex = text0.indexOf(";", text0.indexOf("var paycode"));
//			
//			String payCode = text0.substring(beginIndex,endIndex -1);
//			
//			beginIndex = text0.indexOf("var sessionid") + 17;
//			endIndex = text0.indexOf(";", text0.indexOf("var sessionid"));
//			String sessionId = text0.substring(beginIndex,endIndex -1);
//			
//			beginIndex = text0.indexOf("var orderId") + 15;
//			endIndex = text0.indexOf(";", text0.indexOf("var orderId"));
//			String orderId_mg = text0.substring(beginIndex,endIndex -1);
//			
//			ext = payCode + "|" + sessionId + "|" + orderId_mg;
//			
//			//图形验证码检测
//			boolean checkImgResult = false;
//			
//			org.jsoup.nodes.Document doc = Jsoup.parse(text0); 
//			org.jsoup.nodes.Element element = doc.getElementById("authCodeImg");
//			
//			String imgUrl = element.attr("src");
//			
//			String CheckImgCode = PostRequestEmulator.yundama(imgUrl);
//			
//			String checkCodeUrl = "http://wap.dm.10086.cn/apay/handle.jsp?t=checkImgCode&paycode="+ payCode+ "&sss=" + sessionId+"&answer=" + CheckImgCode;
//			
//			StrIntBag checkImgBag = HttpClientUtil.executeGet(checkCodeUrl); 
//			
//			if (checkImgBag != null && StringUtils.isNotEmpty(checkImgBag._str)) {
//				JSONObject checkObject = JSONObject.parseObject(checkImgBag._str);
//				String checkResult = checkObject.getString("ReturnCode");
//				
//				if ("0".equals(checkResult)) {
//					checkImgResult = true;
//				}
//			}
//			
//			if (checkImgResult) {
//				receiveSms.setHret(HRET_SECOND);
//				receiveSms.setStatus(STATUS_SECOND);
//				
//				orderService.updateReceiveSms(receiveSms);
//				
//				String urlGetCode = "http://wap.dm.10086.cn/apay/handle.jsp?t=checkcode&paycode=" + payCode + "&sss=" + sessionId + "&order=" + orderId_mg + "&msisdn=" + receiveSms.getPhoneno();
//				
//				StrIntBag getCodeBag = HttpClientUtil.executeGet(urlGetCode);
//				
//				if (getCodeBag != null && getCodeBag._int == 200) {
//					
//					JSONObject jObject2 = JSONObject.parseObject(getCodeBag._str);
//					String returnCode = jObject2.getString("ReturnCode");
//					
//					if ("0".equals(returnCode)) {
//						receiveSms.setExt(ext);
//						orderService.updateReceiveSms(receiveSms);
//						jObject.put("orderid", receiveSms.getId());
//						jObject.put("status", STATUS_RESP_SUCCESS);
//					} else {
//						jObject.put("orderid", receiveSms.getId());
//						jObject.put("status", returnCode);
//					}
//				}
//				
//				if (StringUtils.isNotEmpty(receiveSms.getExt())) {
//					jObject.put("orderid", id);
//					jObject.put("status", STATUS_RESP_SUCCESS);
//				}
//			} else {
//				jObject.put("orderid", id);
//				jObject.put("status", STATUS_RESP_FAILED);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyDdoOrder error : " + e.getMessage());
		}
		logBuffer.append(",服务端响应为：" + jObject.toString());
		
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	@RequestMapping("/dmWapVerifyCode.do")
	public ModelAndView dmWapVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		JSONObject respJson = new JSONObject();
		respJson.put("status", STATUS_RESP_FAILED);
		
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("汉娱DDO业务短信验证参数为： " + requestStr);
		
		try {
//			HttpClient httpClient = new HttpClient();
			PostMethod postMethod1 = new PostMethod("https://passport.migu.cn/authn/dynamicpassword");
			 
			 NameValuePair[] data1 = {
		                new NameValuePair("isAsync", "true"),
		                new NameValuePair("sourceID", "205020"),
		                new NameValuePair("msisdn", "13552010609"),
		                new NameValuePair("securityCode", "6520"),
		                new NameValuePair("captcha", "35"),
		                new NameValuePair("dynamicPassword", "205020"),
		        };
		        postMethod1.setRequestBody(data1);
//		        int statusCode = httpClient.executeMethod(postMethod1);
//		        
//		        String text = postMethod1.getResponseBodyAsString();
//			pw = response.getWriter();
//			
//			String orderid = RequestUtil.GetParamString(request, "orderid", null);
//			String verify_code = RequestUtil.GetParamString(request, "verifycode", null);
//			
//			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
//			
//			if (receiveSms != null && receiveSms.getStatus().equals(STATUS_SECOND)) {
//				receiveSms.setHret(HRET_THIRD);
//					   
//				String ext = receiveSms.getExt();
//				String[] strings = ext.split("\\|");
//				
//				String paycode = strings[0];
//				String sessionId = strings[1];
//				String orderId = strings[2];
//					   
//			    String url = "http://wap.dm.10086.cn/apay/handle.jsp?t=pay&paycode=" + paycode + "&sss=" + sessionId + "&order=" + orderId + "&msisdn=" + receiveSms.getPhoneno() + "&checkcode=" + verify_code;
//				StrIntBag resp = HttpClientUtil.executeGet(url);
//				
//				MyLog.InfoLog("hyDdoVerifyCode request : " + url + ", resp: " + resp);
//				
//				if (resp != null && resp._str != null) {
//					JSONObject respObject = JSONObject.parseObject(resp._str);
//					
//					String status = respObject.getString("ReturnCode");
//					
//					if (status != null && status.equals("0")) {
//						respJson.put("status", STATUS_RESP_SUCCESS);
//						receiveSms.setStatus(STATUS_THIRD); //计费短信发送成功
//					} else {
//						respJson.put("status", respObject.getString("ReturnCode"));
//						receiveSms.setStatus(status);
//					}
//				}
//				
//				orderService.updateReceiveSms(receiveSms);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyDdoVerifyCode error : " + e.getMessage());
		}
		
		logBuffer.append(",服务端响应： " + respJson.toString());
		logger.info(logBuffer);
		
		pw.print(respJson.toString());
		return null;
	}
	
	
	
	  public static String Encrypt(String paramString1, String paramString2)
	    throws Exception
	  {
	    if (paramString1 == null)
	    {
	      System.out.print("Key为空null");
	      return null;
	    }
	    if (paramString1.length() != 16)
	    {
	      System.out.print("Key长度不是16位");
	      return null;
	    }
	    SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramString1.getBytes("utf-8"), "AES");
	    Cipher localCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    localCipher.init(1, localSecretKeySpec);
	    byte[] arrayOfByte = localCipher.doFinal(paramString2.getBytes("utf-8"));
	    if (arrayOfByte == null)
	      return "";
	    StringBuffer localStringBuffer = new StringBuffer(2 * arrayOfByte.length);
	    for (int i = 0; i < arrayOfByte.length; i++)
	      localStringBuffer.append("0123456789abcdef".charAt(0xF & arrayOfByte[i] >> 4)).append("0123456789abcdef".charAt(0xF & arrayOfByte[i]));
	    return localStringBuffer.toString();
	  }
	  
		private Map<String, String> getHeadMap() {
			Map<String, String> headMap = new HashMap<String, String>();
			headMap.put("Host", "passport.migu.cn");
			headMap.put("connection", "keep-alive");
			headMap.put("Accept-Encoding", "gzip");
			
			return headMap;
		}
}
