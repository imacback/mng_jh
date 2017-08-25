package com.jh.mng.controller.ds;

import java.io.PrintWriter;

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
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.MyLog;
import com.jh.mng.util.RequestUtil;

/**
 * 人民视讯代码
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class RmsxController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(RmsxController.class);
	
	
	@RequestMapping("/rmsxOrder.do")
	public ModelAndView rmsxOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("人民视讯新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
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
			jObject.put("orderid", id);
			
			String bag = HttpTookit.doGet("http://218.94.146.154:8000/o/dmapi1/d5263ffb99d86e8c0525ebd707f3a351c2b1f6cd/?item_id=300008037001&channel_id=12139000&app_id=300000008037&app_key=1095D36B04F15EAF&phone_number=" + phone + "&item_price=" + consumeInfo.getFee() + "&cpparam=B1&cpparam=B1&exorder=B1", null, "utf-8", false);
			
			MyLog.InfoLog("人民视讯 req: " + phone + " resp is : " + bag);
			
			if (StringUtils.isNotEmpty(bag)) {
				jObject.put("status", STATUS_RESP_SUCCESS);
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(consumeCode);
			receiveSms.setCpparam(cpparam);
			
			if (StringUtils.isNotEmpty(bag)) {
				receiveSms.setHret(HRET_FIRST);
				receiveSms.setStatus(STATUS_FIRST);
				receiveSms.setExt(bag);
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
			logger.error("rmsxOrder error : " + e.getMessage());
		}
		
		logBuffer.append(" ,服务端响应为：" + jObject.toString());
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		return null;
	}
	
	@RequestMapping("/rmsxVerifyCode.do")
	public ModelAndView rmsxVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		JSONObject respJson = new JSONObject();
		respJson.put("status", STATUS_RESP_FAILED);
		
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("人民视讯业务短信验证参数为： " + requestStr);
		
		try {
			pw = response.getWriter();
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "verifycode", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			
			if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
				receiveSms.setHret(HRET_SECOND);
					   
				String ext = receiveSms.getExt();
					   
			    String url = "http://218.94.146.154:8000/o/dmapi2/d5263ffb99d86e8c0525ebd707f3a351c2b1f6cd/?content_sid=" + ext + "&code=" + verify_code;
				StrIntBag resp = HttpClientUtil.executeGet(url);
				
				MyLog.InfoLog("rmsxVerifyCode request : " + url + ", resp: " + resp);
				
				if (resp != null && resp._str != null) {
					JSONObject respObject = JSONObject.parseObject(resp._str);
					
					String status = respObject.getString("ReturnCode");
					
					if (status != null && status.equals("0")) {
						respJson.put("status", STATUS_RESP_SUCCESS);
						receiveSms.setStatus(STATUS_THIRD); //计费短信发送成功
					} else {
						respJson.put("status", respObject.getString("ReturnCode"));
						receiveSms.setStatus(status);
					}
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
	
	@RequestMapping("/rmsxNotify.do")
	public ModelAndView rmsxNotify(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
}
