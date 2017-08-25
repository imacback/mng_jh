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

import com.jh.mng.common.Config;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.Mask;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.DateTool;

/**
 * 兆荣代码
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class ZrController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(ZrController.class);
	
	@RequestMapping("/zrMo.do")
	public ModelAndView zrMo(HttpServletRequest request, HttpServletResponse response) {
		String reqStr = request.getQueryString();
		
		logger.info("兆荣MO上行请求参数为：" + reqStr);
		
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
//			String actionid = request.getParameter("actionid");
//			String destnumber = request.getParameter("destnumber");
			String srcnumber = request.getParameter("srcnumber");
//			String unionid = request.getParameter("unionid");
			String content = request.getParameter("content");
			String linkid = request.getParameter("linkid");
//			String seqid = request.getParameter("seqid");
//			String orderid = request.getParameter("orderid");
//			String hmac = request.getParameter("hmac");
//			String feestatus = request.getParameter("feestatus");
			
			//生成本地订单
			String userid = srcnumber;
			String cpserviceid = "none";
			String versionid = "none";
			
			String packageid = "1000";
			String consumecode = content.substring(0, 4);
			
			int channel = -1;
			
			if (content.length() > 4) {
				String subchannel = content.substring(4, 5);
				channel = getChnl(subchannel);
			}
			
			
			String province = getStateByMobile(srcnumber);
			province = GetState(province);
			
			List<ReceiveSms> list = orderService.queryReceiveSmsByTransido(linkid);
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(consumecode);
			
			if (list != null && list.size() > 0) {
				pw.write("ok");
				return null;
			} else {
				if (StringUtils.isEmpty(userid)) {
					userid = "none";
				}
				
				if (StringUtils.isEmpty(srcnumber)) {
					srcnumber = "none";
				}
				
				Long id = orderService.getNextOrderId();
				
				ReceiveSms receiveSms = new ReceiveSms();
				receiveSms.setId(id);
				receiveSms.setUserid(userid);
				receiveSms.setCpserviceid(cpserviceid);
				receiveSms.setConsumecode(consumecode);
				receiveSms.setCpparam(content.substring(4));
				receiveSms.setHret(HRET_SECOND);
				receiveSms.setStatus(STATUS_SECOND);
				receiveSms.setTransido(linkid);
				receiveSms.setVersionid(versionid);
				receiveSms.setPackageid(packageid);
				receiveSms.setSyncflag(0);
				receiveSms.setSyncchnl(0);
				receiveSms.setChnl_id(channel);
				receiveSms.setFee(consumeInfo.getFee());
				receiveSms.setDec_flag(1);
				receiveSms.setState(province);
				receiveSms.setPhoneno(srcnumber);
				
				orderService.createReceiveSms(receiveSms);
			}
			
			pw.write("ok");
		} catch (Exception e) {
			logger.error("zrMo error : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("/zrMr.do")
	public ModelAndView zrMr(HttpServletRequest request, HttpServletResponse response) {
		String reqStr = request.getQueryString();
		
		logger.info("兆荣数据同步请求参数为：" + reqStr);
		
		PrintWriter pw = null; 
		
		try {
			pw = response.getWriter();
			
			String phone = request.getParameter("phone");
			String linkid = request.getParameter("linkid");
			String report = request.getParameter("report");
//			String unionid = request.getParameter("unionid");
//			String donetime = request.getParameter("donetime");
//			String subtime = request.getParameter("subtime");
//			String destnumber = request.getParameter("destnumber");
//			String content = request.getParameter("content");
//			String hmac = request.getParameter("hmac");
			
			List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByTransido(linkid);
			
			if (receiveSmsList != null && receiveSmsList.size() == 1) {
				ReceiveSms receiveSms = receiveSmsList.get(0);
				int fee = -1;
				int over_arpu = 0;
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				if (StringUtils.isNotEmpty(phone)) {
					receiveSms.setPhoneno(phone);
				}
				
				if (report != null && "DELIVRD".equals(report)) {
					receiveSms.setStatus("1101");
					receiveSms.setHret("0");
				} else {
					receiveSms.setStatus(report);
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
				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_ZR", 1000000), Config.get().getInt("MAX_Y_FEE_ZR", 1000000), today, receiveSms.getUserid(), fee);
				
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
			logger.error("zrMr error : " + e.getMessage());
		}
		return null;
	}

}
