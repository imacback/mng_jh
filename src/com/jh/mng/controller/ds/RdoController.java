package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.jh.mng.util.RequestUtil;

@Controller
@RequestMapping("/ds")
public class RdoController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(HyController.class);
	
	@RequestMapping("/rdoUrl.do")
	public ModelAndView hyRdoUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("rdo order: " + requestStr);
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "phone", null);
			String cm = RequestUtil.GetParamString(request, "cm", null);
			String mcpid = RequestUtil.GetParamString(request, "mcpid", null);
			
			String chnlId = cpparam.substring(0,2);
			
			ReceiveSms receiveSms = new ReceiveSms();
			Long id = orderService.getNextOrderId();
			
			StringBuffer param = new StringBuffer();
			param.append("mcpid=");
			param.append(mcpid);
			param.append("&orderno=");
			param.append(id);
			param.append("&feecode=");
			param.append(feecode);
			param.append("&cm=");
			param.append(cm);
			param.append("&mobile=");
			param.append(phone);
			param.append("&key=");
			param.append(Config.get().get("key_hy"));
			param.append("&customerid=bjhysjdjf");
			
			String bag = HttpTookit.doGet("http://bjhysj.hzcrack.com:9059/jf/platform/rdoCash/reqOrder?" + param.toString(), null, "utf-8", false);
			
			logger.info("hy req : " + param.toString() + ",bag resp is : " + bag);
			
			if (bag != null) {
				JSONObject jsObject = JSONObject.parseObject(bag);
				
				boolean result = jsObject.getBoolean("result");
				String msg = jObject.getString("msg");
				
				receiveSms.setExt(msg);
				
				if (result) {
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("orderid", id);
				}
			}
			
			String province = getStateByMobile(phone);
			province = GetState(province);
			
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
			
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyRdoOrder error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/rdoVerifyCode.do")
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
				
				StringBuffer param = new StringBuffer();
				param.append("validCode=");
				param.append(verifyCode);
				param.append("&orderno=");
				param.append(orderId);
				
				String bag = HttpTookit.doGet("http://bjhysj.hzcrack.com:9059/jf/platform/rdoCash/sendValidCode?" + param.toString(), null, "utf-8", false);
				
				logger.info("hyRdoVerifyCode resp :" + bag);
				
				if (bag != null) {
					JSONObject jsObject = JSONObject.parseObject(bag);
					
					boolean result = jsObject.getBoolean("result");
					
					if (result) {
						jObject.put("status", STATUS_RESP_SUCCESS);
						jObject.put("orderid", orderId);
					}
					
					String msg = jObject.getString("msg");
					receiveSms.setExt(msg);
				}
				
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
	
	
	@RequestMapping("/rdoReport.do")
	public ModelAndView hyRdoReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			String req = request.getQueryString();
			
			logger.info("汉娱阅读2.0数据同步请求参数为 :" + req);
			
			String orderNo = request.getParameter("orderNo");
			String status = request.getParameter("resultCode");
			
			
			if (true) {
				ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderNo);
				
				if (receiveSms != null) {
					int over_arpu = 0;
					String today = DateTool.getCurrentDate("yyyyMMdd");
					
					String province = getStateByMobile(receiveSms.getPhoneno());
					province = GetState(province);
					
					receiveSms.setState(province);
					
					if (status != null && "1".equals(status)) {
						receiveSms.setStatus(STATUS_SUCCESS);
						receiveSms.setHret(HRET_SUCCESS);
					} else {
						receiveSms.setStatus(status);
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
					pw.print("200");
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyRdoReport error : " + e.getMessage());
		}
		return null;
	}
	
	
	@RequestMapping("/hyRdo.do")
	public ModelAndView hyRdo(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("rdo order: " + requestStr);
			
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
			param.append("&vt=3&msisdn=");
			param.append(phone);
			
			String payUrl = Config.get().get("orderurl_hy")+ "?" + param.toString();
			
			logger.info("payUrl :" + payUrl);
			
			jObject.put("payUrl", payUrl);
			
//			String feecode = RequestUtil.GetParamString(request, "feecode", null);
//			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
//			String phone = RequestUtil.GetParamString(request, "phone", null);
//			String cm = RequestUtil.GetParamString(request, "cm", null);
//			String mcpid = RequestUtil.GetParamString(request, "mcpid", null);
//			
//			String chnlId = cpparam.substring(0,2);
//			
//			ReceiveSms receiveSms = new ReceiveSms();
//			Long id = orderService.getNextOrderId();
//			
//			StringBuffer param = new StringBuffer();
//			param.append("mcpid=");
//			param.append(mcpid);
//			param.append("&orderno=");
//			param.append(id);
//			param.append("&feecode=");
//			param.append(feecode);
//			param.append("&cm=");
//			param.append(cm);
//			param.append("&mobile=");
//			param.append(phone);
//			param.append("&key=");
//			param.append(Config.get().get("key_hy"));
//			
//			String bag = HttpTookit.doGet("http://bjhysj.hzcrack.com:9059/jf/platform/rdoCash/reqOrder?" + param.toString(), null, "utf-8", false);
//			
//			logger.info("hy req : " + param.toString() + ",bag resp is : " + bag);
//			
//			if (bag != null) {
//				JSONObject jsObject = JSONObject.parseObject(bag);
//				
//				boolean result = jsObject.getBoolean("result");
//				String msg = jObject.getString("msg");
//				
//				receiveSms.setExt(msg);
//				
//				if (result) {
//					jObject.put("status", STATUS_RESP_SUCCESS);
//					jObject.put("orderid", id);
//				}
//			}
			
//			String province = getStateByMobile(phone);
//			province = GetState(province);
			
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
			
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hyRdoOrder error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/rdoNotity.do")
	public ModelAndView yydNotity(HttpServletRequest request ,HttpServletResponse response){
		try {
			PrintWriter pw = response.getWriter();
			
			String params = request.getQueryString();
			
			logger.info("汉娱阅读2.0 破解方同步请求参数为：" + params);
			
			String orderno =request.getParameter("orderno");
			String status =request.getParameter("status");
//			String msg = request.getParameter("msg");
//			msg = URLDecoder.decode(msg, "utf-8");
//			msg = URLDecoder.decode(msg, "utf-8");
			String msg="";
			boolean flag=false;
			String[] s = params.split("&");
			for (int i = 0; i < s.length; i++) {
				String[] str = s[i].split("=");
				for (int j = 0; j < str.length; j++) {
					if(str[j].equals("msg")){
						msg = str[j+1];
						msg = URLDecoder.decode(msg, "utf-8");
						flag = true;
						break;
					}
				}
				if(flag){
					break;
				}
			}
			if("2".equals(status)){
				ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderno);
				if(receiveSms != null){
					receiveSms.setExt(msg);
					logger.info("添加验证码错误信息到  Ext");
					orderService.updateReceiveSms(receiveSms);
				}
			}
			pw.print("200");
			logger.info("响应内容为：200");
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("rdoNotity  error:" + e.getMessage());
		}
		return null;
		
	}
}
