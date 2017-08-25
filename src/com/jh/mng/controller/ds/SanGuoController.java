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
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.Mask;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.util.DateTool;

/**
 * 狂斩三国
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class SanGuoController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(SanGuoController.class);
	
	/**
	 * 数据同步
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sgSync.do")
	public ModelAndView sgSync(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
//			int n = in.read(b, 0, 10000);
			in.read(b, 0, 10000);
			String requestStr = new String(b, "UTF-8");
			in.close();
			
			logger.info("狂斩三国数据同步请求参数为：" + requestStr);
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
//			JSONObject jObject = JSONObject.parseObject(requestStr);
			
//			String ssid = jObject.getString("ssid");
//			String hret = jObject.getString("hret");
//			String hstatus = jObject.getString("hstatus");
			
			String userid = root.elementText("userId");
			String cpserviceid = root.elementText("contentId");
			String consumecode = root.elementText("consumeCode");
			String versionid = root.elementText("versionId");
			String cpparam = root.elementText("cpparam");
			String status = root.elementText("status");
			String hret = root.elementText("hRet");
			
			String transido = String.valueOf(System.currentTimeMillis());
			
			JSONObject respJson = new JSONObject(); 
			respJson.put("status", "100");
			
			List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByTransido(transido);
			
			if (receiveSmsList != null && receiveSmsList.size() > 0) {
				pw.print(respJson.toJSONString());
				return null;
			}
			
			if (status != null && status.equals("1800")) {
				hret = "0";
				status = "1101";
			}
			
			if (StringUtils.isEmpty(transido)) {
				transido = String.valueOf(System.currentTimeMillis());
			}
			
			String phoneno = "none";
			String packageid = "1000";
			int chnl_id = Integer.parseInt(cpparam.substring(0, 2)); //前2位渠道
			String province = cpparam.substring(2, 4); //后2位省份
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
				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_SG", 1000000), Config.get().getInt("MAX_Y_FEE_SG", 1000000), today, userid, fee);
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
			
			pw.print(respJson.toJSONString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
