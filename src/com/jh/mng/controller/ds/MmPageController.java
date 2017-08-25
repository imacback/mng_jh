package com.jh.mng.controller.ds;

import java.io.InputStream;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.common.Config;
import com.jh.mng.common.StrIntBag;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.Mask;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.Base64Util;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.HttpsClientUtils;
import com.jh.mng.util.RequestUtil;

/**
 * MM网页计费
 * @author admin
 * 公钥：MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAJ2K5XhIC058P+WKUAXyUoljFuKORlmAKnXEmVq7G1Vli4i2tFxUxurjcZIEDe49L1EVna19F7ZAVZhpIqaAYV8+H8BEHnIqNeZqLdgVZIfuMUfq/SryrcmT3FQaO5Y6l+KA4ZCiqvXc6+P491hZlGKkMl6UPPp+ZqlGJM4CFlGS
私钥：MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIURerpGXmaMlfodFLeowbws+t+DPg=

测试地址：
PC端订购页跳转地址：http://210.75.5.244:80/www/pay.action
手机端订购页跳转地址：http://210.75.5.244:80/wabp/pay.action
退订接口地址：http://210.75.5.244:80/unsubscribe4ap
 *
 */
@Controller
@RequestMapping("/ds")
public class MmPageController extends AbstractMultiActionController{
	
	public static final Logger logger = Logger.getLogger(MmPageController.class);
	
	// 默认字符编码
	private static final String DEFAULT_CHARSET = "GBK";
	// 默认加密算法
	private static final String DEFAULT_SIGN_METHOD = "DSA";
	
