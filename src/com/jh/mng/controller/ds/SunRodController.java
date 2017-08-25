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
public class SunRodController extends AbstractMultiActionController {
	private static final Logger logger = Logger.getLogger(SunRodController.class);

	@RequestMapping("/sunRdoOrder.do")
	public ModelAndView sunRdoOrder(HttpServletRequest request, HttpServletResponse response){
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("阳光RDO请求参数: " + requestStr);
			
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String phone = RequestUtil.GetParamString(request, "phone", null);
			String cm = RequestUtil.GetParamString(request, "cm", null);
			String mcpid = RequestUtil.GetParamString(request, "mcpid", null);
			
			String province = getStateByMobile(phone);
			province = GetState(province);
			
			String chnlId = cpparam.substring(0,2);
			
			ReceiveSms receiveSms = new ReceiveSms();
			
			Long id = orderService.getNextOrderId();
			
			jObject.put("orderid", id);
			
			StringBuffer param = new StringBuffer();
			param.append("mcpid=");
			param.append(mcpid);
			param.append("&outorderid=");
			param.append(id);
			param.append("&feecode=");
			param.append(feecode);
			param.append("&mobile=");
			param.append(phone);
			param.append("&channelid=1717");
			param.append("&cm=");
			param.append(cm);
			
			String bag = HttpTookit.doGet("http://h208.hzzrhzzr.com/rdo/sunorder.aspx?" + param.toString(), null, "utf-8", false);
			
			logger.info("ygrdo req : " + param.toString() + ",bag resp is : " + bag);
			
			if (bag != null) {
				JSONObject jObject2 = JSONObject.parseObject(bag);
				
				String code = jObject2.getString("Code");
				String msg = jObject2.getString("Message");
				receiveSms.setExt(msg);
				if ("0".equals(code)) {
					jObject.put("status", STATUS_RESP_SUCCESS);
					jObject.put("orderid", id);
					receiveSms.setStatus(STATUS_FIRST);
				} else {
					receiveSms.setStatus(code);
				}
			}
			receiveSms.setId(id);
			receiveSms.setUserid("none");
			receiveSms.setCpserviceid("none");
			receiveSms.setConsumecode(feecode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(HRET_FIRST);
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
			
			logger.info("阳光RDO服务端响应为：" + jObject.toString());
			
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("sunRdoOrder error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/sunRdoVerifyCode.do")
	public ModelAndView sunRdoVerifyCode(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("阳光RDO短信验证请求参数为: " + requestStr);
			
			String verifyCode = RequestUtil.GetParamString(request, "verifycode", null);
			Long orderId = RequestUtil.GetParamLong(request, "orderid", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderId));
			
			if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
				receiveSms.setHret(HRET_SECOND);
				String ext = receiveSms.getExt();
				
				String bag = HttpTookit.doGet("http://h208.hzzrhzzr.com/rdo/sunorderncp.aspx?orderno=" + ext + "&verifycode=" + verifyCode, null, "utf-8", false);
				
		    	logger.info("sunRdoVerifyCode req:" + "http://h208.hzzrhzzr.com/rdo/sunorderncp.aspx?orderno=" + ext + "&verifycode=" + verifyCode + "resp :" + bag);
		    	
		    	if (bag != null) {
					JSONObject jObject2 = JSONObject.parseObject(bag);
					
					String code = jObject2.getString("Code");
					String msg = jObject2.getString("Message");
					
					if ("0".equals(code)) {
						jObject.put("status", STATUS_RESP_SUCCESS);
						jObject.put("orderid", orderId);
						receiveSms.setStatus(STATUS_SECOND);
					} else {
						receiveSms.setStatus(code);
						receiveSms.setExt(msg);
					}
				}
				orderService.updateReceiveSms(receiveSms);
			}
			pw.print(jObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("sunRdoVerifyCode error : " + e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/sunRdoReport.do")
	public ModelAndView sunRdoReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			String req = request.getQueryString();
			
			logger.info("阳光RDO数据同步请求参数为 :" + req);
			
			String orderNo = request.getParameter("outorderid");
			String mobile = request.getParameter("mobile");
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderNo);
			
			if (receiveSms != null) {

				int fee = -1;
				int over_arpu = 0;
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				if (StringUtils.isNotEmpty(mobile)) {
					receiveSms.setPhoneno(mobile);
				}
				
				String province = getStateByMobile(receiveSms.getPhoneno());
				province = GetState(province);
				
				receiveSms.setState(province);
				
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
				pw.print("ok");
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("sunRdoReport error : " + e.getMessage());
		}
		return null;
	}
}
