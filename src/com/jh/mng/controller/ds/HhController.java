package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.jh.mng.util.RequestUtil;

@Controller
@RequestMapping("/ds")
public class HhController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(HhController.class);
	
	/**
	 * 恒华订单请求
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/hhOrder.do")
	public ModelAndView hhOrder(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			
			logger.info("恒华RDO订单请求参数为：" + request.getQueryString());
			
			pw = response.getWriter();
			
			Long id = orderService.getNextOrderId();
			
			String mcpid = request.getParameter("mcpid");
			String feeCode = request.getParameter("feecode");
			String cm = request.getParameter("cm");
			String channelid = request.getParameter("channelid");
			String mobile = request.getParameter("mobile");
			String cpparam = request.getParameter("cpparam");
//			String outorderid = request.getParameter("outorderid");
			
			String url = Config.get().get("hhOrderUrl");
			String ip = request.getRemoteAddr();
			
			String requestStr = "mcpid=" + mcpid ;
			requestStr += "&feeCode=" + feeCode;
			requestStr += "&cm=" + cm;
			requestStr += "&channelid=" + channelid;
			requestStr += "&mobile=" + mobile;
			requestStr += "&outorderid=" + id;
			requestStr += "&clientip=" + ip;
//			requestStr += "&clientip=101.201.148.52";
			
			
			StrIntBag str = HttpClientUtil.executeGet(url + "?" + requestStr);
			
			logger.info("hhOrderurl : " + url + ", resquestStr: " + requestStr + " , resp: " + str);
			
			if (str != null && StringUtils.isNotEmpty(str._str)) {
				JSONObject respObject = JSONObject.parseObject(str._str);
				
				int code = respObject.getIntValue("Code");
				
				if (code == 0) {
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("orderid", id);
				}
//				jObject.put("Code", respObject.get("Code"));
//				jObject.put("Message", respObject.get("Message"));
			}
			
			//生成本地订单
			String userid = mobile;
			String cpserviceid = "none";
			String consumecode = feeCode;
//			String hret = "none";
			String versionid = "none";
			
			String transido = "";
			
			transido = String.valueOf(System.currentTimeMillis());
			
			String phoneno = mobile;
			String packageid = "1000";
//			String cpparam = outorderid;
			String chnl_id = cpparam.substring(0, 2);
//			String province = cpparam.substring(2, 4);
//			province = GetState(province);
//			String province = Methods.request(mobile);
			String province = getStateByMobile(phoneno);
			province = GetState(province);
			
			if ("未知".equals(province)) {
				province = getStateByMobile(mobile);
				province = GetState(province);
			}
			
			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setId(id);
			receiveSms.setUserid(userid);
			receiveSms.setCpserviceid(cpserviceid);
			receiveSms.setConsumecode(consumecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(HRET_FIRST);
			receiveSms.setStatus(STATUS_FIRST);
			receiveSms.setTransido(transido);
			receiveSms.setVersionid(versionid);
			receiveSms.setPackageid(packageid);
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(Integer.parseInt(chnl_id));
			receiveSms.setFee(0);
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phoneno);
			receiveSms.setExt(mcpid);
			
			orderService.createReceiveSms(receiveSms);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hhOrder error : " + e.getMessage());
		}
		pw.print(jObject.toString());
		return null;
	}
	
	/**
	 * 恒华短信确认
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/hhConfirm.do")
	public ModelAndView hhConfirm(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObject = new JSONObject();
		jObject.put("status", STATUS_RESP_FAILED);
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
//			String mcpid = request.getParameter("mcpid");
//			String feeCode = request.getParameter("feeCode");
//			String mobile = request.getParameter("mobile");
			Long orderid = RequestUtil.GetParamLong(request, "orderid", null);
			String verifycode = request.getParameter("verifycode");
			
			String url = Config.get().get("hhConfirmUrl");
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderid));
			
			if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
				receiveSms.setHret(HRET_SECOND);
				
				String requestStr = "mcpid=" + receiveSms.getExt() ;
				requestStr += "&feeCode=" + receiveSms.getConsumecode();
				requestStr += "&mobile=" + receiveSms.getPhoneno();
				requestStr += "&verifycode=" + verifycode;
				
				StrIntBag str = HttpClientUtil.executeGet(url + "?" + requestStr);
				
				logger.info("hhConfirmUrl : " + url + ", resquestStr: " + requestStr + " , resp: " + str);
				
				if (str != null && StringUtils.isNotEmpty(str._str)) {
					JSONObject respObject = JSONObject.parseObject(str._str);
					
					int code = respObject.getIntValue("Code");
					
					if (code == 0) {
						jObject.put("status", STATUS_RESP_SUCCESS);
//						jObject.put("orderid", id);
					}
//					jObject.put("Code", respObject.get("Code"));
//					jObject.put("Message", respObject.get("Message"));
				}
				
				receiveSms.setStatus(STATUS_SECOND); 
				
				orderService.updateReceiveSms(receiveSms);
//				if (str != null && StringUtils.isNotEmpty(str._str)) {
//					JSONObject respObject = JSONObject.parseObject(str._str);
//					jObject.put("Code", respObject.get("Code"));
//					jObject.put("Message", respObject.get("Message"));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hhConfirm error : " + e.getMessage());
		}
		pw.print(jObject.toString());
		return null;
	}
	
	/**
	 * 浙江恒华网络科技有限公司RDO阅读数据同步
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/hxRdoReport.do")
	public ModelAndView hxRdoReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			String requestStr = request.getQueryString();
			
			logger.info("恒华RDO数据同步参数为： " + requestStr);
			
//			String feecode = request.getParameter("feecode");
//			String mobile = request.getParameter("mobile");
			String outorderid = request.getParameter("outorderid");
//			String orderno = request.getParameter("orderno");
			String resultcode = request.getParameter("resultcode");
			String hret = "-1";
			String status = "-1";
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(outorderid);
			
			if (receiveSms != null) {
				int fee = -1;
				int over_arpu = 0;
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				status = resultcode;
				
				if (resultcode != null && resultcode.equals("200")) {
					hret = HRET_SUCCESS;
					status = STATUS_SUCCESS;
				}
				
				receiveSms.setHret(hret);
				receiveSms.setStatus(status);
				
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
				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_HY", 1000000), Config.get().getInt("MAX_Y_FEE_HY", 1000000), today, receiveSms.getUserid(), fee);
				
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
				
				pw.print("OK");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hxRdoReport error : " + e.getMessage());
		}
		return null;
	}
}
