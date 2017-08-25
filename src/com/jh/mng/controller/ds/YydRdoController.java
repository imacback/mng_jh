package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import antlr.Lookahead;

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
import com.sun.jna.platform.win32.WinUser.MSG;

/**
 * 盈阅读
 * @author xushaobo
 *
 */
@Controller
@RequestMapping("/ds")
public class YydRdoController extends AbstractMultiActionController {
	private static final Logger logger = Logger.getLogger(YydRdoController.class);
	
	@RequestMapping("/yydOrder.do")
	public ModelAndView yydOrder(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("盈阅读 order: " + requestStr);
			
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
			param.append("&key=yingyuekeji");
			param.append("&customerid=bjhysjdjf1");
			String bag = HttpTookit.doGet("http://bjhysj.hzcrack.com:9059/jf/platform/rdoCash/reqOrder?" + param.toString(), null, "utf-8", false);
			
			logger.info("盈阅读 req : " + param.toString() + ",bag resp is : " + bag);
			
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
			logger.error("盈阅读Order error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/yydVerifyCode.do")
	public ModelAndView yydVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("盈阅读短信验证请求参数为: " + requestStr);
			
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
				
				logger.info("盈阅读短信验证VerifyCode resp :" + bag);
				
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
			logger.error("盈阅读VerifyCode error : " + e.getMessage());
		}
		return null;
	}
	
	
	@RequestMapping("/yydReport.do")
	public ModelAndView yydReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			String req = request.getQueryString();
			String resp = "";
			logger.info("盈阅读数据同步请求参数为 :" + req);
			
			String orderNo = request.getParameter("orderNo");
			String msisdn = request.getParameter("msisdn");
			String resultCode = request.getParameter("resultCode");
			
			
			if (true) {
				ReceiveSms receiveSms= orderService.queryReceiveSmsById(orderNo);
				
				if (receiveSms != null) {
					int over_arpu = 0;
					String today = DateTool.getCurrentDate("yyyyMMdd");
					
					if (StringUtils.isNotEmpty(msisdn)) {
						String province = getStateByMobile(msisdn);
						province = GetState(province);
						
						receiveSms.setState(province);
					}
					
					if ("1".equals(resultCode)) {
						receiveSms.setStatus(STATUS_SUCCESS);
						receiveSms.setHret(HRET_SUCCESS);
						logger.info("status  发生改变");
					} else {
						if (StringUtils.isNotEmpty(resultCode)) {
							receiveSms.setStatus(resultCode);
							receiveSms.setHret(HRET_SUCCESS);
						} else {
							receiveSms.setStatus(STATUS_FAILED);
							receiveSms.setHret(HRET_SUCCESS);
						}
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
					logger.info("订单状态已修改");
					resp = "200";
					pw.print(resp);
					logger.info("盈阅读同步响应内容：" + resp);
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("yydReport error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/yydNotity.do")
	public ModelAndView yydNotity(HttpServletRequest request ,HttpServletResponse response){
		try {
			PrintWriter pw = response.getWriter();
			
			String params = request.getQueryString();
			
			logger.info("盈阅读破解方同步请求参数为：" + params);
			
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
			logger.error("yydNotity  error:" + e.getMessage());
		}
		return null;
		
	}
	
}
