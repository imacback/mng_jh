package com.jh.mng.controller.ds;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.Base64Util;
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.MD5Util;
import com.jh.mng.util.RequestUtil;

/**
 * 汉娱DDO包月 金海-e主题特价会员包
 * @author xushaobo
 *
 */
@Controller
@RequestMapping("/ds")
public class EthemesController extends AbstractMultiActionController{
	
	private static final Logger logger = Logger.getLogger(EthemesController.class);
	
	@RequestMapping("/etIndex.do")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		Long id = orderService.getNextOrderId();
		
		String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
		String requestID = "600000008352" + id; 
		String appId = "600000008352";
		String payCode = "300008352001";
		String channelId = "700002982";
		
		String str1 = "http://wap.dm.10086.cn/apay/monthlyOrderHandle.jsp?RequestID=" +  requestID;
			   str1 += "&AppID=" + appId;
			   str1 += "&PayCode=" + payCode + "&TimeStamp=" + timeStamp + "&ChannelID=" + channelId;
			   
		String needSign = requestID + "&" + appId + "&MS5S1AHA0GXKYJKA&" + payCode + "&" + timeStamp + "&" + channelId;
		String signature = Base64Util.encode(MD5Util.getMD5(needSign).getBytes());
		
		str1 += "&Signature=" + signature;
		
		request.setAttribute("str1", str1);
		
		
		String requestID2 = "600000008353" + id; 
		String appId2 = "600000008353";
		String payCode2 = "300008353001";
		
		String str2 = "http://wap.dm.10086.cn/apay/monthlyOrderHandle.jsp?RequestID=" +  requestID2;
			   str2 += "&AppID=" + appId2;
			   str2 += "&PayCode=" + payCode2 + "&TimeStamp=" + timeStamp + "&ChannelID=" + channelId;
			   
		String needSign2 = requestID2 + "&" + appId2 + "&8KLSL5GSGUA9YZ7O&" + payCode2 + "&" + timeStamp + "&" + channelId;
		String signature2 = Base64Util.encode(MD5Util.getMD5(needSign2).getBytes());
		
		str2 += "&Signature=" + signature2;
		
		request.setAttribute("str2", str2);
		
		String requestID3 = "600000008354" + id; 
		String appId3 = "600000008354";
		String payCode3 = "300008354001";
		
		String str3 = "http://wap.dm.10086.cn/apay/monthlyOrderHandle.jsp?RequestID=" +  requestID3;
			   str3 += "&AppID=" + appId3;
			   str3 += "&PayCode=" + payCode3 + "&TimeStamp=" + timeStamp + "&ChannelID=" + channelId;
			   
		String needSign3 = requestID3 + "&" + appId3 + "&ZNVASA479Q9XSITG&" + payCode3 + "&" + timeStamp + "&" + channelId;
		String signature3 = Base64Util.encode(MD5Util.getMD5(needSign3).getBytes());
		
		str3 += "&Signature=" + signature3;
		
		request.setAttribute("str3", str3);
		
		String requestID4 = "600000008355" + id; 
		String appId4 = "600000008355";
		String payCode4 = "300008355001";
		
		String str4 = "http://wap.dm.10086.cn/apay/monthlyOrderHandle.jsp?RequestID=" +  requestID4;
			   str4 += "&AppID=" + appId4;
			   str4 += "&PayCode=" + payCode4 + "&TimeStamp=" + timeStamp + "&ChannelID=" + channelId;
			   
		String needSign4 = requestID4 + "&" + appId4 + "&YVLFZBF2NG109OZ0&" + payCode4 + "&" + timeStamp + "&" + channelId;
		String signature4 = Base64Util.encode(MD5Util.getMD5(needSign4).getBytes());
		
		str4 += "&Signature=" + signature4;
		
		request.setAttribute("str4", str4);
		
