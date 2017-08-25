package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

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
 * 信元业务（电信）
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class XinYuanController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(XinYuanController.class);

	@RequestMapping("/xyReport.do")
	public ModelAndView xyReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("电信信元数据同步参数为：" + request.getQueryString());
			PrintWriter pw = response.getWriter();
			
			String userid  = request.getParameter("simno");
			String phoneno = userid;
			
			if ( phoneno.equals("none") )
			{
				pw.print("error:1");
				return null;
			}
			
			String cpserviceid = "xinyuan_sms";
			String consumecode = request.getParameter("instructor");
			String versionid = "none";
			String cpparam = consumecode.substring(4);
			
			consumecode = consumecode.substring(0, 4);
			String status = request.getParameter("status");
			String hret = "";
			String packageid = "1000";
			
			String transido = request.getParameter("msgid");
			
			if (StringUtils.isEmpty(transido)) {
				transido = String.valueOf(System.currentTimeMillis());
			}
			
			List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByTransido(transido);
			
			if (receiveSmsList != null && receiveSmsList.size() > 0) {
				logger.info("repeat transido : " + transido);
				pw.print("200");
				return null;
			}
			
			if (status != null && status.equals("DELIVRD")) {
				hret = "0";
				status = "1101";
			} else {
				status = "-1";
				hret = "1";
			}
			int chnl_id = -1;
			
			if (cpparam.length() >= 2) {
				String chnlStr = cpparam.substring(0, 2);
				Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$"); 
				
				if (pattern.matcher(chnlStr).matches()) {
					chnl_id = Integer.parseInt(chnlStr);
				}
			}
			
//			String province = Methods.request(phoneno);
			
//			if ("未知".equals(province) || "全国".equals(province)) {
//				province = getStateByMobile(phoneno);
//				province = GetState(province);
//			}
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
				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_XY", 1000000), Config.get().getInt("MAX_Y_FEE_XY", 1000000), today, userid, fee);
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
			
			if (StringUtils.isEmpty(cpparam)) {
				cpparam = "none";
			}
			
			Long id = orderService.getNextOrderId();
			
			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setId(id);
			receiveSms.setUserid(userid);
			receiveSms.setCpserviceid(cpserviceid);
			receiveSms.setConsumecode(consumecode);
			receiveSms.setCpparam(cpparam);
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
			
			pw.print("200");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("xyReport error : " + e.getMessage());
		}
		return null;
	}
}
