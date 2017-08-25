package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
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
import com.jh.mng.util.HttpTookit;
import com.jh.mng.util.RequestUtil;

/**
 * 南京修邦信息科技有限公司
 * RDO业务
 * @author T410
 *
 */
@Controller
@RequestMapping("/ds")
public class XiuBangController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(XiuBangController.class);
	/**
	 * 修邦RDO下订单请求
	 * @param request
	 * @param response
	 * @return
	 * @throws DocumentException 
	 */
	@RequestMapping("/xbOrder.do")
	public ModelAndView xbOrder(HttpServletRequest request, HttpServletResponse response){
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", -1);
			
			String requestStr = request.getQueryString();
			
			logger.info("new xb order: " + requestStr);
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "phone", null);
			String cm = Config.get().get("cm_xb");
			String mcpid = Config.get().get("mcpid_xb");
			
//			String province = Methods.request(phone);
			
			String province = getStateByMobile(phone);
			province = GetState(province);
//			
//			if ("未知".equals(province)) {
//				province = getStateByMobile(phone);
//				province = GetState(province);
//			}
			String chnlId = cpparam.substring(0,2);
			
			ReceiveSms receiveSms = new ReceiveSms();
			
			Long id = orderService.getNextOrderId();
			String reqTime = DateTool.getCurrentDate("yyyyMMddHHmmss");
			String layout = "9";
			
			StringBuffer signStr = new StringBuffer();
			signStr.append(mcpid);
			signStr.append(feecode);
			signStr.append("hy" + id);
			signStr.append(reqTime);
			
			logger.info("need sign str: " + signStr.toString());
			
			String sign = DigestUtils.md5Hex((signStr.toString() + Config.get().get("key_xb")).getBytes("UTF-8")).toUpperCase();
			
			StringBuffer param = new StringBuffer();
			param.append("mcpid=");
			param.append(mcpid);
			param.append("&orderNo=");
			param.append("hy" + id);
			param.append("&feeCode=");
			param.append(feecode);
			param.append("&reqTime=");
			param.append(reqTime);
			param.append("&sign=");
			param.append(sign);
			param.append("&layout=");
			param.append(layout);
			param.append("&cm=");
			param.append(cm);
			