	@RequestMapping("/mmPageOrder.do")
	public ModelAndView mmPageOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		String times = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(times + "MM网页计费新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "mobile", null);
			
//			ConsumeInfo consumeInfo = orderService.queryConsumeInfoById(Long.parseLong(feecode));
			
			String province = "00";
			
			province = getStateByMobile(phone);
			province = GetState(province);
			
			String midStr = phoneToMid(phone);
			
			Integer channelId = Integer.parseInt(cpparam.substring(0,2));

			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setHret(HRET_FAILED);
			receiveSms.setStatus(STATUS_FAILED);
			
			Long id = orderService.getNextOrderId();
			
			jObject.put("orderid", id);
			
			Map<String, Object> data = new TreeMap<String, Object>();
			data.put("apco", "hanjoys");
			data.put("aptid", Base64Util.encode(phone.getBytes()));
			data.put("aptrid", String.valueOf(id));
			data.put("ch", Config.get().get("ch_mm"));
			data.put("ex", Config.get().get("ex_mm"));
			data.put("sin", feecode);
			data.put("bu", Base64Util.encode("http://www.hanjoys.com".getBytes()));
			data.put("xid", "");
			data.put("mid", midStr);
			
			String sign = buildSign(Config.get().get("hyprikey"), data);
			
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append("apco=hanjoys");
			reqBuffer.append("&aptid=");
			reqBuffer.append(Base64Util.encode(phone.getBytes()));
			reqBuffer.append("&aptrid=");
			reqBuffer.append(id);
			reqBuffer.append("&ch=");
			reqBuffer.append(Config.get().get("ch_mm"));
			reqBuffer.append("&ex=");
			reqBuffer.append(Config.get().get("ex_mm"));
			reqBuffer.append("&sin=");
			reqBuffer.append(feecode);
			reqBuffer.append("&bu=");
			reqBuffer.append(Base64Util.encode("http://www.hanjoys.com".getBytes()));
			reqBuffer.append("&xid=");
			reqBuffer.append("&mid=");
			reqBuffer.append(midStr);
			reqBuffer.append("&sign=");
			reqBuffer.append(sign);
			
			String reqTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			StrIntBag bag = HttpClientUtil.executeGet(Config.get().get("mmpageurl") + "pay_h5.action?" + reqBuffer.toString());
			
			String respTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			logger.info( reqTimes + "mmpage req : " + reqBuffer.toString() + ", " + respTimes + "bag resp is : " + bag + ", 响应时间:" + (DateTool.StringToDate(respTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(reqTimes, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000);
			
			if (bag != null && StringUtils.isNotEmpty(bag._str)) {
				
				org.jsoup.nodes.Document doc = Jsoup.parse(bag._str); 
				org.jsoup.nodes.Element element = doc.getElementById("sendmessage");
				
				org.jsoup.nodes.Element element_innerId = doc.getElementById("inner_id");
				org.jsoup.nodes.Element element_random = doc.getElementById("random");
				
				
				if (element != null) {
					String ext = element_innerId.val() + "|" + element_random.val();
					receiveSms.setHret(HRET_FIRST);
					receiveSms.setStatus(STATUS_FIRST);
					receiveSms.setExt(ext);
					jObject.put("status", STATUS_RESP_SUCCESS);
					
//					String codeUrl = Config.get().get("mmpageurl") + "wabpWapOrder!sendSmsCode.action?msisdn=" + midStr + "&inner_id=" + element_innerId.val() + "&ch=10960&ex=10443&random=" + element_random.val() + "&aptrid=" + id;
//					
//					String codeResp = HttpTookit.doPost(codeUrl, null);
//					
//					logger.info("mmpage sendCode req:" + codeUrl + ",resp :" + codeResp);
//					
//					if (codeResp != null) {
//						org.jsoup.nodes.Document doc_code = Jsoup.parse(codeResp);
//						org.jsoup.nodes.Element element_result = doc_code.getElementById("result");
//						
//						if (element_result != null && element_result.val().equals("1")) {
//							String ext = element_innerId.val() + "|" + element_random.val();
//							receiveSms.setHret(HRET_FIRST);
//							receiveSms.setStatus(STATUS_FIRST);
//							receiveSms.setExt(ext);
//							jObject.put("status", STATUS_RESP_SUCCESS);
//						}
//					}
				}
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setTransido(String.valueOf(System.currentTimeMillis()));
			receiveSms.setVersionid("none");
			receiveSms.setPackageid("1000");
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(channelId);
			receiveSms.setFee(0);
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phone);
			
			orderService.createReceiveSms(receiveSms);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mmPageOrder error : " + e.getMessage());
		}
		
		String endTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		logBuffer.append(", " + endTimes + " ,服务端响应为：" + jObject.toString() + ", 响应时间：" + (DateTool.StringToDate(endTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(times, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	@RequestMapping("/newMmPageOrder.do")
	public ModelAndView newMmPageOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		String times = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(times + "MM网页计费新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "mobile", null);
			
			String province = "00";
			
			province = getStateByMobile(phone);
			province = GetState(province);
			
			String midStr = phoneToMid(phone);
			
			Integer channelId = Integer.parseInt(cpparam.substring(0,2));

			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setHret(HRET_FAILED);
			receiveSms.setStatus(STATUS_FAILED);
			
			Long id = orderService.getNextOrderId();
			
			jObject.put("orderid", id);
			
			Map<String, Object> data = new TreeMap<String, Object>();
			data.put("apco", "hanjoys");
			data.put("aptid", Base64Util.encode(phone.getBytes()));
			data.put("aptrid", String.valueOf(id));
			data.put("ch", Config.get().get("ch_mm"));
			data.put("ex", Config.get().get("ex_mm"));
			data.put("sin", feecode);
			data.put("bu", Base64Util.encode("http://www.hanjoys.com".getBytes()));
			data.put("xid", "");
			data.put("mid", midStr);
			
			String sign = buildSign(Config.get().get("hyprikey"), data);
			
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append("apco=hanjoys");
			reqBuffer.append("&aptid=");
			reqBuffer.append(Base64Util.encode(phone.getBytes()));
			reqBuffer.append("&aptrid=");
			reqBuffer.append(id);
			reqBuffer.append("&ch=");
			reqBuffer.append(Config.get().get("ch_mm"));
			reqBuffer.append("&ex=");
			reqBuffer.append(Config.get().get("ex_mm"));
			reqBuffer.append("&sin=");
			reqBuffer.append(feecode);
			reqBuffer.append("&bu=");
			reqBuffer.append(Base64Util.encode("http://www.hanjoys.com".getBytes()));
			reqBuffer.append("&xid=");
			reqBuffer.append("&mid=");
			reqBuffer.append(midStr);
			reqBuffer.append("&sign=");
			reqBuffer.append(sign);
			
			String reqTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			StrIntBag bag = null; 
			
			String str = HttpsClientUtils.httpsClient("https://dev.10086.cn/wabps/wap/purchase_h5.action?" + reqBuffer.toString(), "");
			
//			StrIntBag bag = HttpClientUtil.executeGet("https://dev.10086.cn/wabps/wap/purchase_h5.action?" + reqBuffer.toString());
			
			String respTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			logger.info( reqTimes + "mmpage req : " + reqBuffer.toString() + ", " + respTimes + "bag resp is : " + bag + ", 响应时间:" + (DateTool.StringToDate(respTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(reqTimes, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000);
			
			if (StringUtils.isNotEmpty(str)) {
				
				org.jsoup.nodes.Document doc = Jsoup.parse(str); 
				org.jsoup.nodes.Element element = doc.getElementById("sendmessage");
				
				org.jsoup.nodes.Element element_innerId = doc.getElementById("inner_id");
				org.jsoup.nodes.Element element_checkType = doc.getElementById("checkType");
				
				
				if (element != null) {
					String ext = element_innerId.val() + "|" + element_checkType.val();
					receiveSms.setHret(HRET_FIRST);
					receiveSms.setStatus(STATUS_FIRST);
					receiveSms.setExt(ext);
					jObject.put("status", STATUS_RESP_SUCCESS);
				}
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setTransido(String.valueOf(System.currentTimeMillis()));
			receiveSms.setVersionid("none");
			receiveSms.setPackageid("1000");
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(channelId);
			receiveSms.setFee(0);
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phone);
			
			orderService.createReceiveSms(receiveSms);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mmPageOrder error : " + e.getMessage());
		}
		
		String endTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		logBuffer.append(", " + endTimes + " ,服务端响应为：" + jObject.toString() + ", 响应时间：" + (DateTool.StringToDate(endTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(times, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}

	@RequestMapping("/mmPageConfirm.do")
	public ModelAndView mmPageConfirm(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		JSONObject respJson = new JSONObject();
		respJson.put("status", STATUS_RESP_FAILED);
		
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("mm网页验证参数为： " + requestStr);
		
		try {
			pw = response.getWriter();
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "verifycode", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			String mid = phoneToMid(receiveSms.getPhoneno());
			String ext = receiveSms.getExt();
			
			String[] array = ext.split("\\|");
			
			String innerId = array[0];
			String random = array[1];
			
			String confirmUrl = Config.get().get("mmpageurl") + "wabpWapOrder20!voifyCode.action?msisdn=" + mid + "&aptid=" + Base64Util.encode(receiveSms.getPhoneno().getBytes()) + "&inputCode=" + verify_code + "&inner_id=" + innerId + "&aptrid=" + orderid;
			
			String bag = HttpTookit.doPost(confirmUrl, null);
			
			logger.info("mmPageConfirm req : " + confirmUrl + ", resp is : " + bag );
			
			if (bag != null) {
				org.jsoup.nodes.Document doc_code = Jsoup.parse(bag);
				org.jsoup.nodes.Element element_result = doc_code.getElementById("result");
				
				if (element_result != null && element_result.val().equals("1")) {
					String formUrl = Config.get().get("mmpageurl") + "wfeen.action";
					
					StringBuffer reqBuffer = new StringBuffer();
					reqBuffer.append("inner_id=");
					reqBuffer.append(innerId);
					reqBuffer.append("&apco=hanjoys");
					reqBuffer.append("&aptid=");
					reqBuffer.append(Base64Util.encode(receiveSms.getPhoneno().getBytes()));
					reqBuffer.append("&aptrid=");
					reqBuffer.append(orderid);
					reqBuffer.append("&ch=");
					reqBuffer.append(Config.get().get("ch_mm"));
					reqBuffer.append("&ex=");
					reqBuffer.append(Config.get().get("ex_mm"));
					reqBuffer.append("&bu=");
					reqBuffer.append(Base64Util.encode("http://www.hanjoys.com".getBytes()));
					reqBuffer.append("&random=");
					reqBuffer.append(random);
					reqBuffer.append("&msisdn=");
					reqBuffer.append(mid);
					reqBuffer.append("&inputMobile=");
					reqBuffer.append(receiveSms.getPhoneno());
					reqBuffer.append("&imgPass_getval=");
					reqBuffer.append(verify_code);
					
					formUrl = formUrl + "?" + reqBuffer;
					
					respJson.put("orderid", orderid);
					respJson.put("status", STATUS_RESP_SUCCESS);
					receiveSms.setStatus(STATUS_SECOND);
					
					receiveSms.setHret(HRET_SECOND);
					
					orderService.updateReceiveSms(receiveSms);
					
					String formResp = HttpTookit.doGet(formUrl, null, "UTF-8", false);
					
					logger.info("mmPageformResp req : " + formUrl + ", resp is : " + formResp );
				} else {
					receiveSms.setStatus(STATUS_SECOND);
					receiveSms.setHret(HRET_FAILED);
					orderService.updateReceiveSms(receiveSms);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mmPageConfirm error : " + e.getMessage());
		}
		
		logBuffer.append(",服务端响应： " + respJson.toString());
		logger.info(logBuffer);
		
		pw.print(respJson.toString());
		return null;
	}
	
	@RequestMapping("/newMmPageConfirm.do")
	public ModelAndView newMmPageConfirm(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		JSONObject respJson = new JSONObject();
		respJson.put("status", STATUS_RESP_FAILED);
		
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("mm网页验证参数为： " + requestStr);
		
		try {
			pw = response.getWriter();
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "verifycode", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			String mid = phoneToMid(receiveSms.getPhoneno());
			String ext = receiveSms.getExt();
			
			String[] array = ext.split("\\|");
			
			String innerId = array[0];
			String checkType = array[1];
			
//			String confirmUrl = "https://dev.10086.cn/wabps/wap/purchaseWap!voifyCode.action?msisdn=" + mid + "&aptid=" + Base64Util.encode(receiveSms.getPhoneno().getBytes()) + "&inputCode=" + verify_code + "&inner_id=" + innerId + "&aptrid=" + orderid;
	        String confirmUrl = "https://dev.10086.cn/wabps/wap/purchaseWap!voifyCode.action?msisdn=" + mid + "&inputCode=" + verify_code + "&innerId=" + innerId + "&aptrid="+orderid + "&checkType=" + checkType;
			String bag = HttpTookit.doPost(confirmUrl, null);
			
			logger.info("mmPageConfirm req : " + confirmUrl + ", resp is : " + bag );
			
			if (bag != null) {
				org.jsoup.nodes.Document doc_code = Jsoup.parse(bag);
				org.jsoup.nodes.Element element_result = doc_code.getElementById("result");
				
				if (element_result != null && element_result.val().equals("1")) {
					String formUrl = "https://dev.10086.cn/wabps/wap/purchaseConfirm_h5.action";
					
					StringBuffer reqBuffer = new StringBuffer();
					reqBuffer.append("innerId=");
					reqBuffer.append(innerId);
//					reqBuffer.append("&apco=hanjoys");
//					reqBuffer.append("&aptid=");
//					reqBuffer.append(Base64Util.encode(receiveSms.getPhoneno().getBytes()));
					reqBuffer.append("&aptrid=");
					reqBuffer.append(orderid);
//					reqBuffer.append("&ch=");
//					reqBuffer.append(Config.get().get("ch_mm"));
//					reqBuffer.append("&ex=");
//					reqBuffer.append(Config.get().get("ex_mm"));
					reqBuffer.append("&bu=");
					reqBuffer.append(Base64Util.encode("http://www.hanjoys.com".getBytes()));
					reqBuffer.append("&checkType=");
					reqBuffer.append(checkType);
					reqBuffer.append("&msisdn=");
					reqBuffer.append(mid);
					reqBuffer.append("&inputMobile=");
					reqBuffer.append(receiveSms.getPhoneno());
//					reqBuffer.append("&imgPass_getval=");
//					reqBuffer.append(verify_code);
					
					formUrl = formUrl + "?" + reqBuffer;
					
					respJson.put("orderid", orderid);
					respJson.put("status", STATUS_RESP_SUCCESS);
					receiveSms.setStatus(STATUS_SECOND);
					
					receiveSms.setHret(HRET_SECOND);
					
					orderService.updateReceiveSms(receiveSms);
					
					String formResp = HttpTookit.doGet(formUrl, null, "UTF-8", false);
					
					logger.info("mmPageformResp req : " + formUrl + ", resp is : " + formResp );
				} else {
					receiveSms.setStatus(STATUS_SECOND);
					receiveSms.setHret(HRET_FAILED);
					orderService.updateReceiveSms(receiveSms);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mmPageConfirm error : " + e.getMessage());
		}
		
		logBuffer.append(",服务端响应： " + respJson.toString());
		logger.info(logBuffer);
		
		pw.print(respJson.toString());
		return null;
	}
	
	@RequestMapping("/mmPageVertify.do")
	public ModelAndView mmPageVertify(HttpServletRequest request, HttpServletResponse response) {
		StringBuffer logBuffer = new StringBuffer();
		String respStr = "";
		try {
			PrintWriter pw = response.getWriter();
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			
			logBuffer.append("mm网页确认用户状态请求参数为： " + requestStr);
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
			String orderId = root.elementText("APTransactionID");
			String apId = root.elementText("APId");
			String serviceId = root.elementText("ServiceId");
			String serviceType = root.elementText("ServiceType");
			String channelId = root.elementText("ChannelId");
			String apContentId = root.elementText("APContentId");
			String apUserId = root.elementText("APUserId");
			String msisdn = root.elementText("Msisdn");
			String province = root.elementText("Province");
			String orderType = root.elementText("OrderType");
			String backup1 = StringUtils.isNotEmpty(root.elementText("Backup1")) ? root.elementText("Backup1") : null;
			String backup2 = StringUtils.isNotEmpty(root.elementText("Backup2")) ? root.elementText("Backup2") : null;
			String actiontime = root.elementText("Actiontime");
			String method = StringUtils.isNotEmpty(root.elementText("method")) ? root.elementText("method") : null;
			String sign = root.elementText("sign");
			
			Map<String, Object> map = new TreeMap<String, Object>(); // 用treeMap按照key做排序
			map.put("APTransactionID", orderId);
			map.put("APId", apId);
			map.put("ServiceId", serviceId);
			map.put("ServiceType", serviceType);
			map.put("ChannelId", channelId);
			map.put("APContentId", apContentId);
			map.put("APUserId", apUserId);
			map.put("Msisdn", msisdn);
			map.put("Province", province);
			map.put("OrderType", orderType);
			map.put("Backup1", backup1);
			map.put("Backup2", backup2);
			map.put("Actiontime", actiontime);
			map.put("method", method);
			
			if (verifySign(sign, Config.get().get("zwpubkey"), map)) {
				Document doc = DocumentHelper.createDocument();
				
				Element rootResp = doc.addElement("VertifyUserState2APRsp");
				rootResp.addElement("APTransactionID").setText(orderId);
				rootResp.addElement("ResultCode").setText("000");
				rootResp.addElement("ResultMSG").setText("success");
				rootResp.addElement("RspTime").setText(DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
				
				respStr = doc.asXML();
			}
			
			logBuffer.append("，服务端响应为：" + respStr);
			logger.info(logBuffer);
			
			pw.print(respStr);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mmPageVertify error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/newMmPageVertify.do")
	public ModelAndView newMmPageVertify(HttpServletRequest request, HttpServletResponse response) {
		StringBuffer logBuffer = new StringBuffer();
		String respStr = "";
		try {
			PrintWriter pw = response.getWriter();
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			
			logBuffer.append("mm网页确认用户状态请求参数为： " + requestStr);
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
			String orderId = root.elementText("APTransactionID");
			String apId = root.elementText("APId");
			String serviceId = root.elementText("ServiceId");
			String serviceType = root.elementText("ServiceType");
			String channelId = root.elementText("ChannelId");
			String apContentId = root.elementText("APContentId");
			String apUserId = root.elementText("APUserId");
			String msisdn = root.elementText("Msisdn");
			String province = root.elementText("Province");
			String orderType = root.elementText("OrderType");
			String backup1 = StringUtils.isNotEmpty(root.elementText("Backup1")) ? root.elementText("Backup1") : null;
			String backup2 = StringUtils.isNotEmpty(root.elementText("Backup2")) ? root.elementText("Backup2") : null;
			String actiontime = root.elementText("Actiontime");
			String method = StringUtils.isNotEmpty(root.elementText("method")) ? root.elementText("method") : null;
			String sign = root.elementText("sign");
			
			Map<String, Object> map = new TreeMap<String, Object>(); // 用treeMap按照key做排序
			map.put("APTransactionID", orderId);
			map.put("APId", apId);
			map.put("ServiceId", serviceId);
			map.put("ServiceType", serviceType);
			map.put("ChannelId", channelId);
			map.put("APContentId", apContentId);
			map.put("APUserId", apUserId);
			map.put("Msisdn", msisdn);
			map.put("Province", province);
			map.put("OrderType", orderType);
			map.put("Backup1", backup1);
			map.put("Backup2", backup2);
			map.put("Actiontime", actiontime);
			map.put("method", method);
			
			if (verifySign(sign, Config.get().get("zwpubkey"), map)) {
				Document doc = DocumentHelper.createDocument();
				
				Element rootResp = doc.addElement("VertifyUserState2APRsp");
				rootResp.addElement("APTransactionID").setText(orderId);
				rootResp.addElement("ResultCode").setText("000");
				rootResp.addElement("ResultMSG").setText("success");
				rootResp.addElement("RspTime").setText(DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
				
				respStr = doc.asXML();
			}
			
			logBuffer.append("，服务端响应为：" + respStr);
			logger.info(logBuffer);
			
			pw.print(respStr);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mmPageVertify error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/mmPageNotify.do")
	public ModelAndView mmPageNotify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		
		StringBuffer logBuffer = new StringBuffer();
		
		String respStr = "";
		
		try {
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			pw = response.getWriter();
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			logBuffer.append("MM网页数据同步请求参数：" + requestStr);
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
			String orderId = root.elementText("APTransactionID");
			String apId = root.elementText("APId");
			String serviceId = root.elementText("ServiceId");
			String serviceType = root.elementText("ServiceType");
			String channelId = root.elementText("ChannelId");
			String apContentId = root.elementText("APContentId");
			String apUserId = root.elementText("APUserId");
			String msisdn = root.elementText("Msisdn");
			String province = root.elementText("Province");
			String orderType = root.elementText("OrderType");
			String backup1 = StringUtils.isNotEmpty(root.elementText("Backup1")) ? root.elementText("Backup1") : null;
			String backup2 = StringUtils.isNotEmpty(root.elementText("Backup2")) ? root.elementText("Backup2") : null;
			String serviceAction = root.elementText("ServiceAction");
			String actiontime = root.elementText("Actiontime");
			String method = StringUtils.isNotEmpty(root.elementText("method")) ? root.elementText("method") : null;
			String sign = root.elementText("sign");
			
			Map<String, Object> map = new TreeMap<String, Object>(); // 用treeMap按照key做排序
			map.put("APTransactionID", orderId);
			map.put("APId", apId);
			map.put("ServiceId", serviceId);
			map.put("ServiceType", serviceType);
			map.put("ChannelId", channelId);
			map.put("APContentId", apContentId);
			map.put("APUserId", apUserId);
			map.put("Msisdn", msisdn);
			map.put("Province", province);
			map.put("OrderType", orderType);
			map.put("Backup1", backup1);
			map.put("Backup2", backup2);
			map.put("ServiceAction", serviceAction);
			map.put("Actiontime", actiontime);
			map.put("method", method);
			
			Document doc = DocumentHelper.createDocument();
			
			Element rootResp = doc.addElement("ServiceWebTransfer2APRsp");
			rootResp.addElement("APTransactionID").setText(orderId);
			rootResp.addElement("ResultCode").setText("000");
			rootResp.addElement("ResultMSG").setText("success");
			rootResp.addElement("RspTime").setText(DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			
			respStr = doc.asXML();
			
			if (verifySign(sign, Config.get().get("zwpubkey"), map)) {
				ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderId);
				
				if (receiveSms != null) {
					String today = DateTool.getCurrentDate("yyyyMMdd");
					
					if ("0".equals(serviceAction)) {
						receiveSms.setStatus(STATUS_SUCCESS);
						receiveSms.setHret(HRET_SUCCESS);
					} else {
						receiveSms.setStatus(serviceAction);
						receiveSms.setHret(HRET_THIRD);
					}
					
					ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(receiveSms.getConsumecode());
					
					if (consumeInfo != null) {
						receiveSms.setFee(consumeInfo.getFee());
						
						List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
						
						boolean flag = false;
						
						if (chnlResourceList != null && chnlResourceList.size() > 0) {
							for (ChnlResource chnlResource : chnlResourceList) {
								
								if (receiveSms.getChnl_id() == chnlResource.getChnlId().intValue()) {
									List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), receiveSms.getState());
									
									if (maskList != null && maskList.size() > 0) {
										receiveSms.setChnl_id(chnlResource.getChnlId().intValue());
										flag = true;
										break;
									}
								}
							}
						}
						
						if (!flag) {
							receiveSms.setChnl_id(-1);
						}
					} else {
						receiveSms.setChnl_id(-1);
					}
					
					//扣量验证
					int dec_flag = 0;
					
						if (receiveSms.getChnl_id() > 0) {
							dec_flag = shouldKouliangByState(today, receiveSms.getChnl_id(), receiveSms.getConsumecode(), receiveSms.getState(), receiveSms.getPackageid());
							
							receiveSms.setDec_flag(dec_flag);
							
							if (dec_flag == 1) {
								receiveSms.setSyncchnl(200);
							} else {
								receiveSms.setSyncchnl(0);
							}
						}
					
					orderService.updateReceiveSms(receiveSms);
					
//					Document doc = DocumentHelper.createDocument();
//					
//					Element rootResp = doc.addElement("ServiceWebTransfer2APRsp");
//					rootResp.addElement("APTransactionID").setText(orderId);
//					rootResp.addElement("ResultCode").setText("000");
//					rootResp.addElement("ResultMSG").setText("success");
//					rootResp.addElement("RspTime").setText(DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
//					
//					respStr = doc.asXML();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			respStr = "error";
			logger.error("mmPageNotify error :" + e.getMessage());
		}
		
		logBuffer.append(", 服务端响应为： " + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		
		return null;
	}
	
	/**
	 * 
	 * 生成数字签名字符串
	 * 
	 * @Title: buildSign
	 * @param privateKey
	 *            私钥
	 * @param data
	 *            待校验数据
	 * @return
	 * @throws Exception
	 * @author: yanhuajian 2013-9-6上午10:37:30
	 */
	public static String buildSign(String privateKey, Map<String, Object> data)
			throws Exception {

		// 按照标准url参数的形式组装签名源字符串
		String stringToSign = map2String(data);
		// 转换成二进制
		byte[] bytesToSign = stringToSign.getBytes(DEFAULT_CHARSET);

		// 初始化DSA签名工具
		Signature sg = Signature.getInstance("DSA");
		// 初始化DSA私钥
		sg.initSign((PrivateKey) getPrivateKey(privateKey));
		sg.update(bytesToSign);

		// 得到二进制形式的签名
		byte[] signBytes = sg.sign();
		// 进行标准Base64编码
		byte[] sign = Base64.encodeBase64(signBytes);
		// 转换成签名字符串
		String signContent = new String(sign);

		return signContent;
	}
	
	/**
	 * 通过私钥字符串初始化DSA的私钥
	 * 
	 * @return
	 * @throws Exception
	 */
	private static PrivateKey getPrivateKey(String privateKeyStr)
			throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(DEFAULT_SIGN_METHOD);
		EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
				Base64.decodeBase64(privateKeyStr.getBytes(DEFAULT_CHARSET)));
		return keyFactory.generatePrivate(keySpec);
	}
	
	/**
	 * 
	 * 根据数字签名和公钥验证签名是否正确
	 * 
	 * @Title: verifySign
	 * 
	 * @param sign
	 *            数字签名
	 * @param publicKey
	 *            公钥
	 * @param data
	 *            待校验数据
	 * @return
	 * @throws Exception
	 * @author: yanhuajian 2013-9-6上午10:37:55
	 */
	public static boolean verifySign(String sign, String publicKey,
			Map<String, Object> data) throws Exception {
		// 将map转换为url参数形式
		String stringToSign = map2String(data);
		// 将参数字符串转换成二进制
		byte[] bytesToSign = stringToSign.getBytes(DEFAULT_CHARSET);
		// 将数字签名符串转换成二进制
		byte[] signBytes = Base64.decodeBase64(sign.getBytes(DEFAULT_CHARSET));

		// 初始化DSA签名工具
		Signature sg = Signature.getInstance(DEFAULT_SIGN_METHOD);
		// 初始化DSA私钥
		sg.initVerify(getPublicKey(publicKey));
		sg.update(bytesToSign);
		// 验证签名
		boolean status = sg.verify(signBytes);

		return status;
	}
	
	/**
	 * 通过公钥字符串初始化DSA的公钥
	 * 
	 * @return
	 * @throws Exception
	 */
	private static PublicKey getPublicKey(String publicKeyStr) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(DEFAULT_SIGN_METHOD);
		EncodedKeySpec keySpec = new X509EncodedKeySpec(
				Base64.decodeBase64(publicKeyStr.getBytes(DEFAULT_CHARSET)));
		return keyFactory.generatePublic(keySpec);
	}
	
	/**
	 * 
	 * 将map转换为url格式字符串
	 * 
	 * @Title: map2String
	 * @param map
	 * @return
	 * @author: yanhuajian 2013-7-21下午7:25:08
	 */
	public static String map2String(Map<String, Object> map) {
		if (null == map || map.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue());
		}

		return sb.toString();
	}
	
	public String phoneToMid(String phone) {
		String midStr = "";
		Map<String, String> map = new HashMap<String, String>();
		map.put("0", "R");
		map.put("1", "I");
		map.put("2", "Z");
		map.put("3", "B");
		map.put("4", "H");
		map.put("5", "G");
		map.put("6", "E");
		map.put("7", "C");
		map.put("8", "F");
		map.put("9", "O");
		
		StringBuffer mid = new StringBuffer();
		
		for (int i = 0; i < phone.length(); i++) {
			mid.append(map.get(String.valueOf(phone.charAt(i))));
		}
		
		String tmp = mid.toString().substring(0,5);
		
		midStr = tmp + "KAF" + mid.toString().substring(5);
		
		return midStr;
	}
	
}
