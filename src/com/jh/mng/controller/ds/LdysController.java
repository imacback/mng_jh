package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.common.Config;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.Mask;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.MD5Util;
import com.jh.mng.util.RequestUtil;

/**
 * 联动优势小额支付
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class LdysController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(LdysController.class);
	
	/**
	 * 下单接口
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/ldOrder.do")
	public ModelAndView ldOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		String times = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(times + "联动优势小额支付新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String amount = RequestUtil.GetParamString(request, "amount", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String phone = RequestUtil.GetParamString(request, "mobile", null);
			String ip = RequestUtil.GetParamString(request, "ip", null);
			
			String province = "00";
			
			if (StringUtils.isNotEmpty(phone)) {
				province = getStateByMobile(phone);
			} else {
				phone = "none";
				province = cpparam.substring(2,4);
			}
			province = GetState(province);
			
			Integer channelId = Integer.parseInt(cpparam.substring(0,2));

			ReceiveSms receiveSms = new ReceiveSms();
			
			Long id = orderService.getNextOrderId();
			
			String orderdate = DateTool.getCurrentDate("yyyyMMdd");
			
			StringBuffer signBuffer = new StringBuffer();
			signBuffer.append("merid=");
			signBuffer.append(Config.get().get("merid_ld"));
			signBuffer.append("&goodsid=");
			signBuffer.append(feecode);
			signBuffer.append("&goodsinfo=&mobileid=");
			signBuffer.append("&orderid=");
			signBuffer.append(id);
			signBuffer.append("&orderdate=");
			signBuffer.append(orderdate);
			signBuffer.append("&amount=");
			signBuffer.append(amount);
			signBuffer.append("&amttype=02");
			signBuffer.append("&banktype=3");
			signBuffer.append("&platType=9");
			signBuffer.append("&gateid=");
			signBuffer.append("&returl=www.hanjoys.com&merpriv=&expand=&channelid=&appid=&upversion=1.1");
			
			String sign = MD5Util.getMD5(signBuffer.toString() + Config.get().get("key_ld")).toLowerCase();// 加密后的密文
			
			String bag = "";
			
			String reqTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("merid", Config.get().get("merid_ld"));
			data.put("goodsid", feecode);
			data.put("clientip", ip);
			data.put("orderid", String.valueOf(id));
			data.put("orderdate", orderdate);
			data.put("amount", amount);
			data.put("amttype", "02");
			data.put("banktype", "3");
			data.put("platType", "9");
			data.put("returl", "www.hanjoys.com");
			data.put("upversion", "1.1");
			data.put("imei", imei);
			data.put("sign", sign);
			
			bag = HttpTookit.doPost(Config.get().get("orderurl_ldorder"), data);
			
			String respTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			logger.info( reqTimes + "ldys req : " + data.toString() + ", " + respTimes + "bag resp is : " + bag + ", 响应时间:" + (DateTool.StringToDate(respTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(reqTimes, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000);
			
			if (bag != null) {
				JSONObject resp = JSONObject.parseObject(bag);
				
				String result = resp.getString("retCode");
				String paytype = resp.getString("paytype");
				
				if ("0000".equals(result)) {
					if ("YZM".equals(paytype)) {
						StringBuffer sign_yzm = new StringBuffer();
						sign_yzm.append("merid=");
						sign_yzm.append(Config.get().get("merid_ld"));
						sign_yzm.append("&orderid=");
						sign_yzm.append(id);
						sign_yzm.append("&orderdate=");
						sign_yzm.append(orderdate);
						sign_yzm.append("&upversion=1.1");
						
						sign = MD5Util.getMD5(sign_yzm.toString() + Config.get().get("key_ld")).toLowerCase();// 加密后的密文
						
						Map<String, String> data_yzm = new HashMap<String, String>();
						data_yzm.put("merid", Config.get().get("merid_ld"));
						data_yzm.put("orderid", String.valueOf(id));
						data_yzm.put("orderdate", orderdate);
						data_yzm.put("upversion", "1.1");
						data_yzm.put("mobileid", phone);
						data_yzm.put("sign", sign);
						
						String bag_yzm = HttpTookit.doPost(Config.get().get("orderurl_ldgetverifycode"), data_yzm);
						
						logger.info("ldGetVerifyCode req : " + data_yzm.toString() + ", resp is : " + bag_yzm );
						
						if (bag_yzm != null) {
							JSONObject resp_yzm = JSONObject.parseObject(bag_yzm);
							
							String result_yzm = resp_yzm.getString("retCode");
							
							if ("0000".equals(result_yzm)) {
								jObject.put("orderid", id);
								jObject.put("status", STATUS_RESP_SUCCESS);
								jObject.put("paytype", "YZM");
							} else {
								receiveSms.setStatus(result);
							}
						}
					} else if ("SMS".equals(paytype)) {
						jObject.put("paytype", "SMS");
						String mo = resp.getString("mo");
						String called = resp.getString("called");
						String mo2 = resp.getString("mo2");
						String called2 = resp.getString("called2");
						jObject.put("smsmsg", mo);
						jObject.put("smsnum", called);
						jObject.put("smsmsg2", mo2);
						jObject.put("smsnum2", called2);
						jObject.put("orderid", id);
						jObject.put("status", STATUS_RESP_SUCCESS);
					}
				}
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			
			if (jObject.get("paytype") != null) {
				receiveSms.setHret(HRET_FIRST);
				receiveSms.setStatus(STATUS_FIRST);
			} else {
				receiveSms.setHret(HRET_FAILED);
				receiveSms.setStatus(STATUS_FAILED);
			}
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
			logger.error("ldOrder error : " + e.getMessage());
		}
		
		String endTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		logBuffer.append(", " + endTimes + " ,服务端响应为：" + jObject.toString() + ", 响应时间：" + (DateTool.StringToDate(endTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(times, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	/**
	 * 验证码确认
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/ldConfirmCode.do")
	public ModelAndView ldConfirmCode(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		JSONObject respJson = new JSONObject();
		respJson.put("status", STATUS_RESP_FAILED);
		
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("联动优势验证参数为： " + requestStr);
		
		try {
			pw = response.getWriter();
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "verifycode", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			
			String orderdate = DateTool.getCurrentDate("yyyyMMdd");
			
			StringBuffer signBuffer = new StringBuffer();
			signBuffer.append("merid=");
			signBuffer.append(Config.get().get("merid_ld"));
			signBuffer.append("&orderid=");
			signBuffer.append(orderid);
			signBuffer.append("&orderdate=");
			signBuffer.append(orderdate);
			signBuffer.append("&upversion=1.1");
			signBuffer.append("&verifycode=");
			signBuffer.append(verify_code);
			
			String sign = MD5Util.getMD5(signBuffer.toString() + Config.get().get("key_ld")).toLowerCase();// 加密后的密文
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("merid", Config.get().get("merid_ld"));
			data.put("orderid", orderid);
			data.put("orderdate", orderdate);
			data.put("upversion", "1.1");
			data.put("verifycode", verify_code);
			data.put("sign", sign);
			
			String bag = HttpTookit.doPost(Config.get().get("orderurl_ldconfirm"), data);
			
			logger.info("ldConfirmCode req : " + data.toString() + ", resp is : " + bag );
			
			if (bag != null) {
				JSONObject resp = JSONObject.parseObject(bag);
				
				String result = resp.getString("retCode");
				
				if ("0000".equals(result)) {
					respJson.put("orderid", orderid);
					respJson.put("status", STATUS_RESP_SUCCESS);
					receiveSms.setStatus(STATUS_SECOND);
				} else {
					receiveSms.setStatus(result);
				}
			}
			receiveSms.setHret(STATUS_SECOND);
			
			orderService.updateReceiveSms(receiveSms);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ldConfirmCode error : " + e.getMessage());
		}
		
		logBuffer.append(",服务端响应： " + respJson.toString());
		logger.info(logBuffer);
		
		pw.print(respJson.toString());
		return null;
	}
	
	@RequestMapping("/ldNotify.do")
	public ModelAndView ldNotify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("联动优势数据同步请求参数：" + requestStr);
		
		String respStr = "";
		
		try {
			pw = response.getWriter();
			
			String merid = RequestUtil.GetParamString(request, "merid", null);
			String goodsid = RequestUtil.GetParamString(request, "goodsid", null);
			String appid = RequestUtil.GetParamString(request, "appid", "");
			String channelid = RequestUtil.GetParamString(request, "channelid", "");
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String orderdate = RequestUtil.GetParamString(request, "orderdate", null);
			String transdate = RequestUtil.GetParamString(request, "transdate", null);
			String amount = RequestUtil.GetParamString(request, "amount", null);
			String amttype = RequestUtil.GetParamString(request, "amttype", null);
			String banktype = RequestUtil.GetParamString(request, "banktype", null);
			String mobileid = RequestUtil.GetParamString(request, "mobileid", null);
			String transtype = RequestUtil.GetParamString(request, "transtype", null);
			String settledate = RequestUtil.GetParamString(request, "settledate", null);
			String merpriv = RequestUtil.GetParamString(request, "merpriv", "");
			String retCode = RequestUtil.GetParamString(request, "retCode", null);
			String upversion = RequestUtil.GetParamString(request, "upversion", null);
			String sign = RequestUtil.GetParamString(request, "sign", null);
			
			StringBuffer signBuffer = new StringBuffer();
			signBuffer.append("merid=");
			signBuffer.append(URLDecoder.decode(merid, "UTF-8"));
			signBuffer.append("&goodsid=");
			signBuffer.append(URLDecoder.decode(goodsid, "UTF-8"));
			signBuffer.append("&appid=");
			signBuffer.append(URLDecoder.decode(appid, "UTF-8"));
			signBuffer.append("&channelid=");
			signBuffer.append(URLDecoder.decode(channelid, "UTF-8"));
			signBuffer.append("&orderid=");
			signBuffer.append(URLDecoder.decode(orderid, "UTF-8"));
			signBuffer.append("&orderdate=");
			signBuffer.append(URLDecoder.decode(orderdate, "UTF-8"));
			signBuffer.append("&transdate=");
			signBuffer.append(URLDecoder.decode(transdate, "UTF-8"));
			signBuffer.append("&amount=");
			signBuffer.append(URLDecoder.decode(amount, "UTF-8"));
			signBuffer.append("&amttype=");
			signBuffer.append(URLDecoder.decode(amttype, "UTF-8"));
			signBuffer.append("&banktype=");
			signBuffer.append(URLDecoder.decode(banktype, "UTF-8"));
			signBuffer.append("&mobileid=");
			signBuffer.append(URLDecoder.decode(mobileid, "UTF-8"));
			signBuffer.append("&transtype=");
			signBuffer.append(URLDecoder.decode(transtype, "UTF-8"));
			signBuffer.append("&settledate=");
			signBuffer.append(URLDecoder.decode(settledate, "UTF-8"));
			signBuffer.append("&merpriv=");
			signBuffer.append(URLDecoder.decode(merpriv, "UTF-8"));
			signBuffer.append("&retCode=");
			signBuffer.append(URLDecoder.decode(retCode, "UTF-8"));
			signBuffer.append("&upversion=");
			signBuffer.append(URLDecoder.decode(upversion, "UTF-8"));
			
			String localsign = MD5Util.getMD5(signBuffer.toString() + Config.get().get("key_ld")).toLowerCase();// 加密后的密文
			
			if (sign.equals(localsign)) {
				ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(Long.parseLong(orderid)));
				
				if (receiveSms != null) {
					String today = DateTool.getCurrentDate("yyyyMMdd");
					
					if (StringUtils.isNotEmpty(mobileid)) {
						receiveSms.setPhoneno(mobileid);
					}
					
					String province = getStateByMobile(receiveSms.getPhoneno());
					province = GetState(province);
					
					receiveSms.setState(province);
					
					if ("0000".equals(retCode)) {
						receiveSms.setStatus(STATUS_SUCCESS);
						receiveSms.setHret(HRET_SUCCESS);
					} else {
						receiveSms.setStatus(retCode);
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
				}
				String msg = merid + "|" + goodsid + "|" + orderid + "|" + orderdate + "|0000|" + "ok" + "|1.1";
				
				String loclSign = MD5Util.getMD5(msg + Config.get().get("key_ld")).toLowerCase();// 加密后的密文
				
				respStr = "<META NAME=\"MobilePayPlatform\" CONTENT=\""
					+ msg
					+ "|"
					+ loclSign
					+ "\">";
			}
		} catch (Exception e) {
			e.printStackTrace();
			respStr = "error";
			logger.error("ldNotify error :" + e.getMessage());
		}
		
		logBuffer.append(", 服务端响应为： " + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}

}
