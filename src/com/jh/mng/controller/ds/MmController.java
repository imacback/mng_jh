package com.jh.mng.controller.ds;

import java.io.IOException;
import java.io.InputStream;
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
import com.jh.mng.common.Config;
import com.jh.mng.common.StrIntBag;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.Mask;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.Methods;
import com.jh.mng.util.RequestUtil;

/**
 * MM破解 （反恐女神）
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class MmController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(MmController.class);
	
	@RequestMapping("/mmOrder.do")
	public ModelAndView mmOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("MM业务新订单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String imsi = RequestUtil.GetParamString(request, "imsi", null);
//			String app_id = RequestUtil.GetParamString(request, "app_id", null);
			String ip = RequestUtil.GetParamString(request, "ip", null);
//			String gps_lng = RequestUtil.GetParamString(request, "gps_lng", null);
//			String gps_lat = RequestUtil.GetParamString(request, "gps_lat", null);
//			String proStr = RequestUtil.GetParamString(request, "province", null);
//			String city = RequestUtil.GetParamString(request, "city", null);
//			String bsc_lac = RequestUtil.GetParamString(request, "bsc_lac", null);
//			String bsc_cid = RequestUtil.GetParamString(request, "bsc_cid", null);
			String phone = RequestUtil.GetParamString(request, "mobile", null); 
			String channel_id = RequestUtil.GetParamString(request, "channelid", null);
			String appId = RequestUtil.GetParamString(request, "appid", null);
			
			String province = "00";
			
			if (StringUtils.isNotEmpty(phone)) {
				province = getStateByMobile(phone);
			} else {
				phone = "none";
				province = cpparam.substring(2,4);
			}
			province = super.GetState(province);
			
			if ("未知".equals(province)) {
				province = Methods.getProvinceByIp(ip);
			}
			
			Integer channelId = Integer.parseInt(cpparam.substring(0, 2));
			
			ReceiveSms receiveSms = new ReceiveSms();
			Long id = orderService.getNextOrderId();
			
			StringBuffer sb = new StringBuffer();
			sb.append("imei=");
			sb.append(imei);
			sb.append("&imsi=");
			sb.append(imsi);
			sb.append("&sid=");
			sb.append(id);
			sb.append("&paycode=");
			sb.append(feecode);
			sb.append("&app_id=");
			sb.append(appId);
			sb.append("&channel_id=");
			sb.append(channel_id);
			sb.append("&ip=");
			sb.append(ip);
//			sb.append("&gps_lng=");
//			sb.append(gps_lng);
//			sb.append("&gps_lat=");
//			sb.append(gps_lat);
//			sb.append("&province=");
//			sb.append(proStr);
//			sb.append("&city=");
//			sb.append(city);
//			sb.append("&bsc_lac=");
//			sb.append(bsc_lac);
//			sb.append("&bsc_cid=");
//			sb.append(bsc_cid);
			
			StrIntBag strIntBag = HttpClientUtil.executeGet("http://121.14.38.20:25494/if_mtk/service?operation=102&" + sb.toString());
			
			logger.info("mm new req: " + sb.toString() + ", resp is :" + strIntBag);
			
			String smsnum = "";
//			String sms = "";
			String result = "";
			String status = STATUS_SECOND;
			
			
			if (strIntBag != null && StringUtils.isNotEmpty(strIntBag._str)) {
				JSONObject json = JSONObject.parseObject(strIntBag._str);
				
				result = json.getString("result");
				String smsport = json.getString("smsport");
				String smsmsg = json.getString("smsmsg");
				String smsbin = json.getString("smsbin");
				
				if ("0".equals(result)) {
					smsnum = smsport;
					
					if (StringUtils.isNotEmpty(smsmsg)) {
						jObject.put("smsmsg", smsmsg);
					} else if (StringUtils.isNotEmpty(smsbin)) {
						jObject.put("smsbin", smsbin);
					}
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("smsnum", smsnum);
					jObject.put("orderid", id);
				} else if ("1".equals(result)) {
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("orderid", id);
				} else {
					status = result;
				}
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid(imsi);
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			
//			if (StringUtils.isNotEmpty(smsnum) && StringUtils.isNotEmpty(sms)) {
//				
//				receiveSms.setStatus(STATUS_SECOND);
//			} else {
//				receiveSms.setHret(HRET_FIRST);
//				receiveSms.setStatus(STATUS_FIRST);
//			}
			receiveSms.setHret(HRET_SECOND);
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
			
			
			orderService.createReceiveSms(receiveSms);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mmOrder error : " + e.getMessage());
		}
		logBuffer.append(", 服务端响应为：" + jObject.toString());
		logger.info(logBuffer.toString());
		
		pw.print(jObject.toString());
		
		return null;
	}

	@RequestMapping("/mmNotify.do")
	public ModelAndView mmNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter pw = null;
		
		InputStream in = request.getInputStream();
		byte b[] = new byte[10000];
		in.read(b, 0, 10000);
		String requestStr = new String(b, "UTF-8");
		in.close();
		
		boolean chnlflag = false;
		
		StringBuffer logBuffer = new StringBuffer();
		
		String respStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SyncAppOrderResp xmlns=\" http://www.monternet.com/dsmp/schemas/\"><TransactionID>%s</TransactionID><MsgType>SyncAppOrderResp</MsgType><Version>1.0.0</Version><hRet>0</hRet></SyncAppOrderResp>";
		
		try {
			pw = response.getWriter();
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document doc = DocumentHelper.parseText(requestStr);
			Element root = doc.getRootElement();
			
			String orderId = root.elementText("OrderID");
			String exData = root.elementText("ExData");
			String channelId = root.elementText("ChannelID");
			String transactionID = root.elementText("TransactionID");
			String payCode = root.elementText("PayCode");
			String returnStatus = root.elementText("ReturnStatus");
			String provinceID = root.elementText("ProvinceID");
			
			respStr = String.format(respStr, transactionID);
			
			String mmchId = Config.get().get("mm_channelid");
			
			String[] chIds = mmchId.split(",");
			
			for (int i = 0; i < chIds.length; i++) {
				if (chIds[i].equals(channelId)) {
					chnlflag = true;
					break;
				}
			}
			
			if (chnlflag) {
				logBuffer.append("MM业务数据数据同步请求参数：" + requestStr);
					ReceiveSms receiveSms = orderService.queryReceiveSmsById(exData);
					
					if (receiveSms != null) {
						String today = DateTool.getCurrentDate("yyyyMMdd");
						
						if (!"00000000000000000000".equals(orderId)) {
							receiveSms.setStatus(STATUS_SUCCESS);
							receiveSms.setHret(HRET_SUCCESS);
						} else {
							receiveSms.setStatus(returnStatus != null ? returnStatus : STATUS_FAILED);
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
					}  else {
						
						List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByTransido(transactionID);
						
						if (receiveSmsList != null && receiveSmsList.size() > 0) {
							logger.error("============repeatId==================:" + transactionID );
							pw.print(respStr);
							return null;
						}
						
						ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(payCode);
						
						ReceiveSms newReceiveSms = new ReceiveSms();
						Long id = orderService.getNextOrderId();
						
						newReceiveSms.setId(id);
						newReceiveSms.setUserid("none");
						newReceiveSms.setCpserviceid("none");
						newReceiveSms.setConsumecode(payCode);
						newReceiveSms.setCpparam("none");
						
						if (!"00000000000000000000".equals(orderId)) {
							newReceiveSms.setStatus(STATUS_SUCCESS);
							newReceiveSms.setHret(HRET_SUCCESS);
						} else {
							newReceiveSms.setStatus(returnStatus != null ? returnStatus : STATUS_FAILED);
							newReceiveSms.setHret(HRET_FAILED);
						}
						
						newReceiveSms.setTransido(transactionID);
						newReceiveSms.setVersionid("none");
						newReceiveSms.setPackageid("1000");
						newReceiveSms.setSyncflag(0);
						newReceiveSms.setSyncchnl(0);
						newReceiveSms.setChnl_id(-1);
						newReceiveSms.setFee(consumeInfo.getFee());
						newReceiveSms.setDec_flag(1);
						
						provinceID = this.GetState(provinceID);
						
						newReceiveSms.setState(provinceID);
						newReceiveSms.setPhoneno("none");
						
						orderService.createReceiveSms(newReceiveSms);
					}
				HttpClientUtil.execute("http://121.14.38.20:25498/iap/SyncAppOrderReq", requestStr);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			respStr = "error";
			logger.error("mmNotify error :" + e.getMessage());
		}
		
		if (chnlflag) {
			logBuffer.append(", 服务端响应为： " + respStr);
			logger.info(logBuffer.toString());
		}
		
		pw.print(respStr);
		
		return null;
	}
	
	String localStates[] = {
			"北京", "上海","天津","重庆","黑龙江",
			"吉林","辽宁","内蒙古","河北","河南",
			"广东","湖北","山东","浙江","安徽",
			"江苏","江西","云南","宁夏","青海",
			"山西","陕西","湖南","福建","甘肃",
			"四川","广西","贵州","海南","西藏","新疆"
			};

	public String GetState(String idx) {
		String ret = "未知";
		try {
			int i = Integer.parseInt(idx);
			if (i > 0 && i < 32) {
				ret = localStates[i - 1];
			}
		} catch (Exception e) {
		}

		return ret;
	}
	

	
}
