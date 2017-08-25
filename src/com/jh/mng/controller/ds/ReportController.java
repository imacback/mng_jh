package com.jh.mng.controller.ds;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.RequestUtil;


/**
 * 代收业务处理类
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class ReportController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(ReportController.class);

	/**
	 * 科大讯飞RDO下订单请求
	 * @param request
	 * @param response
	 * @return
	 * @throws DocumentException 
	 */
	@RequestMapping("/kdSubmitOrder.do")
	public ModelAndView kdSubmitOrder(HttpServletRequest request, HttpServletResponse response){
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", -1);
			
			String requestStr = request.getQueryString();
			
			logger.info("new kd order: " + requestStr);
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String imsi = RequestUtil.GetParamString(request, "imei", null);
			String caller = RequestUtil.GetParamString(request, "caller", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String cm = Config.get().get("kdCm");
			
			String orderid = String.valueOf(System.currentTimeMillis());
			String requesttype = "2";
			String clientip = request.getRemoteAddr();
			String bizType = "0";
			String appid = Config.get().get("appid");
			String reqtime = DateTool.getCurrentDate("yyyyMMddHHmmss");
			
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("appid", appid);
			paraMap.put("feecode", feecode);
			paraMap.put("cm", cm);
			paraMap.put("reqtime", reqtime);
			paraMap.put("orderid", orderid);
			paraMap.put("requesttype", requesttype);
			paraMap.put("imsi", imsi);
			paraMap.put("imei", imei);
			paraMap.put("caller", caller);
			paraMap.put("clientip", clientip);
			paraMap.put("bizType", bizType);
			paraMap.put("mervar", cpparam);
			
			List<String> list = new ArrayList<String>();
			
			for (String key : paraMap.keySet()) {
				list.add(key);
			}
			
			Collections.sort(list);
			
			StringBuffer signStr = new StringBuffer();
			
			for (int i = 0; i < list.size(); i++) {
				
				if (i != list.size() - 1) {
					signStr.append(list.get(i));
					signStr.append("=");
					signStr.append(paraMap.get(list.get(i)));
					signStr.append("&");
				} else {
					signStr.append(list.get(i));
					signStr.append("=");
					signStr.append(paraMap.get(list.get(i)));
				}
			}
			
			logger.info("need sign str: " + signStr.toString());
			
			String sign = DigestUtils.md5Hex((signStr.toString() + Config.get().get("kdKey")).getBytes("UTF-8"));
			
			signStr.append("&sign=");
			signStr.append(sign);
			
//			StrIntBag bag = HttpClientUtil.execute(Config.get().get("kdorderurl") + "?" + signStr.toString() , null);
			StrIntBag bag = HttpClientUtil.execute(Config.get().get("kdorderurl"), signStr.toString());
			
			logger.info("bag resp is : " + bag);
			
			if (bag != null && bag._str != null) {
				Document doc = DocumentHelper.parseText(bag._str);
				Element root = doc.getRootElement();
				
				String resultCode = root.elementText("ResultCode");
				
				if (resultCode != null && resultCode.equals("000000")) {
					String reqUrl = root.elementText("ReqUrl");
					
					//计费请求消息接口
					StrIntBag bag_fee = HttpClientUtil.executeGet(reqUrl);
					
					logger.info("bag_fee resp : " + bag_fee);
					
					if (bag_fee != null && bag_fee._str != null) {
						Document doc_fee = DocumentHelper.parseText(bag_fee._str);
						Element root_fee = doc_fee.getRootElement();
						
						String resultCode_fee = root_fee.elementText("ResultCode");
						
						if (resultCode_fee != null && resultCode_fee.equals("200")) {
							
							String verifType = root_fee.element("Order").elementText("VerifType");
							
							if ("0".equals(verifType)) { //短信验证
								String getSMSVerifyCodeUrl = root_fee.element("Order").element("Submit0").elementText("GetSMSVerifyCodeUrl");
								
								if (StringUtils.isNotEmpty(getSMSVerifyCodeUrl)) {
									getSMSVerifyCodeUrl += "&msisdn=" + caller;
									
									//获取短信验证码
									StrIntBag verifyCodeBag = HttpClientUtil.executeGet(getSMSVerifyCodeUrl);
									
									logger.info("verifyCodeBag resp : " + verifyCodeBag);
									
									if (verifyCodeBag != null && verifyCodeBag._str != null) {
										Document document = DocumentHelper.parseText(verifyCodeBag._str);
										Element root_code = document.getRootElement();
										
										String resultCode_code = root_code.elementText("ResultCode");
										
										if (resultCode_code != null && "200".equals(resultCode_code)) { //成功
											String ButtonTag = root_code.element("Order").element("Submit0").elementText("ButtonTag");
											jObject.put("status", 0);
											jObject.put("confirmUrl", ButtonTag);
										}
									}
								}
							}
						}
					}
				}
			}
			
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("kdSubmitOrder error : " + e.getMessage());
		}
		return null;
	}
	
	public ModelAndView kdVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		
		try {
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("kdVerifyCode error : " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * 接收数据同步
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/kdReport.do")
	public ModelAndView kdReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter pw = response.getWriter();
		
		JSONObject json = new JSONObject();
		json.put("retcode", "000000");
		json.put("desc", "ok");
		
		String requestStr = "";

		try
		{
			InputStream in = request.getInputStream();
			byte b[]=new byte[10000];
			in.read(b,0,10000);
			requestStr = new String(b,"UTF-8");
			in.close();
			
		}catch(Exception ee)
		{
			json.put("retcode", "9999");
			json.put("desc", "failed");
			return null;
		}
		
		logger.info("kd report str: " + requestStr);
		
		if (requestStr != null && !"".equals(requestStr)) {
			JSONObject jObject = JSONObject.parseObject(requestStr);
			String appid = jObject.getString("appid");
			String feecode = jObject.getString("feecode");
			String cm = jObject.getString("cm");
			String biztype = jObject.getString("biztype");
			String reqtime = jObject.getString("reqtime");
			String orderid = jObject.getString("orderid");
			String caller = jObject.getString("caller");
			String paytime = jObject.getString("paytime");
			String resultcode = jObject.getString("resultcode");
			String mervar = jObject.getString("mervar");
			String fee = jObject.getString("fee");
			String sign_req = jObject.getString("sign");
			
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("appid", appid);
			paraMap.put("feecode", feecode);
			paraMap.put("cm", cm);
			paraMap.put("biztype", biztype);
			paraMap.put("reqtime", reqtime);
			paraMap.put("orderid", orderid);
			paraMap.put("caller", caller);
			paraMap.put("paytime", paytime);
			paraMap.put("resultcode", resultcode);
			paraMap.put("mervar", mervar);
			paraMap.put("fee", fee);
			
			List<String> list = new ArrayList<String>();
			
			for (String key : paraMap.keySet()) {
				list.add(key);
			}
			
			Collections.sort(list);
			
			StringBuffer signStr = new StringBuffer();
			
			for (int i = 0; i < list.size(); i++) {
				
				if (i != list.size() - 1) {
					signStr.append(list.get(i));
					signStr.append("=");
					signStr.append(paraMap.get(list.get(i)));
					signStr.append("&");
				} else {
					signStr.append(list.get(i));
					signStr.append("=");
					signStr.append(paraMap.get(list.get(i)));
				}
			}
			
			logger.info("need sign str: " + signStr.toString());
			
			String sign = DigestUtils.md5Hex((signStr.toString() + Config.get().get("kdKey")).getBytes("UTF-8"));
			
			if (sign.equals(sign_req)) {
				String userId = "";
				String cpserviceid = "none";
				String consumecode = feecode;
				String cpparam = mervar;
				String hret = "none";
				String status = "-1";
				String versionid = "none";
				String transido = orderid;
				int chnl_id = -1;
				int dec_flag = 0;
				String province = "";
				String packageid = "1000";
				String phoneno = "";
				
				userId = caller;
				phoneno = caller;
				String channelId = cpparam.substring(0, 2);
				province = cpparam.substring(2, 4);
				
				province = GetState(province);
				
				if (userId == null || "".equals(userId)) {
					userId = "none";
				}
				
				if (phoneno == null || "".equals(phoneno)) {
					phoneno = "none";
				}
				
				if ("1".equals(resultcode)) {
					hret = "0";
					status = "1101";
				}
				
				
				int fee_local = -1;
				int over_arpu = 0;
				int sync_cpflag = 0;
				int sync_chnlflag = 0;
				
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(feecode);
				
				if (consumeInfo != null) {
					fee_local = consumeInfo.getFee();
					
					List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
					
					boolean flag = false;
					
					if (chnlResourceList != null && chnlResourceList.size() > 0) {
						for (ChnlResource chnlResource : chnlResourceList) {
							
							if (chnl_id == chnlResource.getChnlId().intValue()) {
								List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), province);
								
								if (maskList != null && maskList.size() > 0) {
									chnl_id = Integer.parseInt(channelId);
									flag = true;
									break;
								}
							}
						}
					}
					
					if (!flag) {
						chnl_id = -1;
					}
				} else {
					chnl_id = -1;
				}
				//超限验证
				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_HY", 1000000), Config.get().getInt("MAX_Y_FEE_HY", 1000000), today, userId, fee_local);
				
				//扣量验证
				
				if (over_arpu == 0) {
					if (chnl_id > 0) {
						dec_flag = shouldKouliangByState(today, chnl_id, feecode, province, packageid);
						
						if (dec_flag == 1) {
							sync_chnlflag = 200;
						} else {
							sync_chnlflag = 0;
						}
					}
					
				} else {
					sync_chnlflag = 300;
					dec_flag = 1;
				}
				
				Long id = orderService.getNextOrderId();
				
				ReceiveSms receiveSms = new ReceiveSms();
				receiveSms.setId(id);
				receiveSms.setUserid(userId);
				receiveSms.setCpserviceid(cpserviceid);
				receiveSms.setConsumecode(consumecode);
				receiveSms.setCpparam(cpparam);
				receiveSms.setHret(hret);
				receiveSms.setStatus(status);
				receiveSms.setTransido(transido);
				receiveSms.setVersionid(versionid);
				receiveSms.setPackageid(packageid);
				receiveSms.setSyncflag(sync_cpflag);
				receiveSms.setSyncchnl(sync_chnlflag);
				receiveSms.setChnl_id(chnl_id);
				receiveSms.setFee(fee_local);
				receiveSms.setDec_flag(dec_flag);
				receiveSms.setState(province);
				receiveSms.setPhoneno(phoneno);
				
				orderService.createReceiveSms(receiveSms);
				
				
			} else {
				json.put("retcode", "100003");
				json.put("desc", "failed");
			}
		} else {
			json.put("retcode", "100001");
			json.put("desc", "failed");
		}
		
		pw.print(json.toString());
		
		return null;
	}
	
