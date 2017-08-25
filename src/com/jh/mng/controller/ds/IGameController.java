package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.MD5Util;
import com.jh.mng.util.Methods;
import com.jh.mng.util.MobileFromUtil;
import com.jh.mng.util.RequestUtil;

/**
 * 电信爱游戏（管控省份：河北、安徽、陕西、江西、甘肃、山西、广西、山东、河南、海南、新疆、福建、重庆）
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class IGameController extends AbstractMultiActionController {
	
	public static final Logger logger = Logger.getLogger(IGameController.class);
	
	@RequestMapping("/aiyxOrder.do")
	public ModelAndView aiyxOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("爱游戏业务新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String imsi = RequestUtil.GetParamString(request, "imsi", null);
			String phone = RequestUtil.GetParamString(request, "mobile", null); 
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(feecode);
			
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
			
			String game_code = "120356017337";
			String channel_code = "41040192";
			
			StringBuffer sb = new StringBuffer();
			sb.append("cp_order_id=");
			sb.append(id);
			sb.append("&game_code=");
			sb.append(game_code);
			sb.append("&consume_code=");
			sb.append(feecode);
			sb.append("&fee=");
			sb.append(consumeInfo.getFee());
			sb.append("&channel_code=");
			sb.append(channel_code);
			sb.append("&phone=");
			sb.append("&imsi=");
			sb.append(imsi);
			
			String needSign = id + game_code + imsi + channel_code + feecode + consumeInfo.getFee() + "db70ecc52d6a0be649cda2b2f2c4f788";
			
			sb.append("&sign_msg=");
			sb.append(MD5Util.getMD5(needSign).toLowerCase());
			
			StrIntBag strIntBag = HttpClientUtil.executeGet("http://open.play.cn/api/v1/charge/epay/wf/request_pay?" + sb.toString());
			
			logger.info("aiyouxi new req: " + sb.toString() + ", resp is :" + strIntBag);
			
			String smsnum = "";
			String sms = "";
			String result = "";
			String status = STATUS_FIRST;
			
			
			if (strIntBag != null && StringUtils.isNotEmpty(strIntBag._str)) {
				JSONObject json = JSONObject.parseObject(strIntBag._str);
				
				result = json.getString("code");
				String ext = json.getString("ext");
				
				if ("0".equals(result)) {
					
					JSONObject resp = JSONObject.parseObject(ext);
					
					if ("00".equals(resp.getString("result_code"))) {
						sms = resp.getString("order_info");
						smsnum = resp.getString("access_code");
						
						jObject.put("status", STATUS_RESP_SUCCESS);
						jObject.put("smsnum", smsnum);
						jObject.put("smsmsg", sms);
					} else {
						status = resp.getString("result_code");
					}
					jObject.put("orderid", id);
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
			receiveSms.setFee(consumeInfo.getFee());
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phone);
			receiveSms.setExt(imsi);
			
			orderService.createReceiveSms(receiveSms);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("aiyxOrder error : " + e.getMessage());
		}
		logBuffer.append(", 服务端响应为：" + jObject.toString());
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		
		return null;
	}

	@RequestMapping("/aiyxNotify.do")
	public ModelAndView aiyxNotify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("爱游戏验证或数据同步请求参数：");
		
		String respStr = "";
		
		try {
			pw = response.getWriter();
			
			String method = request.getParameter("method");
			
			String cp_order_id = request.getParameter("cp_order_id");
			String correlator = request.getParameter("correlator");
			String order_time = request.getParameter("order_time");
			String sign = request.getParameter("sign");
			String result_code = request.getParameter("result_code");
			String fee = request.getParameter("fee");
			String pay_type = request.getParameter("pay_type");
			
			logBuffer.append("method: " + method + ", cp_order_id:" + cp_order_id + ",correlator:" + correlator + ",order_time:" + order_time + ",sign:" + sign + ",result_code:" + result_code + ",fee :" + fee + ",pay_type:" + pay_type);
			
			if ("check".equals(method)) { //验证请求
				
				String localSign = MD5Util.getMD5(cp_order_id + correlator + order_time + method + "db70ecc52d6a0be649cda2b2f2c4f788").toLowerCase();
				
				if (localSign.equals(sign)) {
					ReceiveSms receiveSms = orderService.queryReceiveSmsById(cp_order_id);
					
					Document document = DocumentHelper.createDocument();
					
					Element root = document.addElement("sms_pay_check_resp");
					root.addElement("cp_order_id").setText(cp_order_id);
					root.addElement("correlator").setText(cp_order_id);
					
					if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
						root.addElement("fee").setText(String.valueOf(receiveSms.getFee()));
						root.addElement("if_pay").setText("0");
					} else {
						root.addElement("fee").setText("0");
						root.addElement("if_pay").setText("-1");
					}
					root.addElement("order_time").setText(DateTool.getCurrentDate("yyyyMMddHHmmss"));
					
					respStr = document.asXML();
					
					receiveSms.setStatus(STATUS_SECOND);
					receiveSms.setHret(HRET_SECOND);
					
					orderService.updateReceiveSms(receiveSms);
				}
				
			} else if ("callback".equals(method)) { //数据同步
				String localSign = MD5Util.getMD5(cp_order_id + correlator + result_code + fee + pay_type + method + "db70ecc52d6a0be649cda2b2f2c4f788").toLowerCase();
				
				if (localSign.equals(sign)) {
					ReceiveSms receiveSms = orderService.queryReceiveSmsById(cp_order_id);
					
					if (receiveSms != null) {
						int over_arpu = 0;
						String today = DateTool.getCurrentDate("yyyyMMdd");
						
						if ("00".equals(result_code)) {
							receiveSms.setStatus(STATUS_SUCCESS);
							receiveSms.setHret(HRET_SUCCESS);
						} else {
							receiveSms.setStatus(result_code);
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
						//超限验证
//						over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_HY", 1000000), Config.get().getInt("MAX_Y_FEE_HY", 1000000), today, receiveSms.getUserid(), receiveSms.getFee());
						
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
					
					Document document = DocumentHelper.createDocument();
					Element root = document.addElement("cp_notify_resp");
					root.addElement("h_ret").setText("0");
					root.addElement("cp_order_id").setText(cp_order_id);
					
					respStr = document.asXML();
				}
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			respStr = "error";
			logger.error("aiyxNotify error :" + e.getMessage());
		}
		
		logBuffer.append(", 服务端响应为： " + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}
	
	@RequestMapping("/updatePhone.do")
	public ModelAndView updatePhone(HttpServletRequest request, HttpServletResponse response) {
		
		List<String> mobileList = orderService.getUnknowMobile();
		
		if (mobileList != null) {
			String mobile = "";
			String provinceName = "";
			int state = 32;
			
			
			Map<String, String> map = new HashMap<String, String>();
			
			for (int i = 0; i < mobileList.size(); i++) {
				mobile = mobileList.get(i);
//				provinceName = Methods.request(mobile);
				String cityStr = MobileFromUtil.getMobileFrom(mobile);
				
				String[] array = cityStr.split(",");
				
				provinceName = array[0];
				state = Integer.parseInt(Methods.getStateId(provinceName));
				
				if (state != 0) {
					if (map.get(mobile.substring(0, 7)) == null) {
						orderService.createSmsHaoduan(mobile.substring(0, 7), state);
						map.put(mobile.substring(0, 7), "1");
					} 
				}
			}
			
			System.out.println("done");
		}
		
		return null;
		
	}
}
