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
import com.jh.mng.util.RequestUtil;

/**
 * PC页游
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class PcController extends AbstractMultiActionController {

private static final Logger logger = Logger.getLogger(PcController.class);
	
	/**
	 * 数据同步
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/pcSync.do")
	public ModelAndView pcSync(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
//			int n = in.read(b, 0, 10000);
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			
			logger.info("PC业务数据同步请求参数为：" + requestStr);
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
			String phoneno = root.elementText("userId");
			String cpserviceid = root.elementText("cpServiceId");
			String consumecode = root.elementText("consumeCode");
			String cpparam = root.elementText("cpParam");
			String hret = root.elementText("hRet");
			String status = root.elementText("status");
			String versionid = root.elementText("versionId");
			String transido = root.elementText("transIDO");
			
			List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByTransido(transido);
			
			if (receiveSmsList != null && receiveSmsList.size() > 0) {
				logger.error("============repeatId==================:" + transido );
				pw.print("ok");
				return null;
			}
			
			if (status != null && status.equals("1800")) {
				hret = "0";
				status = "1101";
			}
			
			String packageid = "1000";
			
			//渠道编号3位+渠道自定义代码6位+运营商计费渠道代码8位
			String channelId = StringUtils.isNotEmpty(cpparam) ? cpparam.substring(0,3) : "-1"; //
			int chnl_id = Integer.parseInt(channelId);
			
			String province = getStateByMobile(phoneno);
			province = GetState(province);
			
			int fee = -1;
			int over_arpu = 0;
			int sync_cpflag = 0;
			int sync_chnlflag = 0;
			
			String today = DateTool.getCurrentDate("yyyyMMdd");
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(consumecode);
			
			if (consumeInfo != null) {
				fee = consumeInfo.getFee();
				
				List<ChnlResource> chnlResourceList = orderService.queryResourceByConsumeId(Long.parseLong(today), consumeInfo.getId());
				
				boolean flag = false;
				
				if (chnlResourceList != null && chnlResourceList.size() > 0) {
					for (ChnlResource chnlResource : chnlResourceList) {
						
						if (chnl_id == chnlResource.getChnlId().intValue()) {
							List<Mask> maskList = orderService.queryMaskByResIdAndState(chnlResource.getId(), province);
							
							if (maskList != null && maskList.size() > 0) {
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
			
			if (status.equals("1101")) {
				//超限验证
				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_SG", 1000000), Config.get().getInt("MAX_Y_FEE_SG", 1000000), today, phoneno, fee);
			}
			
			
			//扣量验证
			int dec_flag = 0;
			
			if (over_arpu == 0) {
				if (chnl_id > 0) {
					
					if (status.equals("1101")) {
						dec_flag = shouldKouliangByState(today, chnl_id, consumecode, province, packageid);
					}
					
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
			
			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setId(id);
			receiveSms.setUserid(phoneno);
			receiveSms.setCpserviceid(cpserviceid);
			receiveSms.setConsumecode(consumecode);
			receiveSms.setCpparam(StringUtils.isNotEmpty(cpparam) ? cpparam : "none");
			receiveSms.setHret(hret);
			receiveSms.setStatus(status);
			receiveSms.setTransido(transido);
			receiveSms.setVersionid(versionid);
			receiveSms.setPackageid(packageid);
			receiveSms.setSyncflag(sync_cpflag);
			receiveSms.setSyncchnl(sync_chnlflag);
			receiveSms.setChnl_id(chnl_id);
			receiveSms.setFee(fee);
			receiveSms.setDec_flag(dec_flag);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phoneno);
			
			orderService.createReceiveSms(receiveSms);
			
			pw.print("ok");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@RequestMapping("/pcNotify.do")
	public ModelAndView pcNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter pw = null;
		String requestStr = request.getQueryString();
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("新PC业务数据同步请求参数：" + requestStr);
		
		String respStr = "ok";
		
		try {
			pw = response.getWriter();
			
			String tel = request.getParameter("tel");
			String orderId = request.getParameter("cpparam");
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderId);
			
			if (receiveSms != null) {
				int fee = -1;
				int over_arpu = 0;
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				if (StringUtils.isNotBlank(tel)) {
					String province = getStateByMobile(receiveSms.getPhoneno());
					province = GetState(province);
					
					receiveSms.setState(province);
				}
				
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
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			respStr = "error";
			logger.error("pcNotity error :" + e.getMessage());
		}
		
		logBuffer.append(", 服务端响应为： " + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}
	 
	 
	 
	 @RequestMapping("/pcOrder.do")
	 public ModelAndView pcOrder(HttpServletRequest request, HttpServletResponse response) {
		 String params = request.getQueryString();

			StringBuffer logBuffer = new StringBuffer();
			logBuffer.append("PC业务新订单请求参数：" + params);
			
			JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			PrintWriter pw = null;
			
			try {
				pw = response.getWriter();
				
				String feecode = RequestUtil.GetParamString(request, "feecode", null);
				String mobile = RequestUtil.GetParamString(request, "mobile", null);
				String cpparam = RequestUtil.GetParamString(request, "cpparam", null);

				String consumeCode = feecode;
				String transido = String.valueOf(System.currentTimeMillis());
				
				Long id = orderService.getNextOrderId();
				String versionid = "none";
				String chnlId = cpparam.substring(0,2);
				
				//生成本地订单
				String userid = mobile;
				String cpserviceid = "none";
				
				String phoneno = mobile;
				String packageid = "1000";
				
				String province = getStateByMobile(phoneno);
				province = GetState(province);
				
				ReceiveSms receiveSms = new ReceiveSms();
				receiveSms.setId(id);
				receiveSms.setUserid(userid);
				receiveSms.setCpserviceid(cpserviceid);
				receiveSms.setConsumecode(consumeCode);
				receiveSms.setCpparam(cpparam);
				receiveSms.setHret(HRET_FIRST);
				receiveSms.setTransido(transido);
				receiveSms.setVersionid(versionid);
				receiveSms.setPackageid(packageid);
				receiveSms.setSyncflag(0);
				receiveSms.setSyncchnl(0);
				receiveSms.setChnl_id(Integer.parseInt(chnlId));
				receiveSms.setFee(0);
				receiveSms.setDec_flag(1);
				receiveSms.setState(province);
				receiveSms.setPhoneno(phoneno);
				
				StringBuffer sb = new StringBuffer();
				
				sb.append("tel=" + phoneno);
				sb.append("&payCode=" + consumeCode);
				sb.append("&channelCode=142");
				sb.append("&cpparam=" + id);
				
				StrIntBag strIntBag = HttpClientUtil.executeGet("http://ifeestat.vipst.cn/interface_yeyou.php?c=yeyou&m=get_yzm&" + sb.toString());
				
				logger.info("pc req:" + sb.toString() + ", resp:" + strIntBag);
				
				if (strIntBag != null && strIntBag._str != null) {
					JSONObject json = JSONObject.parseObject(strIntBag._str);
					
					String status = json.getString("status");
					String order_number = json.getString("order_number");
					String msg = json.getString("message");
					
					if ("1".equals(status)) {
						jObject.put("orderid", receiveSms.getId());
						jObject.put("status", STATUS_RESP_SUCCESS);
						receiveSms.setStatus(STATUS_FIRST);
						receiveSms.setExt(order_number);
					} else {
						receiveSms.setStatus(status);
						receiveSms.setExt(msg);
					}
					orderService.createReceiveSms(receiveSms);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("pcOrder error : " + e.getMessage());
			}
			logBuffer.append(",服务端响应为：" + jObject.toString());
			
			logger.info(logBuffer.toString());
			pw.print(jObject.toString());
			return null;
	 }
	 
	 @RequestMapping("/pcConfirm.do")
	 public ModelAndView pcConfirm(HttpServletRequest request, HttpServletResponse response) {
		 	JSONObject jObject = new JSONObject();
			jObject.put("status", STATUS_RESP_FAILED);
			PrintWriter pw = null;
			
			String requestStr = request.getQueryString();
			
			StringBuffer logBuffer = new StringBuffer();
			logBuffer.append("PC业务短信确认请求参数：" + requestStr);
			
			try {
				pw = response.getWriter();
				Long orderid = RequestUtil.GetParamLong(request, "orderid", null);
				String verifycode = request.getParameter("verifycode");
				
				ReceiveSms receiveSms = orderService.queryReceiveSmsById(String.valueOf(orderid));
				
				if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
					
					receiveSms.setHret(HRET_SECOND);
					
					StringBuffer sb = new StringBuffer();
					
					sb.append("order_number=" + receiveSms.getExt());
					sb.append("&yzm=" + verifycode);
					
					StrIntBag strIntBag = HttpClientUtil.executeGet("http://ifeestat.vipst.cn/interface_yeyou.php?c=yeyou&m=submit_yzm&" + sb.toString());
					
					logger.info("pcConifrm req: " + sb.toString() + ", resp:" + strIntBag);
					
					if (strIntBag != null && strIntBag._str != null) {
						JSONObject json = JSONObject.parseObject(strIntBag._str);
						
						String status = json.getString("status");
						String message = json.getString("message");
						
						if ("1".equals(status)) {
							jObject.put("orderid", receiveSms.getId());
							jObject.put("status", STATUS_RESP_SUCCESS);
							receiveSms.setStatus(STATUS_SECOND);
						} else {
							receiveSms.setStatus(status);
							receiveSms.setExt(message);
							jObject.put("orderid", receiveSms.getId());
							jObject.put("status", status);
						}
					} else {
						receiveSms.setStatus(STATUS_FAILED);
					}
					orderService.updateReceiveSms(receiveSms);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("pcConfirm error : " + e.getMessage());
			}
			
			logBuffer.append(",服务端响应为：" + jObject.toString());
			
			logger.info(logBuffer.toString());
			
			pw.print(jObject.toString());
			return null;
	 }
}
