package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.util.List;

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
import com.jh.mng.util.Base64Util;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.MyLog;
import com.jh.mng.util.RequestUtil;

@Controller
@RequestMapping("/ds")
public class DyyController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(DyyController.class);
	
	@RequestMapping("/hyDDoDyyOrderBase64.do")
	public ModelAndView hyDDoDyyOrderBase64(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		String times = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(times + "单应用刷卡业务新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String imsi = RequestUtil.GetParamString(request, "imsi", null);
			String phone = RequestUtil.GetParamString(request, "mobile", null);
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoById(Long.parseLong(feecode));
			
			String consumeCode = consumeInfo.getConsumeCode(); 
			
			String province = "00";
			
			if (StringUtils.isNotEmpty(phone)) {
				province = getStateByMobile(phone);
			} else {
				phone = "none";
				province = cpparam.substring(2,4);
			}
			province = GetState(province);
			
			Integer channelId = Integer.parseInt(cpparam.substring(0, 2));

			ReceiveSms receiveSms = new ReceiveSms();
			
			Long id = orderService.getNextOrderId();
			
			StringBuffer param = new StringBuffer();
			param.append("imei=");
			param.append(imei);
			param.append("&imsi=");
			param.append(imsi);
			param.append("&linkId=");
			param.append(consumeCode);
			param.append("&cpparam=");
			param.append(id);
			param.append("&chid=");
			param.append("youyu");
			param.append("&pid=8089");
			
			String bag = "";
			
			String reqTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			if (consumeCode.startsWith("ddmj")) {
				bag = HttpTookit.doGet("http://120.55.67.88:8080/dmProject/getall?" + param.toString(), null, "utf-8", false);
			} else if (consumeCode.startsWith("jjdm")) {
				bag = HttpTookit.doGet("http://120.55.67.88:8080/dmProject/getall?" + param.toString(), null, "utf-8", false);
			}else if (consumeCode.startsWith("qsmx")) {
				bag = HttpTookit.doGet("http://www.topwises.com/dmfp/sp/getCommand.php?" + param.toString(), null, "utf-8", false);
			}
			
			String respTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			MyLog.InfoLog( reqTimes + "动漫单应用 req : " + param.toString() + ", " + respTimes + "bag resp is : " + bag + ", 响应时间:" + (DateTool.StringToDate(respTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(reqTimes, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000);
			
			String smsnum = "";
			String sms = "";
			
			if (bag != null) {
				if (!bag.contains("####") || bag.contains("error")) {
				} else {
					String[] str = bag.split("####");
					smsnum = str[0];
					sms = str[1];
				}
				
				if (StringUtils.isNotEmpty(smsnum) && StringUtils.isNotEmpty(sms)) {
					jObject.put("orderid", id);
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("smsnum", smsnum);
					jObject.put("sms", Base64Util.encode(sms.getBytes()));
				}
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(consumeCode);
			receiveSms.setCpparam(cpparam);
			
			if (StringUtils.isNotEmpty(smsnum) && StringUtils.isNotEmpty(sms)) {
				receiveSms.setHret(HRET_SECOND);
				receiveSms.setStatus(STATUS_SECOND);
			} else {
				receiveSms.setHret(HRET_FIRST);
				receiveSms.setStatus(STATUS_FIRST);
				receiveSms.setExt(bag);
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
			logger.error("hyDDoDyyOrder error : " + e.getMessage());
		}
		
		String endTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		logBuffer.append(", " + endTimes + " ,服务端响应为：" + jObject.toString() + ", 响应时间：" + (DateTool.StringToDate(endTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(times, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	@RequestMapping("/hyDDoDyyOrder.do")
	public ModelAndView hyDDoDyyOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		String times = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(times + "动漫单应用业务新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String imsi = RequestUtil.GetParamString(request, "imsi", null);
			String phone = RequestUtil.GetParamString(request, "mobile", null);
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoById(Long.parseLong(feecode));
			
			String consumeCode = consumeInfo.getConsumeCode(); 
			
			String province = "00";
			
			if (StringUtils.isNotEmpty(phone)) {
				province = getStateByMobile(phone);
			} else {
				phone = "none";
				province = cpparam.substring(2,4);
			}
			province = GetState(province);
			
			Integer channelId = Integer.parseInt(cpparam.substring(0, 2));

			ReceiveSms receiveSms = new ReceiveSms();
			
			Long id = orderService.getNextOrderId();
			
			StringBuffer param = new StringBuffer();
			param.append("imei=");
			param.append(imei);
			param.append("&imsi=");
			param.append(imsi);
			param.append("&linkId=");
			param.append(consumeCode);
			param.append("&cpparam=");
			param.append(id);
			param.append("&chid=");
			param.append("youyu");
			param.append("&pid=8089");
			
			String bag = "";
			
			String reqTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			if (consumeCode.startsWith("ddmj")) {
				bag = HttpTookit.doGet("http://120.55.67.88:8080/dmProject/getall?" + param.toString(), null, "utf-8", false);
			} else if (consumeCode.startsWith("jjdm")) {
				bag = HttpTookit.doGet("http://120.55.67.88:8080/dmProject/getall?" + param.toString(), null, "utf-8", false);
			} else if (consumeCode.startsWith("qsmx")) {
				bag = HttpTookit.doGet("http://www.topwises.com/dmfp/sp/getCommand.php?" + param.toString(), null, "utf-8", false);
			}
			
			String respTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			logger.info( reqTimes + "动漫单应用 req : " + param.toString() + ", " + respTimes + "bag resp is : " + bag + ", 响应时间:" + (DateTool.StringToDate(respTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(reqTimes, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
			
			String smsnum = "";
			String sms = "";
			
			if (bag != null) {
				if (!bag.contains("####") || bag.contains("error")) {
				} else {
					String[] str = bag.split("####");
					smsnum = str[0];
					sms = str[1];
				}
				
				if (StringUtils.isNotEmpty(smsnum) && StringUtils.isNotEmpty(sms)) {
					jObject.put("orderid", id);
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("smsnum", smsnum);
					jObject.put("sms", sms);
				} 
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(consumeCode);
			receiveSms.setCpparam(cpparam);
			
			if (StringUtils.isNotEmpty(smsnum) && StringUtils.isNotEmpty(sms)) {
				receiveSms.setHret(HRET_SECOND);
				receiveSms.setStatus(STATUS_SECOND);
			} else {
				receiveSms.setHret(HRET_FIRST);
				receiveSms.setStatus(STATUS_FIRST);
				receiveSms.setExt(bag);
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
			logger.error("hyDDoDyyOrder error : " + e.getMessage());
		}
		
		String endTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		logBuffer.append(", " + times + "服务端响应为：" + jObject.toString() + ", 响应时间：" + (DateTool.StringToDate(endTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(times, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	@RequestMapping("/hyDdoDyyNotity.do")
	public ModelAndView hyDdoDyyNotity(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter pw = null;
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("动漫单应用业务数据同步请求参数：" + requestStr);
		
		String respStr = "ok";
		
		try {
			pw = response.getWriter();
			
			String orderId = request.getParameter("cpparam");
			String status = request.getParameter("status");
			String phone = request.getParameter("tel");
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(Long.parseLong(orderId)));
			
			if (receiveSms != null) {
//				int fee = -1;
				int over_arpu = 0;
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				if (StringUtils.isNotEmpty(phone)) {
					receiveSms.setPhoneno(phone);
				}
				
				String province = getStateByMobile(receiveSms.getPhoneno());
				province = GetState(province);
				
				receiveSms.setState(province);
				
				if ("0".equals(status)) {
					receiveSms.setStatus(STATUS_SUCCESS);
					receiveSms.setHret(HRET_SUCCESS);
				} else {
					receiveSms.setStatus(status);
					receiveSms.setHret(HRET_FAILED);
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
				
				if (consumeInfo.getGameInfoId().intValue() == 641) { //艺动晴天动漫 
					String province_stop_yd = Config.get().get("province_stop_dyy_yd");
					logger.info("当前省份：" + province +", 艺动暂停省份==========================" + province_stop_yd);
					if (StringUtils.isNotEmpty(province_stop_yd)) {
						String[] provinces = province_stop_yd.split(",");
						
						for (int i = 0; i < provinces.length; i++) {
							if (provinces[i].equals(province)) {
								receiveSms.setSyncchnl(200);
								receiveSms.setDec_flag(1);
								break;
							}
						}
					}
				} else {
					String province_stop = Config.get().get("province_stop_dyy");
					
					logger.info("当前省份：" + province +", 暂停省份==========================" + province_stop);
					
					if (StringUtils.isNotEmpty(province_stop)) {
						String[] provinces = province_stop.split(",");
						
						for (int i = 0; i < provinces.length; i++) {
							if (provinces[i].equals(province)) {
								receiveSms.setSyncchnl(200);
								receiveSms.setDec_flag(1);
								break;
							}
						}
					}
				}
				
				orderService.updateReceiveSms(receiveSms);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			respStr = "error";
			logger.error("hyDdoDyyNotity error :" + e.getMessage());
		}
		
		logBuffer.append(", 服务端响应为： " + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}
}
