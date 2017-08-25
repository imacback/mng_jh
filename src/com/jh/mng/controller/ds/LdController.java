package com.jh.mng.controller.ds;

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
import com.jh.mng.util.Base64Util;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.HttpClientUtil;
import com.jh.mng.util.RequestUtil;


/**
 * 雷电3D
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class LdController extends AbstractMultiActionController {
	
	private final static Logger logger = Logger.getLogger(LdController.class);

	@RequestMapping("/ldLogin.do")
	public ModelAndView ldLogin(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		logger.info("雷电3D登录请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String consumecode = RequestUtil.GetParamString(request, "consumecode", null);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String imsi = RequestUtil.GetParamString(request, "imsi", null);
			String mobile = RequestUtil.GetParamString(request, "mobile", null);
			String brand = RequestUtil.GetParamString(request, "brand", null);
			String model = RequestUtil.GetParamString(request, "model", null);
			String osbuild = RequestUtil.GetParamString(request, "osbuild", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(consumecode);
			
			Long id = orderService.getNextOrderId();
			
			JSONObject reqJson = new JSONObject();
			reqJson.put("ssid", consumeInfo.getFeeurl());
			reqJson.put("imei", imei);
			reqJson.put("imsi", imsi);
			reqJson.put("brand", brand);
			reqJson.put("model", model);
			reqJson.put("osbuild", osbuild);
			
			StrIntBag resp = HttpClientUtil.executeJson(Config.get().get("loginurl_ld"), reqJson.toJSONString());

			logger.info("ld login req: " + reqJson.toJSONString() + ", resp :" + resp.toString());
			
			if (resp != null && resp._str != null) {
				JSONObject respObject = JSONObject.parseObject(resp._str);
				
				String status = respObject.getString("status");
				String init_dst = respObject.getString("init_dst");
				String init_b64 = respObject.getString("init_b64");
				
				if ("100".equals(status)) { //成功
					init_b64 = new String(Base64Util.decode(init_b64));
					
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("orderid", id);
					jObject.put("dst", init_dst);
					jObject.put("message", init_b64);
				} else {
					jObject.put("status", status);
				}
			}

			String transido = "";
			
			//生成本地订单
			String userid = mobile;
			String cpserviceid = "none";
			String hret = HRET_FIRST;
			String versionid = "none";
			
			if (StringUtils.isEmpty(transido)) {
				transido = String.valueOf(System.currentTimeMillis());
			}
			
			String phoneno = mobile;
			String packageid = "1000";
			String subchannel = cpparam.substring(0, 1);
			int channel = getChnl(subchannel);
			String province = "未知";
			
			//XXX zm6渠道使用5位自定义参数
			
			if (23 != channel && 6 != channel) {
				if (StringUtils.isEmpty(mobile)) {
					province = cpparam.substring(1, 2);
					province = getState(province);
				} else {
//					province = Methods.request(mobile);
					province = getStateByMobile(phoneno);
					province = GetState(province);
				}
			} else {
				province = "北京";
			}
			
			if (StringUtils.isEmpty(userid)) {
				userid = "none";
			}
			
			if (StringUtils.isEmpty(phoneno)) {
				phoneno = "none";
			}
			
			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setId(id);
			receiveSms.setUserid(userid);
			receiveSms.setCpserviceid(cpserviceid);
			receiveSms.setConsumecode(consumecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(hret);
			
			if (jObject.get("status").toString().equals("100")) {
				receiveSms.setStatus(STATUS_FIRST);  //登录成功
			} else {
				receiveSms.setStatus(jObject.get("status").toString());
			}
			receiveSms.setTransido(transido);
			receiveSms.setVersionid(versionid);
			receiveSms.setPackageid(packageid);
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(channel);
			receiveSms.setFee(consumeInfo.getFee());
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phoneno);
			receiveSms.setExt(imsi);
			
			orderService.createReceiveSms(receiveSms);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ldLogin error : " + e.getMessage());
		}
		pw.print(jObject.toString());
		logger.info("服务端响应 ：" + jObject.toString());
		return null;
	}
	
	@RequestMapping("/ldFee.do")
	public ModelAndView ldFee(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject respJson = new JSONObject();
			respJson.put("status", STATUS_RESP_FAILED);
//			respJson.put("msg", "失败");
			
			String requestStr = request.getQueryString();
			
			logger.info("雷电3D计费请求参数： " + requestStr);
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			
			if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
				receiveSms.setHret(HRET_SECOND);
				ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(receiveSms.getConsumecode());
//				
//				int length = orderid.length();
//				if (length < 16 ) {
//					for (int i = 0; i < 16 - length; i++) {
//						orderid = "0" + orderid;
//					}
//				}
				
				JSONObject jObject = new JSONObject();
				jObject.put("ssid", consumeInfo.getFeeurl());
				jObject.put("cpparam", "0000000000" + receiveSms.getCpparam());
				jObject.put("imsi", receiveSms.getExt());
				
				StrIntBag resp = HttpClientUtil.executeJson(Config.get().get("feeurl_ld"), jObject.toJSONString());
				
				logger.info("ldFee request : " + jObject.toJSONString() + ", resp: " + resp);
				
				if (resp != null && resp._str != null) {
					JSONObject respObject = JSONObject.parseObject(resp._str);
					
					String status = respObject.getString("status");
					String billing_dst = respObject.getString("billing_dst");
					String billing_b64 = respObject.getString("billing_b64");
					
					if ("100".equals(status)) { //成功
						billing_b64 = new String(Base64Util.decode(billing_b64));
						respJson.put("status", STATUS_RESP_SUCCESS);
						respJson.put("dst", billing_dst);
						respJson.put("message", billing_b64);
						receiveSms.setStatus(STATUS_SECOND); //计费短信发送成功
					} else {
						respJson.put("status", status);
						receiveSms.setStatus(status); //计费短信发送失败
					}
				}
				
				orderService.updateReceiveSms(receiveSms);
			}
			pw.print(respJson.toString());
			logger.info("服务端响应 ：" + respJson.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ldFee error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/ldReport.do")
	public ModelAndView ldReport(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
//			int n = in.read(b, 0, 10000);
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			
			logger.info("雷电3D数据同步请求参数为：" + requestStr);
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
			String userid = root.elementText("userId");
			String cpserviceid = root.elementText("contentId");
			String consumecode = root.elementText("consumeCode");
			String versionid = root.elementText("versionId");
			String cpparam = root.elementText("cpparam");
			String status = root.elementText("status");
			String hret = root.elementText("hRet");
			
			String transido = String.valueOf(System.currentTimeMillis());
			int chnl_id = -1;
			int dec_flag = 0;
			String packageid = "1000";
			String phoneno = "";
			
			int channelId = getChnl((cpparam.substring(10).substring(0, 1)));
			//XXX zm6渠道使用5位自定义参数
			
			String province = "未知";
			
			if (channelId != 23 && channelId != 6) {
				province = cpparam.substring(10).substring(1, 2);
				province = getState(province);
			} else {
				province = "北京";
			}
			
			if (userid == null || "".equals(userid)) {
				userid = "none";
			}
			
			if (phoneno == null || "".equals(phoneno)) {
				phoneno = "none";
			}
			
			if (hret != null && "0".equals(hret) && status != null && "1800".equals(status)) {
				hret = HRET_SUCCESS;
				status = STATUS_SUCCESS;
			} 
			
			
			int fee_local = -1;
			int over_arpu = 0;
			int sync_cpflag = 0;
			int sync_chnlflag = 0;
			
			String today = DateTool.getCurrentDate("yyyyMMdd");
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(consumecode);
			
			if (consumeInfo != null) {
				fee_local = consumeInfo.getFee();
				
				List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
				
				boolean flag = false;
				
				if (chnlResourceList != null && chnlResourceList.size() > 0) {
					for (ChnlResource chnlResource : chnlResourceList) {
						
						if (channelId == chnlResource.getChnlId().intValue()) {
							List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), province);
							
							if (maskList != null && maskList.size() > 0) {
								chnl_id = channelId;
								flag = true;
								break;
							}
						}
					}
				}
				
				if (!flag) {
					chnl_id = -1;
				}
			} else {
				chnl_id = -1;
			}
			//超限验证
			over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_LD", 1000000), Config.get().getInt("MAX_Y_FEE_LD", 1000000), today, userid, fee_local);
			
			//扣量验证
			
			if (over_arpu == 0) {
				if (chnl_id > 0) {
					dec_flag = shouldKouliangByState(today, chnl_id, consumecode, province, packageid);
					
					if (dec_flag == 1) {
						sync_chnlflag = 200;
					} else {
						sync_chnlflag = 0;
					}
				}
				
			} else {
				sync_chnlflag = 300;
				dec_flag = 1;
			}
			
			Long id = orderService.getNextOrderId();
			
			ReceiveSms newSms = new ReceiveSms();
			newSms.setId(id);
			newSms.setUserid(userid);
			newSms.setCpserviceid(cpserviceid);
			newSms.setConsumecode(consumecode);
			newSms.setCpparam(cpparam);
			newSms.setHret(hret);
			newSms.setStatus(status);
			newSms.setTransido(transido);
			newSms.setVersionid(versionid);
			newSms.setPackageid(packageid);
			newSms.setSyncflag(sync_cpflag);
			newSms.setSyncchnl(sync_chnlflag);
			newSms.setChnl_id(chnl_id);
			newSms.setFee(fee_local);
			newSms.setDec_flag(dec_flag);
			newSms.setState(province);
			newSms.setPhoneno(phoneno);
			
			orderService.createReceiveSms(newSms);
			
			String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><hRet>0</hRet>< message>successful</message></response>";
			pw.print(resp);
			
//			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(Integer.parseInt(cpparam)));
//			
//			if (receiveSms != null) {
//				int fee = -1;
//				int over_arpu = 0;
//				String today = DateTool.getCurrentDate("yyyyMMdd");
//				
//				if (hret != null && "0".equals(hret) && status != null && "1800".equals(status)) {
//					receiveSms.setStatus("1101");
//					receiveSms.setHret("0");
//				} else {
//					receiveSms.setStatus(status);
//					receiveSms.setHret("-1");
//				}
//				
//				ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(receiveSms.getConsumecode());
//				
//				if (consumeInfo != null) {
//					receiveSms.setFee(consumeInfo.getFee());
//					
//					List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
//					
//					boolean flag = false;
//					
//					if (chnlResourceList != null && chnlResourceList.size() > 0) {
//						for (ChnlResource chnlResource : chnlResourceList) {
//							
//							if (receiveSms.getChnl_id() == chnlResource.getChnlId().intValue()) {
//								List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), receiveSms.getState());
//								
//								if (maskList != null && maskList.size() > 0) {
//									receiveSms.setChnl_id(chnlResource.getChnlId().intValue());
//									flag = true;
//									break;
//								}
//							}
//						}
//					}
//					
//					if (!flag) {
//						receiveSms.setChnl_id(-1);
//					}
//				} else {
//					receiveSms.setChnl_id(-1);
//				}
//				//超限验证
//				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_LD", 1000000), Config.get().getInt("MAX_Y_FEE_LD", 1000000), today, receiveSms.getUserid(), fee);
//				
//				//扣量验证
//				int dec_flag = 0;
//				
//				if (over_arpu == 0) {
//					if (receiveSms.getChnl_id() > 0) {
//						dec_flag = shouldKouliangByState(today, receiveSms.getChnl_id(), receiveSms.getConsumecode(), receiveSms.getState(), receiveSms.getPackageid());
//						
//						receiveSms.setDec_flag(dec_flag);
//						
//						if (dec_flag == 1) {
//							receiveSms.setSyncchnl(200);
//						} else {
//							receiveSms.setSyncchnl(0);
//						}
//					}
//					
//				} else {
//					receiveSms.setSyncchnl(300);
//					receiveSms.setDec_flag(1);
//				}
//				
//				orderService.updateReceiveSms(receiveSms);
//				
//				String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><hRet>0</hRet>< message>successful</message></response>";
//				pw.print(resp);
//			} else {
//				String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><hRet>1</hRet>< message>failure</message></response>";
//				pw.print(resp);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ldReport error : " + e.getMessage());
			String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><hRet>1</hRet>< message>failure</message></response>";
			pw.print(resp);
		}
		return null;
	}
}
