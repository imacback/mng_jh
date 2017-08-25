package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.util.List;

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
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.RequestUtil;

/**
 * 联通爱时尚
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class LtAssController extends AbstractMultiActionController {

	private static final Logger logger = Logger.getLogger(LtAssController.class);
	
	/**
	 * 爱时尚短验
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/assPageOrder.do")
	public ModelAndView assPageOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		String times = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(times + "爱时尚计费新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "mobile", null);
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoById(Long.parseLong(feecode));
			
			String province = "00";
			
			province = getStateByMobile(phone);
			province = GetState(province);
			
			Integer channelId = Integer.parseInt(cpparam.substring(0,2));

			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setHret(HRET_FAILED);
			receiveSms.setStatus(STATUS_FAILED);
			
			Long id = orderService.getNextOrderId();
			
			jObject.put("orderid", id);
			
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append("mobile=");
			reqBuffer.append(phone);
			reqBuffer.append("&msg=");
			reqBuffer.append(consumeInfo.getConsumeCode());
			
			String reqTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			StrIntBag bag = HttpClientUtil.executeGet("http://124.232.138.78:8080/woread/dy/hzc/reqapi?" + reqBuffer.toString());
			
			String respTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			logger.info( reqTimes + "assPageOrder req : " + reqBuffer.toString() + ", " + respTimes + "bag resp is : " + bag + ", 响应时间:" + (DateTool.StringToDate(respTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(reqTimes, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000);
			
			if (bag != null && StringUtils.isNotEmpty(bag._str)) {
				JSONObject resp = JSONObject.parseObject(bag._str);
				
				String code = resp.getString("code");
				String innercode = resp.getString("innercode");
				
				if ("0000".equals(code) && "0000".equals(innercode)) {
					receiveSms.setStatus(STATUS_FIRST);
					jObject.put("status", STATUS_RESP_SUCCESS);
				} else {
					receiveSms.setStatus(code);
				}
			}
			
			receiveSms.setHret(HRET_FIRST);
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
			logger.error("assPageOrder error : " + e.getMessage());
		}
		
		String endTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		logBuffer.append(", " + endTimes + " ,服务端响应为：" + jObject.toString() + ", 响应时间：" + (DateTool.StringToDate(endTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(times, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	
	/**
	 * 短验验证码
	 * @return
	 */
	@RequestMapping("/assPageConfirm.do")
	public ModelAndView assPageConfirm(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		JSONObject respJson = new JSONObject();
		respJson.put("status", STATUS_RESP_FAILED);
		
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("爱时尚短验验证参数为： " + requestStr);
		
		try {
			pw = response.getWriter();
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "verifycode", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			
			if (receiveSms != null) {
				StringBuffer reqBuffer = new StringBuffer();
				reqBuffer.append("mobile=");
				reqBuffer.append(receiveSms.getPhoneno());
				reqBuffer.append("&msg=");
				reqBuffer.append(receiveSms.getConsumecode());
				reqBuffer.append("&verifycode=");
				reqBuffer.append(verify_code);
				reqBuffer.append("&cpdata=");
				reqBuffer.append(receiveSms.getId());
				
				String confirmUrl = "http://124.232.138.78:8080/woread/dy/hzc/reqapi?" + reqBuffer.toString();
				
				StrIntBag bag = HttpClientUtil.executeGet(confirmUrl);
				
				logger.info("assPageConfirm req : " + confirmUrl + ", resp is : " + bag );
				
				if (bag != null && StringUtils.isNotEmpty(bag._str)) {
					JSONObject resp = JSONObject.parseObject(bag._str);
					
					String code = resp.getString("code");
					String innercode = resp.getString("innercode");
					
					if ("0000".equals(code) && "0000".equals(innercode)) {
						receiveSms.setStatus(STATUS_SECOND);
						respJson.put("status", STATUS_RESP_SUCCESS);
					} else {
						receiveSms.setStatus(code);
					}
				}
			}
			receiveSms.setHret(STATUS_SECOND);
			
			orderService.updateReceiveSms(receiveSms);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("assPageConfirm error : " + e.getMessage());
		}
		
		logBuffer.append(",服务端响应： " + respJson.toString());
		logger.info(logBuffer);
		
		pw.print(respJson.toString());
		return null;
	}
	
	@RequestMapping("/assPageNotify.do")
	public ModelAndView assPageNotify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("爱时尚短验业务数据同步请求参数：" + requestStr);
		
		String respStr = "ok";
		
		try {
			pw = response.getWriter();
			
			String orderId = request.getParameter("cpdata");
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderId);
			
			if (receiveSms != null) {
				int over_arpu = 0;
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
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
			logger.error("assPageNotify error :" + e.getMessage());
		}
		
		logBuffer.append(", 服务端响应为： " + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}
	
	@RequestMapping("/assOrder.do")
	public ModelAndView assOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		String times = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append(times + "爱时尚业务新订单请求参数：" + params);
		
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
			param.append("Cmd=");
			param.append(consumeCode);
			param.append("&Imsi=");
			param.append(imsi);
			param.append("&Imei=");
			param.append(imei);
			param.append("&cpdata=");
			param.append(id);
			
			String bag = "";
			
			String reqTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			bag = HttpTookit.doGet("http://124.232.153.97:7003/unicom/pay/uc2?" + param.toString(), null, "utf-8", false);
			
			String respTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			
			logger.info( reqTimes + "ass应用 req : " + param.toString() + ", " + respTimes + "bag resp is : " + bag + ", 响应时间:" + (DateTool.StringToDate(respTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(reqTimes, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
			
			String smsnum = "";
			String sms = "";
			
			if (bag != null) {
				Document doc = DocumentHelper.parseText(bag);
				Element root = doc.getRootElement();
				
				String returnCode = root.elementText("returnCode");
				String orderSms = root.elementText("OrderSms");
				String orderNum = root.elementText("OrderNum");
				String confirmNum = root.elementText("ConfirmNum");
				
				smsnum = orderNum;
				sms = orderSms;
				
				if ("0".equals(returnCode)) {
					jObject.put("orderid", id);
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("smsnum", orderNum);
					jObject.put("sms", orderSms);
					jObject.put("confirmNum", confirmNum);
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
			logger.error("assOrder error : " + e.getMessage());
		}
		
		String endTimes = DateTool.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		
		logBuffer.append(", " + times + "服务端响应为：" + jObject.toString() + ", 响应时间：" + (DateTool.StringToDate(endTimes, "yyyy-MM-dd HH:mm:ss").getTime() - DateTool.StringToDate(times, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000 );
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
