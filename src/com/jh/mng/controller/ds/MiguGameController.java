package com.jh.mng.controller.ds;

import java.io.InputStream;
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
import com.jh.mng.common.StrIntBag;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.Mask;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.Base64Util;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.RequestUtil;

@Controller
@RequestMapping("/ds")
public class MiguGameController extends AbstractMultiActionController {

	
	private static final Logger logger = Logger.getLogger(MiguGameController.class);
	
	@RequestMapping("/miguOrder.do")
	public ModelAndView miguOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("咪咕游戏业务新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String imsi = RequestUtil.GetParamString(request, "imsi", null);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String ip = RequestUtil.GetParamString(request, "ip", null); 
			String phone = RequestUtil.GetParamString(request, "phone", null);
			
//			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(feecode);
			
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
			
			StringBuffer sb = new StringBuffer();
			sb.append("pid=");
			sb.append(feecode);
			sb.append("&imsi=");
			sb.append(imsi);
			sb.append("&imei=");
			sb.append(imei);
			sb.append("&ua==HUAWEIG510-0010");
			sb.append("&cpparam=");
			sb.append(id);
			sb.append("&ip=");
			sb.append(ip);
			
			StrIntBag strIntBag = HttpClientUtil.executeGet("http://115.28.113.167:13266/migu/api?" + sb.toString());
			
			logger.info("migu new req: " + sb.toString() + ", resp is :" + strIntBag);
			
			String smsnum = "";
			String sms = "";
			String result = "";
			String status = STATUS_FIRST;
			
			jObject.put("orderid", id);
			
			
			if (strIntBag != null && StringUtils.isNotEmpty(strIntBag._str)) {
				JSONObject json = JSONObject.parseObject(strIntBag._str);
				
				result = json.getString("resultcode");
				
				if ("0".equals(result)) {
					
					sms = json.getString("migusms");
					smsnum = json.getString("miguport");
					
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("smsnum", smsnum);
					jObject.put("smsmsg", Base64Util.decode(sms));
				} else {
					status = result;
				}
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(HRET_FIRST);
			receiveSms.setStatus(status);
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
			receiveSms.setExt(imsi);
			
			orderService.createReceiveSms(receiveSms);
			
			logBuffer.append(", 服务端响应为：" + jObject.toString());
			logger.info(logBuffer.toString());
			
			pw.print(jObject.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("miguOrder error : " + e.getMessage());
		}
		
		
		return null;
	}
	
	@RequestMapping("/miguNotify.do")
	public ModelAndView miguNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter pw = null;
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("咪咕游戏业务数据同步请求参数：" + requestStr);
		
		String respStr = "ok";
		
		try {
			pw = response.getWriter();
			
			String orderId = request.getParameter("cpparam");
			String status = request.getParameter("result");
			String phone = request.getParameter("mobileno");
			
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
				orderService.updateReceiveSms(receiveSms);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			respStr = "error";
			logger.error("miguNotify error :" + e.getMessage());
		}
		
		logBuffer.append(", 服务端响应为： " + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}
	
	@RequestMapping("/jdGmaeNotify.do")
	public ModelAndView jdGmaeNotify(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("咪咕游戏业务基地同步请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
//			int n = in.read(b, 0, 10000);
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			
			logger.info("咪咕游戏业务基地同步为：" + requestStr);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("jdGmaeNotify error : " + e.getMessage());
		}
		logBuffer.append(", 服务端响应为：" + jObject.toString());
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		
		return null;
	}
}