//	public int checkArpu(int MAX_R_FEE,int MAX_Y_FEE, String today,String mdn,int fee) {
//		
//		int ret = 0;
//		
//		int r_fee = 0;
//	    int y_fee = 0;
//		
//		try {
//			UserLimit userLimit = orderService.getUserLimitByMdn(mdn);
//			
//			if (userLimit == null) {
//				r_fee = 0;
//				y_fee = 0;
//				
//				r_fee = orderService.sumDayFee(today, mdn).intValue();
//				y_fee = orderService.sumMonthFee(today, mdn).intValue();
//				
//				r_fee = r_fee + fee;
//				y_fee = y_fee + fee;
//				
//				userLimit.setRq(Long.parseLong(today));
//				userLimit.setMdn(mdn);
//				userLimit.setR_fee(new Long(r_fee));
//				userLimit.setY_fee(new Long(y_fee));
//				
//				orderService.createUserLimit(userLimit);
//				
//			} else {
//				int hist_rq = userLimit.getRq().intValue();
//				int hist_r_fee = 0;
//				int hist_y_fee = 0;
//				
//				if (hist_rq == Integer.parseInt(today)) {
//					hist_r_fee = userLimit.getR_fee().intValue();
//				} else {
//					hist_r_fee = 0;
//				}
//				
//				if (hist_rq / 100 == Integer.parseInt(today) / 100) {
//					hist_y_fee = userLimit.getY_fee().intValue();
//				} else {
//					hist_y_fee = 0;
//				}
//				
//				hist_r_fee = hist_r_fee + fee;
//				hist_y_fee = hist_y_fee + fee;
//				
//				if ( (hist_r_fee >= MAX_R_FEE) || (hist_y_fee >= MAX_Y_FEE) )
//				{
//				  ret = 1;
//				}
//				
//				userLimit.setRq(Long.parseLong(today));
//				userLimit.setR_fee(new Long(hist_r_fee));
//				userLimit.setY_fee(new Long(hist_y_fee));
//				
//				orderService.updateUserLimit(userLimit);
//				
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("checkArpu exception : " + e.getMessage());
//		}
//		
//		return ret;
//	}
	
	String states[] = {
			"北京", "天津","上海","重庆","河北",
			"山西","河南","辽宁","吉林","黑龙江",
			"内蒙古","江苏","山东","安徽","浙江",
			"福建","湖北","湖南","广东","广西",
			"江西","四川","贵州","云南","西藏",
			"海南","陕西","甘肃","宁夏","青海","新疆"
			};

	public String GetState(String idx) {
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
	
//	synchronized int shouldKouliangByState(String today, int chnl_id, String consumecode, String state, String packageid) {
//		int id = 0;
//
//	    int baseline = 0;
//	    int curcnt = 0;
//	    int percent = 0;
//	    String workday = "";
//
//	    int skipcnt = 0;
//	    int dec_flag = 0;
//		
//	    try {
//	    	ChnlDecPercent chnlDecPercent = orderService.queryDecPercent(chnl_id, consumecode, packageid, today);
//	    	
//	    	if (chnlDecPercent != null) {
//	    		id = chnlDecPercent.getId().intValue();
//	    		baseline = chnlDecPercent.getBaseline();
//	    		percent = chnlDecPercent.getPercent();
//	    		workday = chnlDecPercent.getCurday();
//	    		
//	    		if (!today.equals(workday)) {
//	    			orderService.delPercent2(chnl_id, consumecode, packageid);
//	    			orderService.createPercent2(chnl_id, consumecode, packageid, today, percent, baseline);
//	    			orderService.updatePercent(today, new Long(id));
//	    			
//	    			curcnt = 1;
//	    		} else {
//	    			chnlDecPercent = orderService.queryDecPercent2(chnl_id, consumecode, packageid, state);
//	    			
//	    			if (chnlDecPercent != null) {
//	    				curcnt = chnlDecPercent.getCurcnt() + 1;
//	    			} else {
//	    				curcnt = 1;
//	    			}
//	    		}
//	    		
//	    		if (curcnt > baseline) 
//	    		{
//	    		  if ( percent > 0 )
//	    		  {
//	    			  skipcnt = 100 / percent;
//	    			  if ( ( (curcnt-100) % skipcnt) == 0) {
//	    				dec_flag = 1;
//	    			  }
//	    		  }
//	            }
//	    		
//	    		orderService.updatePercent2(chnl_id, consumecode, packageid, state);
//	    	}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("ShouldKouliangByState error : " + e.getMessage());
//		}
//		
//		return dec_flag;
//	}
	
	/**
	 * 天津爱阅读
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/iydOrder.do")
	public ModelAndView iydOrder(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject respJson = new JSONObject();
			respJson.put("status", "-1");
			respJson.put("msg", "失败");
			
			String requestStr = request.getQueryString();
			
			logger.info("天津爱阅读请求下单参数为： " + requestStr);
			
			String merc_id = Config.get().get("merc_id");
			String app_id = Config.get().get("app_id");
			String app_level = "3";
			String market_id = RequestUtil.GetParamString(request, "market_id", null);
			String pay_code = RequestUtil.GetParamString(request, "pay_code", null);
			String user_level = "0";
			String phone = RequestUtil.GetParamString(request, "phone", null);
			int amount = RequestUtil.GetParamInteger(request, "amount"); 
			long time = new Date().getTime() / 1000; 
			int apn_type = RequestUtil.GetParamInteger(request, "apn_type", 6);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String imsi = RequestUtil.GetParamString(request, "imsi", null);
			String ip = request.getRemoteAddr();
//			String ret_url = RequestUtil.GetParamString(request, "ret_url", null);
			String noti_url = Config.get().get("noti_url");
			String site_type = "3";
			String scheme = "1";
			String ver = "2.0";
			
			Long id = orderService.getNextOrderId();
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("merc_id", merc_id);
			map.put("app_orderid", String.valueOf(id));
			map.put("app_id", app_id);
			map.put("app_level", app_level);
			map.put("market_id", market_id);
			map.put("pay_code", pay_code);
			map.put("user_level", user_level);
			map.put("phone", phone);
			map.put("amount", String.valueOf(amount));
			map.put("time", String.valueOf(time));
			map.put("apn_type", String.valueOf(apn_type));
			map.put("corp_type", "1");
			map.put("imei", imei);
			map.put("imsi", imsi);
			map.put("ip", ip);
//			map.put("ret_url", ret_url);
			map.put("noti_url", noti_url);
			map.put("site_type", site_type);
			map.put("scheme", scheme);
			map.put("ver", ver);
			map.put("merc_key", Config.get().get("merc_key"));
			
			String needSign = buildSignStr(map);
			
			String sign = DigestUtils.md5Hex(needSign.getBytes("utf-8"));
			
			map.put("sign", sign);
			
			logger.info("iyd order request params: " + map.toString());
			
			String str = HttpTookit.doPost(Config.get().get("merc_url"), map);
			
			logger.info("bag resp is : " + str);
			
			if (str != null) {
				String orderid = "";
				String status_local = "-1";
				
				JSONObject object = JSONObject.parseObject(str);
				
				String status = object.getString("status");
				String msg = object.getString("msg");
				
				respJson.put("status", status);
				respJson.put("msg", msg);
				
				if ("0".equals(status)) { //成功
//					respJson.put("status", "1");
					JSONObject resObject = JSONObject.parseObject(object.getString("res"));
					orderid = resObject.getString("orderid");
					respJson.put("orderid", id);
				} else { //失败
					status_local = status;
				}
				
				//生成本地订单
				String userid = phone;
				String cpserviceid = "none";
				String consumecode = pay_code;
				String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
				String hret = "99";
				String versionid = "none";
				
				String transido = "";
				
				if (orderid != null && !"".equals(orderid)) {
					transido = orderid;
				} else {
					transido = String.valueOf(System.currentTimeMillis());
				}
				
				String phoneno = phone;
				String packageid = "1000";
				String chnl_id = market_id;
//				String province = Methods.request(phone);
				
				String province = getStateByMobile(phoneno);
				province = GetState(province);
				
				if ("未知".equals(province)) {
					province = cpparam.substring(0, 2);
					province = GetState(province);
				} 
				
				ReceiveSms receiveSms = new ReceiveSms();
				receiveSms.setId(id);
				receiveSms.setUserid(userid);
				receiveSms.setCpserviceid(cpserviceid);
				receiveSms.setConsumecode(consumecode);
				receiveSms.setCpparam(cpparam);
				receiveSms.setHret(hret);
				receiveSms.setStatus(status_local);
				receiveSms.setTransido(transido);
				receiveSms.setVersionid(versionid);
				receiveSms.setPackageid(packageid);
				receiveSms.setSyncflag(0);
				receiveSms.setSyncchnl(0);
				receiveSms.setChnl_id(Integer.parseInt(chnl_id));
				receiveSms.setFee(amount);
				receiveSms.setDec_flag(1);
				receiveSms.setState(province);
				receiveSms.setPhoneno(phoneno);
				
				orderService.createReceiveSms(receiveSms);
			}
			
			pw.print(respJson.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("iydOrder error : " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 爱阅读短信验证
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/iydSmsConfirm.do")
	public ModelAndView iydSmsConfirm(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject respJson = new JSONObject();
			respJson.put("status", "-1");
			respJson.put("msg", "失败");
			
			String requestStr = request.getQueryString();
			
			logger.info("天津爱阅读短信验证参数为： " + requestStr);
			
			String merc_id = Config.get().get("merc_id");
			String app_id = Config.get().get("app_id");
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "verify_code", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			
			if (receiveSms != null) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("merc_id", merc_id);
				map.put("app_id", app_id);
				map.put("orderid", receiveSms.getTransido());
				map.put("verify_code", verify_code);
				map.put("ver", "2.0");
				map.put("merc_key", Config.get().get("merc_key"));
				
				String needSign = buildSignStr(map);
				
				String sign = DigestUtils.md5Hex(needSign.getBytes("utf-8"));
				
				map.put("sign", sign);
				
				logger.info("iyd smsconfirm request params: " + map.toString());
				
				String str = HttpTookit.doPost(Config.get().get("merc_url_confirm"), map);
				
				logger.info("smsconfirm resp is : " + str);
				

				JSONObject object = JSONObject.parseObject(str);
				
				String status = object.getString("status");
				String msg = object.getString("msg");
				
				respJson.put("msg", msg);
				
				String code = "-1";
				
				if ("0".equals(status)) { //成功
					JSONObject resObject = JSONObject.parseObject(object.getString("res"));
					code = resObject.getString("code");
					respJson.put("status", code);
				} else { //失败
				}
			}
			pw.print(respJson.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("iydSmsConfirm error : " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * 天津爱阅读
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/iydReport.do")
	public ModelAndView iydReport(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			if (checkSign(request, Config.get().get("merc_key"))) {// 验证成功
				String app_orderid = RequestUtil.GetParamString(request, "app_orderid", null);
				String status = RequestUtil.GetParamString(request, "status", null);
				
//				if (status != null && "1".equals(status)) { //订购成功
					ReceiveSms receiveSms = orderService.queryReceiveSmsById(app_orderid);
					
					if (receiveSms != null) {
						int fee = -1;
						int over_arpu = 0;
						String today = DateTool.getCurrentDate("yyyyMMdd");
						
						if (status != null && "1".equals(status)) {
							receiveSms.setStatus("1101");
							receiveSms.setHret("0");
						} else {
							receiveSms.setStatus(status);
							receiveSms.setHret("-1");
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
						over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_IYD", 1000000), Config.get().getInt("MAX_Y_FEE_IYD", 1000000), today, receiveSms.getUserid(), fee);
						
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
						
						pw.print("success");
					}
//				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("iydReport error : " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 恒华订单请求
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/hhOrder.do")
//	public ModelAndView hhOrder(HttpServletRequest request, HttpServletResponse response) {
//		JSONObject jObject = new JSONObject();
//		jObject.put("Code", "9999");
//		PrintWriter pw = null;
//		
//		try {
//			pw = response.getWriter();
//			
//			Long id = orderService.getNextOrderId();
//			
//			String mcpid = request.getParameter("mcpid");
//			String feeCode = request.getParameter("feeCode");
//			String cm = request.getParameter("cm");
//			String channelid = request.getParameter("channelid");
//			String mobile = request.getParameter("mobile");
//			String outorderid = request.getParameter("outorderid");
//			
//			String url = Config.get().get("hhOrderUrl");
//			String ip = request.getRemoteAddr();
//			
//			String requestStr = "mcpid=" + mcpid ;
//			requestStr += "&feeCode=" + feeCode;
//			requestStr += "&cm=" + cm;
//			requestStr += "&channelid=" + channelid;
//			requestStr += "&mobile=" + mobile;
//			requestStr += "&outorderid=" + id;
//			requestStr += "&clientip=" + ip;
//			
//			
//			StrIntBag str = HttpClientUtil.executeGet(url + "?" + requestStr);
//			
//			logger.info("hhOrderurl : " + url + ", resquestStr: " + requestStr + " , resp: " + str);
//			
//			if (str != null && StringUtils.isNotEmpty(str._str)) {
//				JSONObject respObject = JSONObject.parseObject(str._str);
//				jObject.put("Code", respObject.get("Code"));
//				jObject.put("Message", respObject.get("Message"));
//			}
//			
//			//生成本地订单
//			String userid = mobile;
//			String cpserviceid = "none";
//			String consumecode = feeCode;
//			String hret = "none";
//			String versionid = "none";
//			
//			String transido = "";
//			
//			transido = String.valueOf(System.currentTimeMillis());
//			
//			String phoneno = mobile;
//			String packageid = "1000";
//			String cpparam = outorderid;
//			String chnl_id = cpparam.substring(0, 2);
////			String province = cpparam.substring(2, 4);
////			province = GetState(province);
//			String province = Methods.request(mobile);
//			
//			ReceiveSms receiveSms = new ReceiveSms();
//			receiveSms.setId(id);
//			receiveSms.setUserid(userid);
//			receiveSms.setCpserviceid(cpserviceid);
//			receiveSms.setConsumecode(consumecode);
//			receiveSms.setCpparam(cpparam);
//			receiveSms.setHret(hret);
//			
//			if (jObject.get("Code").toString().equals("0")) {
//				receiveSms.setStatus("-1");
//			} else {
//				receiveSms.setStatus(jObject.get("Code").toString());
//			}
//			receiveSms.setTransido(transido);
//			receiveSms.setVersionid(versionid);
//			receiveSms.setPackageid(packageid);
//			receiveSms.setSyncflag(0);
//			receiveSms.setSyncchnl(0);
//			receiveSms.setChnl_id(Integer.parseInt(chnl_id));
//			receiveSms.setFee(0);
//			receiveSms.setDec_flag(1);
//			receiveSms.setState(province);
//			receiveSms.setPhoneno(phoneno);
//			
//			orderService.createReceiveSms(receiveSms);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("hhOrder error : " + e.getMessage());
//		}
//		pw.print(jObject.toString());
//		return null;
//	}
	
	/**
	 * 恒华短信确认
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/hhConfirm.do")
//	public ModelAndView hhConfirm(HttpServletRequest request, HttpServletResponse response) {
//		JSONObject jObject = new JSONObject();
//		jObject.put("Code", "9999");
//		PrintWriter pw = null;
//		
//		try {
//			pw = response.getWriter();
//			String mcpid = request.getParameter("mcpid");
//			String feeCode = request.getParameter("feeCode");
//			String mobile = request.getParameter("mobile");
//			String verifycode = request.getParameter("verifycode");
//			
//			String url = Config.get().get("hhConfirmUrl");
//			
//			String requestStr = "mcpid=" + mcpid ;
//			requestStr += "&feeCode=" + feeCode;
//			requestStr += "&mobile=" + mobile;
//			requestStr += "&verifycode=" + verifycode;
//			
//			StrIntBag str = HttpClientUtil.executeGet(url + "?" + requestStr);
//			
//			logger.info("hhConfirmUrl : " + url + ", resquestStr: " + requestStr + " , resp: " + str);
//			
//			if (str != null && StringUtils.isNotEmpty(str._str)) {
//				JSONObject respObject = JSONObject.parseObject(str._str);
//				jObject.put("Code", respObject.get("Code"));
//				jObject.put("Message", respObject.get("Message"));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("hhOrder error : " + e.getMessage());
//		}
//		pw.print(jObject.toString());
//		return null;
//	}
	
//	/**
//	 * 浙江恒华网络科技有限公司RDO阅读数据同步
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/hxRdoReport.do")
//	public ModelAndView hxRdoReport(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			PrintWriter pw = response.getWriter();
//			
//			String requestStr = request.getQueryString();
//			
//			logger.info("恒华RDO数据同步参数为： " + requestStr);
//			
////			String feecode = request.getParameter("feecode");
////			String mobile = request.getParameter("mobile");
//			String outorderid = request.getParameter("outorderid");
////			String orderno = request.getParameter("orderno");
//			String resultcode = request.getParameter("resultcode");
//			String hret = "-1";
//			String status = "-1";
//			
//			ReceiveSms receiveSms = orderService.queryReceiveSmsById(outorderid);
//			
//			if (receiveSms != null) {
//				int fee = -1;
//				int over_arpu = 0;
//				String today = DateTool.getCurrentDate("yyyyMMdd");
//				
//				status = resultcode;
//				
//				if (resultcode != null && resultcode.equals("200")) {
//					hret = "0";
//					status = "1101";
//				}
//				
//				receiveSms.setHret(hret);
//				receiveSms.setStatus(status);
//				
//				ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(receiveSms.getConsumecode());
//				
//				if (consumeInfo != null) {
//					receiveSms.setFee(consumeInfo.getFee());
//					
//					List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
//					
//					boolean flag = false;
//					
//					if (chnlResourceList != null && chnlResourceList.size() > 0) {
//						for (ChnlResource chnlResource : chnlResourceList) {
//							
//							if (receiveSms.getChnl_id() == chnlResource.getChnlId().intValue()) {
//								List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), receiveSms.getState());
//								
//								if (maskList != null && maskList.size() > 0) {
//									receiveSms.setChnl_id(chnlResource.getChnlId().intValue());
//									flag = true;
//									break;
//								}
//							}
//						}
//					}
//					
//					if (!flag) {
//						receiveSms.setChnl_id(-1);
//					}
//				} else {
//					receiveSms.setChnl_id(-1);
//				}
//				//超限验证
//				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_HY", 1000000), Config.get().getInt("MAX_Y_FEE_HY", 1000000), today, receiveSms.getUserid(), fee);
//				
//				//扣量验证
//				int dec_flag = 0;
//				
//				if (over_arpu == 0) {
//					if (receiveSms.getChnl_id() > 0) {
//						dec_flag = shouldKouliangByState(today, receiveSms.getChnl_id(), receiveSms.getConsumecode(), receiveSms.getState(), receiveSms.getPackageid());
//						
//						receiveSms.setDec_flag(dec_flag);
//						
//						if (dec_flag == 1) {
//							receiveSms.setSyncchnl(200);
//						} else {
//							receiveSms.setSyncchnl(0);
//						}
//					}
//					
//				} else {
//					receiveSms.setSyncchnl(300);
//					receiveSms.setDec_flag(1);
//				}
//				
//				orderService.updateReceiveSms(receiveSms);
//				
//				pw.print("OK");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("hxRdoReport error : " + e.getMessage());
//		}
//		return null;
//	}
	
	
	/**
	 * 浙江恒华网络科技有限公司RDO阅读数据同步
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/hxRdoReport.do")
//	public ModelAndView hxRdoReport(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			PrintWriter pw = response.getWriter();
//			
//			String requestStr = request.getQueryString();
//			
//			logger.info("恒华RDO数据同步参数为： " + requestStr);
//			
//			String feecode = request.getParameter("feecode");
//			String mobile = request.getParameter("mobile");
//			String outorderid = request.getParameter("outorderid");
//			String orderno = request.getParameter("orderno");
//			String resultcode = request.getParameter("resultcode");
//			
//			String userid = mobile;
//			String cpserviceid = "none";
//			String consumecode = feecode;
//			String cpparam = outorderid;
//			String hret = "-1";
//			String status = "-1";
//			String versionid = "none";
//			
//			String transido = orderno;
//			
//			List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByTransido(transido);
//			
//			if (receiveSmsList != null && receiveSmsList.size() > 0) {
//				pw.print("OK");
//				return null;
//			}
//			
//			status = resultcode;
//			
//			if (resultcode != null && resultcode.equals("200")) {
//				hret = "0";
//				status = "1101";
//			}
//			
//			if (StringUtils.isEmpty(transido)) {
//				transido = String.valueOf(System.currentTimeMillis());
//			}
//			
//			String phoneno = mobile;
//			String packageid = "1000";
//			int chnl_id = Integer.parseInt(cpparam.substring(0, 2));
//			
//			String province = Methods.request(mobile);
//			
//			if (province.equals("-") || "未知".equals(province)) {
//				province = cpparam.substring(2, 4);
//				province = GetState(province);
//			}
//			
//			int fee = -1;
//			int over_arpu = 0;
//			int sync_cpflag = 0;
//			int sync_chnlflag = 0;
//			
//			String today = DateTool.getCurrentDate("yyyyMMdd");
//			
//			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(feecode);
//			
//			if (consumeInfo != null) {
//				fee = consumeInfo.getFee();
//				
//				List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
//				
//				boolean flag = false;
//				
//				if (chnlResourceList != null && chnlResourceList.size() > 0) {
//					for (ChnlResource chnlResource : chnlResourceList) {
//						
//						if (chnl_id == chnlResource.getChnlId().intValue()) {
//							List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), province);
//							
//							if (maskList != null && maskList.size() > 0) {
//								flag = true;
//								break;
//							}
//						}
//					}
//				}
//				
//				if (!flag) {
//					chnl_id = -1;
//				}
//			} else {
//				chnl_id = -1;
//			}
//			
//			if (status.equals("1101")) {
//				//超限验证
//				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_HY", 1000000), Config.get().getInt("MAX_Y_FEE_HY", 1000000), today, userid, fee);
//			}
//			
//			
//			//扣量验证
//			int dec_flag = 0;
//			
//			if (over_arpu == 0) {
//				if (chnl_id > 0) {
//					
//					if (status.equals("1101")) {
//						dec_flag = shouldKouliangByState(today, chnl_id, feecode, province, packageid);
//					}
//					
//					if (dec_flag == 1) {
//						sync_chnlflag = 200;
//					} else {
//						sync_chnlflag = 0;
//					}
//				}
//				
//			} else {
//				sync_chnlflag = 300;
//				dec_flag = 1;
//			}
//			
//			Long id = orderService.getNextOrderId();
//			
//			ReceiveSms receiveSms = new ReceiveSms();
//			receiveSms.setId(id);
//			receiveSms.setUserid(userid);
//			receiveSms.setCpserviceid(cpserviceid);
//			receiveSms.setConsumecode(consumecode);
//			receiveSms.setCpparam(cpparam);
//			receiveSms.setHret(hret);
//			receiveSms.setStatus(status);
//			receiveSms.setTransido(transido);
//			receiveSms.setVersionid(versionid);
//			receiveSms.setPackageid(packageid);
//			receiveSms.setSyncflag(sync_cpflag);
//			receiveSms.setSyncchnl(sync_chnlflag);
//			receiveSms.setChnl_id(chnl_id);
//			receiveSms.setFee(fee);
//			receiveSms.setDec_flag(dec_flag);
//			receiveSms.setState(province);
//			receiveSms.setPhoneno(phoneno);
//			
//			orderService.createReceiveSms(receiveSms);
//			
//			pw.print("OK");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("hxRdoReport error : " + e.getMessage());
//		}
//		return null;
//	}
	
	private String buildSignStr(Map<String, String> map) {
		List<String> list = new ArrayList<String>();
		
		for (String key : map.keySet()) {
			list.add(key);
		}
		
		Collections.sort(list);
		
		StringBuffer signStr = new StringBuffer();
		
		for (int i = 0; i < list.size(); i++) {
			
			if (i != list.size() - 1) {
				signStr.append(list.get(i));
				signStr.append("=");
				signStr.append(map.get(list.get(i)));
				signStr.append("&");
			} else {
				signStr.append(list.get(i));
				signStr.append("=");
				signStr.append(map.get(list.get(i)));
			}
		}
		
		return signStr.toString();
	}
	
    public static boolean checkSign(HttpServletRequest request, String md5key) throws Exception {
    	boolean result = false;
        if (StringUtils.isEmpty(md5key)) {
            throw new Exception("merc_key is empty");
        }

        SortedMap<String, String> sortedMap = getSignParam(request);

        logger.info("阅读数据同步参数为: " + sortedMap.toString());
        
        if (sortedMap != null) {
            //签名原串
            String signOriginalStr = buildSignParamStr(sortedMap, md5key);
            //传递过来的签名
            String beforeSign = getSignStr(sortedMap);
            //计算后的签名
            String afterSign = DigestUtils.md5Hex(signOriginalStr.getBytes("utf-8"));

            if (StringUtils.isEmpty(beforeSign) || StringUtils.isEmpty(signOriginalStr) ||
                    !beforeSign.equalsIgnoreCase(afterSign)) {
                throw new Exception("sign error");
            }
            
            if (beforeSign.equals(afterSign)) {
            	result = true;
            }
        }
        return result;
    }
    
    public static String buildSignParamStr(SortedMap<String, String> sortedMap, String md5key) {

        SortedMap<String, String> map = new TreeMap<String, String>();
        for (String pname : sortedMap.keySet()) {
            map.put(pname, sortedMap.get(pname));
        }
        map.put("merc_key", md5key);

        StringBuilder sb = new StringBuilder(500);
        for (String pname : map.keySet()) {
            if ("sign".equals(pname) || StringUtils.isEmpty(map.get(pname))) {
                continue;
            }
            sb.append(pname).append("=").append(map.get(pname)).append("&");
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        String signOriginalStr = sb.toString();
        return signOriginalStr;
    }
    
    @SuppressWarnings("unchecked")
	private static SortedMap<String, String> getSignParam(HttpServletRequest request) {

        SortedMap<String, String> sortedMap = null;
        Map<String, String[]> paraMap = request.getParameterMap();
        if (paraMap != null && paraMap.size() > 0) {
            sortedMap = new TreeMap<String, String>();
            for (String pname : paraMap.keySet()) {
                String pvalue = null;
                if (paraMap.get(pname) != null && paraMap.get(pname).length > 0) {
                    pvalue = paraMap.get(pname)[0];
                }
                if (StringUtils.isNotEmpty(pvalue)) {
                    sortedMap.put(pname, pvalue);
                }
            }
        }
        return sortedMap;
    }
    
    private static String getSignStr(SortedMap<String, String> sortedMap) {
        return sortedMap.containsKey("sign") ? sortedMap.get("sign") : null;
    }
}
