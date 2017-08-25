package com.jh.mng.controller.ds;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.Mask;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.RequestUtil;

/**
 * 汉娱动漫包月
 * @author Google
 *
 */
@Controller
@RequestMapping("/ds")
public class DdoController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(DdoController.class);
	
	@RequestMapping("/ddoOrder.do")
	public ModelAndView ddoOrder(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("ddoOrder: " + requestStr);
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "phone", null);
			String chid = RequestUtil.GetParamString(request, "chid", null);
			
			String chnlId = cpparam.substring(0,2);
			
			ReceiveSms receiveSms = new ReceiveSms();
			Long id = orderService.getNextOrderId();
			
			StringBuffer param = new StringBuffer();
			param.append("pid=");
			param.append(feecode);
			param.append("&mobileno=");
			param.append(phone);
			param.append("&chid=");
			param.append(chid);
			param.append("&cpparam=");
			param.append(cpparam);
			
			jObject.put("orderid", id);
			
			String bag = HttpTookit.doGet("http://115.28.252.55:13888/cmddo/init?" + param.toString(), null, "utf-8", false);
			
			logger.info("hy ddo req : http://115.28.252.55:13888/cmddo/init?" + param.toString() + ",bag resp is : " + bag);
			
			if (bag != null) {
				JSONObject jsObject = JSONObject.parseObject(bag);
				
				String result = jsObject.getString("result");
//				String msg = jsObject.getString("msg");
				String orderid = jsObject.getString("orderid");
				
				if ("0".equals(result)) {
					jObject.put("status", STATUS_RESP_SUCCESS);
					receiveSms.setExt(orderid);
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
			logger.error("ddoOrder error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/ddoVerifyCode.do")
	public ModelAndView ddoVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("汉娱ddo短信验证请求参数为: " + requestStr);
			
			String verifyCode = RequestUtil.GetParamString(request, "verifycode", null);
			Long orderId = RequestUtil.GetParamLong(request, "orderid", null);
			
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderId));
			
			if (receiveSms != null) {
				receiveSms.setHret(HRET_SECOND);
				
				jObject.put("orderid", orderId);
				
				StringBuffer param = new StringBuffer();
				param.append("orderid=");
				param.append(receiveSms.getExt());
				param.append("&smscode=");
				param.append(verifyCode);
				
				String bag = HttpTookit.doGet("http://115.28.252.55:13888/cmddo/pay?" + param.toString(), null, "utf-8", false);
				
				logger.info("ddoVerifyCode resp :" + bag);
				
				if (bag != null) {
					JSONObject jsObject = JSONObject.parseObject(bag);
					
					String result = jsObject.getString("result");
					String cp_orderid = jsObject.getString("cp_orderid");
					
					if ("0".equals(result)) {
						jObject.put("status", STATUS_RESP_SUCCESS);
					}
					
//					String msg = jObject.getString("msg");
					receiveSms.setExt(cp_orderid);
				}
				receiveSms.setStatus(STATUS_SECOND); //计费短信发送成功
				
				orderService.updateReceiveSms(receiveSms);
			}
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ddoVerifyCode error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/etNotify.do")
	public ModelAndView notify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		
		StringBuffer logBuffer = new StringBuffer();
		
		String respStr = "";
		
		try {
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			pw = response.getWriter();
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			logBuffer.append("汉娱DDO包月数据同步请求参数：" + requestStr);
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
			String orderId = root.elementText("OrderID");
			String RentSuccess = root.elementText("RentSuccess");
			String payCode = root.elementText("PayCode");
			
			respStr = "<Response><MsgType> MonthlyOrderNotifyResp</MsgType><Result>0</Result><ErrorMsg>ok</ErrorMsg></Response>";
			
			if (true) {
				ReceiveSms query = new ReceiveSms();
				query.setConsumecode(payCode);
				query.setExt(orderId);
				
				List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByExt(orderId);
				
				if (receiveSmsList != null && receiveSmsList.size() == 1) {
					ReceiveSms receiveSms = receiveSmsList.get(0);
					String today = DateTool.getCurrentDate("yyyyMMdd");
					
					if ("0".equals(RentSuccess)) {
						receiveSms.setStatus(STATUS_SUCCESS);
						receiveSms.setHret(HRET_SUCCESS);
					} else {
						receiveSms.setStatus(RentSuccess);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			respStr = "error";
			logger.error("etNotify error :" + e.getMessage());
		}
		
		logBuffer.append(", 服务端响应为： " + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}
}