		return new ModelAndView("/url/ethems.vm");
		
	}
	
	@RequestMapping("/ddoMonthOrder.do")
	public ModelAndView ddoMonthOrder(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("汉娱ddo包月 order: " + requestStr);
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "phone", null);
			String chid = RequestUtil.GetParamString(request, "chid", null);
			
			ReceiveSms receiveSms = new ReceiveSms();
			Long id = orderService.getNextOrderId();
			receiveSms.setHret(HRET_FIRST);
			
			StringBuffer param = new StringBuffer();
			param.append("pid=");
			param.append(feecode);
			param.append("&mobileno=");
			param.append(phone);
			param.append("&chid=");
			param.append(feecode);
			param.append("&cm=");
			param.append(chid);
			param.append("&cpparam=");
			param.append(cpparam);
			
			String bag = HttpTookit.doGet("http://115.28.252.55:13888/cmddo/init?" + param.toString(), null, "utf-8", false);
			
			logger.info("hy ddo month req : " + param.toString() + ",bag resp is : " + bag);
			
			if (bag != null) {
				JSONObject jsObject = JSONObject.parseObject(bag);
				
				String result = jsObject.getString("result");
				String orderid = jsObject.getString("orderid");
				
				receiveSms.setExt(orderid);
				
				if ("0".equals(result)) {
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("orderid", id);
				} else {
					receiveSms.setHret(result);
				}
			}
			
			String province = getStateByMobile(phone);
			province = GetState(province);
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setStatus(STATUS_FIRST);
			receiveSms.setTransido(String.valueOf(System.currentTimeMillis()));
			receiveSms.setVersionid("none");
			receiveSms.setPackageid("1000");
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(Integer.parseInt(chid));
			receiveSms.setFee(0);
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phone);
			
			orderService.createReceiveSms(receiveSms);
			
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ddoMonthOrder error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/ddoMonthVerifyCode.do")
	public ModelAndView ddoMonthVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("汉娱ddo 包月 短信验证请求参数为: " + requestStr);
			
			String verifyCode = RequestUtil.GetParamString(request, "verifycode", null);
			Long orderId = RequestUtil.GetParamLong(request, "orderid", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderId));
			
			if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
				receiveSms.setHret(HRET_SECOND);
				
				StringBuffer param = new StringBuffer();
				param.append("smscode=");
				param.append(verifyCode);
				param.append("&orderid=");
				param.append(receiveSms.getExt());
				
				String bag = HttpTookit.doGet("http://115.28.252.55:13888/cmddo/pay?" + param.toString(), null, "utf-8", false);
				
				logger.info("ddoMonthVerifyCode resp :" + bag);
				
				if (bag != null) {
					JSONObject jsObject = JSONObject.parseObject(bag);
					
					String result = jsObject.getString("result");
					
					if ("0".equals(result)) {
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
			logger.error("ddoMonthVerifyCode error : " + e.getMessage());
		}
		return null;
	}
	
//	@RequestMapping("/etNotify.do")
//	public ModelAndView notify(HttpServletRequest request, HttpServletResponse response) {
//		PrintWriter pw = null;
//		
//		StringBuffer logBuffer = new StringBuffer();
//		
//		String respStr = "";
//		
//		try {
//			InputStream in = request.getInputStream();
//			byte b[] = new byte[10000];
//			in.read(b, 0, 10000);
//			String requestStr = new String(b, "UTF-8");
//			in.close();
//			pw = response.getWriter();
//			
//			if(null != requestStr && !"".equals(requestStr)){
//				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
//				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
//			}
//			
//			logBuffer.append("汉娱DDO包月数据同步请求参数：" + requestStr);
//			
//			Document document = DocumentHelper.parseText(requestStr);
//			Element root = document.getRootElement();
//			
//			String orderId = root.elementText("OrderID");
//			String RentSuccess = root.elementText("RentSuccess");
//			
//			respStr = "<Response><MsgType> MonthlyOrderNotifyResp</MsgType><Result>0</Result><ErrorMsg>ok</ErrorMsg></Response>";
//			
//			if (true) {
//				ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderId);
//				
//				if (receiveSms != null) {
//					String today = DateTool.getCurrentDate("yyyyMMdd");
//					
//					if ("0".equals(RentSuccess)) {
//						receiveSms.setStatus(STATUS_SUCCESS);
//						receiveSms.setHret(HRET_SUCCESS);
//					} else {
//						receiveSms.setStatus(RentSuccess);
//						receiveSms.setHret(HRET_THIRD);
//					}
//					
//					ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(receiveSms.getConsumecode());
//					
//					if (consumeInfo != null) {
//						receiveSms.setFee(consumeInfo.getFee());
//						
//						List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
//						
//						boolean flag = false;
//						
//						if (chnlResourceList != null && chnlResourceList.size() > 0) {
//							for (ChnlResource chnlResource : chnlResourceList) {
//								
//								if (receiveSms.getChnl_id() == chnlResource.getChnlId().intValue()) {
//									List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), receiveSms.getState());
//									
//									if (maskList != null && maskList.size() > 0) {
//										receiveSms.setChnl_id(chnlResource.getChnlId().intValue());
//										flag = true;
//										break;
//									}
//								}
//							}
//						}
//						
//						if (!flag) {
//							receiveSms.setChnl_id(-1);
//						}
//					} else {
//						receiveSms.setChnl_id(-1);
//					}
//					
//					//扣量验证
//					int dec_flag = 0;
//					
//						if (receiveSms.getChnl_id() > 0) {
//							dec_flag = shouldKouliangByState(today, receiveSms.getChnl_id(), receiveSms.getConsumecode(), receiveSms.getState(), receiveSms.getPackageid());
//							
//							receiveSms.setDec_flag(dec_flag);
//							
//							if (dec_flag == 1) {
//								receiveSms.setSyncchnl(200);
//							} else {
//								receiveSms.setSyncchnl(0);
//							}
//						}
//					
//					orderService.updateReceiveSms(receiveSms);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			respStr = "error";
//			logger.error("etNotify error :" + e.getMessage());
//		}
//		
//		logBuffer.append(", 服务端响应为： " + respStr);
//		logger.info(logBuffer.toString());
//		
//		pw.print(respStr);
//		return null;
//	}

}