//			StrIntBag bag = HttpClientUtil.execute(Config.get().get("kdorderurl") + "?" + signStr.toString() , null);
			StrIntBag bag = HttpClientUtil.executeGet(Config.get().get("orderurl_xb")+ "?" + param.toString());
			
			logger.info("xb req : " + param.toString() + ",bag resp is : " + bag);
			
			if (bag != null && bag._str != null) {
				Document doc = DocumentHelper.parseText(bag._str);
				Element root = doc.getRootElement();
				
				String resultCode = root.elementText("ResultCode");
				
				if (resultCode != null && resultCode.equals("200")) {
					String verifType = root.element("Order").elementText("VerifType");
					
					if ("0".equals(verifType)) { //短信验证
						String getSMSVerifyCodeUrl = root.element("Order").element("Submit0").elementText("GetSMSVerifyCodeUrl");
						
						if (StringUtils.isNotEmpty(getSMSVerifyCodeUrl)) {
							getSMSVerifyCodeUrl += "&msisdn=" + phone;
							
							//获取短信验证码
							StrIntBag verifyCodeBag = HttpClientUtil.executeGet(getSMSVerifyCodeUrl);
							
							logger.info("verifyCodeBag resp : " + verifyCodeBag);
							
							if (verifyCodeBag != null && verifyCodeBag._str != null) {
								Document document = DocumentHelper.parseText(verifyCodeBag._str);
								Element root_code = document.getRootElement();
								
								String resultCode_code = root_code.elementText("ResultCode");
								
								if (resultCode_code != null && "200".equals(resultCode_code)) { //成功
									String submitUrl = root_code.element("Order").element("Submit0").element("ButtonTag").elementText("SubmitUrl");
									jObject.put("status", 0);
									jObject.put("orderid", id);
									receiveSms.setExt(submitUrl);
								}
							}
						}
					}
				}
			}
			
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret("none");
			receiveSms.setStatus("-1");
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
			logger.error("xbOrder error : " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 短证码确认
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/xbVerifyCode.do")
	public ModelAndView xbVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", "1");
			
			String verifyCode = RequestUtil.GetParamString(request, "verifyCode", null);
			Long orderId = RequestUtil.GetParamLong(request, "orderId", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderId));
			
			if (receiveSms != null) {
				String ext = receiveSms.getExt();
				logger.info("xbVerifyCode req: " + ext + "&verifyCode=" + verifyCode);
				
				NameValuePair param = new NameValuePair("verifyCode", verifyCode);
		        NameValuePair[] data = {param}; 
		    	
		    	String confirmResp = HttpTookit.methodPost(ext, data);
				
				logger.info("xbVerifyCode resp :" + confirmResp);
			}
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("xbVerifyCode error : " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 修邦同步
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/xbReport.do")
	public ModelAndView xbReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			String req = request.getQueryString();
			
			logger.info("修邦数据同步请求参数为 :" + req);
			
			String mcpid = request.getParameter("mcpid");
			String feeCode = request.getParameter("feeCode");
			String orderNo = request.getParameter("orderNo");
			String reqTime = request.getParameter("reqTime");
			String resultCode = request.getParameter("resultCode");
			String sign = request.getParameter("sign");
			
			StringBuffer signStr = new StringBuffer();
			signStr.append(mcpid);
			signStr.append(feeCode);
			signStr.append(orderNo);
			signStr.append(reqTime);
			
			String localSign = DigestUtils.md5Hex((signStr.toString() + Config.get().get("key_xb")).getBytes("UTF-8")).toUpperCase();
			
			if (localSign.equals(sign)) {
				ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderNo.substring(2));
				
				if (receiveSms != null) {
					int fee = -1;
					int over_arpu = 0;
					String today = DateTool.getCurrentDate("yyyyMMdd");
					
					if (resultCode != null && "1".equals(resultCode)) {
						receiveSms.setStatus("1101");
						receiveSms.setHret("0");
					} else {
						receiveSms.setStatus(resultCode);
						receiveSms.setHret("-1");
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
					over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_XB", 1000000), Config.get().getInt("MAX_Y_FEE_XB", 1000000), today, receiveSms.getUserid(), fee);
					
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
//					else {
//					String userId = "none";
//					String cpserviceid = "none";
//					String consumecode = feeCode;
//					String cpparam = "";
//					String hret = "none";
//					String status = "-1";
//					String versionid = "none";
//					String transido = String.valueOf(System.currentTimeMillis());
//					int chnl_id = -1;
//					int dec_flag = 0;
//					String province = "";
//					String packageid = "1000";
//					String phoneno = "";
//					
//					phoneno = "";
//					String channelId = cpparam.substring(0, 2);
//					province = cpparam.substring(2, 4);
//					
//					province = GetState(province);
//					
//					if (userId == null || "".equals(userId)) {
//						userId = "none";
//					}
//					
//					if (phoneno == null || "".equals(phoneno)) {
//						phoneno = "none";
//					}
//					
//					if ("1".equals(resultCode)) {
//						hret = "0";
//						status = "1101";
//					}
//					
//					
//					int fee_local = -1;
//					int over_arpu = 0;
//					int sync_cpflag = 0;
//					int sync_chnlflag = 0;
//					
//					String today = DateTool.getCurrentDate("yyyyMMdd");
//					
//					ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(feeCode);
//					
//					if (consumeInfo != null) {
//						fee_local = consumeInfo.getFee();
//						
//						List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
//						
//						boolean flag = false;
//						
//						if (chnlResourceList != null && chnlResourceList.size() > 0) {
//							for (ChnlResource chnlResource : chnlResourceList) {
//								
//								if (chnl_id == chnlResource.getChnlId().intValue()) {
//									List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), province);
//									
//									if (maskList != null && maskList.size() > 0) {
//										chnl_id = Integer.parseInt(channelId);
//										flag = true;
//										break;
//									}
//								}
//							}
//						}
//						
//						if (!flag) {
//							chnl_id = -1;
//						}
//					} else {
//						chnl_id = -1;
//					}
//					//超限验证
//					over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_XB", 1000000), Config.get().getInt("MAX_Y_FEE_XB", 1000000), today, userId, fee_local);
//					
//					//扣量验证
//					
//					if (over_arpu == 0) {
//						if (chnl_id > 0) {
//							dec_flag = shouldKouliangByState(today, chnl_id, feeCode, province, packageid);
//							
//							if (dec_flag == 1) {
//								sync_chnlflag = 200;
//							} else {
//								sync_chnlflag = 0;
//							}
//						}
//						
//					} else {
//						sync_chnlflag = 300;
//						dec_flag = 1;
//					}
//					
//					Long id = orderService.getNextOrderId();
//					
//					ReceiveSms newSms = new ReceiveSms();
//					newSms.setId(id);
//					newSms.setUserid(userId);
//					newSms.setCpserviceid(cpserviceid);
//					newSms.setConsumecode(consumecode);
//					newSms.setCpparam(cpparam);
//					newSms.setHret(hret);
//					newSms.setStatus(status);
//					newSms.setTransido(transido);
//					newSms.setVersionid(versionid);
//					newSms.setPackageid(packageid);
//					newSms.setSyncflag(sync_cpflag);
//					newSms.setSyncchnl(sync_chnlflag);
//					newSms.setChnl_id(chnl_id);
//					newSms.setFee(fee_local);
//					newSms.setDec_flag(dec_flag);
//					newSms.setState(province);
//					newSms.setPhoneno(phoneno);
//					
//					orderService.createReceiveSms(receiveSms);
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("xbReport error : " + e.getMessage());
		}
		return null;
	}
}
