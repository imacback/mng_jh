package com.jh.mng.controller.ds;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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
 * 滚石传统短信
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class GsController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(GsController.class);

	@RequestMapping("/gsMo.do")
	public ModelAndView gsMo(HttpServletRequest request, HttpServletResponse response) {
		StringBuffer logBuffer = new StringBuffer();
		
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
			in.read(b, 0, 10000);
			String requestStr = new String(b, "GBK");
			in.close();
			
			logBuffer.append("滚石传统MO请求参数为：" + requestStr);
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
			String phoneno = root.elementText("SRC_TERMID");
			String consumecode = root.elementText("SERVICEID");
			String status = root.elementText("MOSTATUS");
			String transido = root.elementText("TRANSACTIONID");
			String content = root.elementText("CONTENT");
			
			
			List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByTransido(transido);
			
			if (receiveSmsList != null && receiveSmsList.size() > 0) {
				logger.error("============gs repeatId==================:" + transido );
				pw.print("<?xml version=\"1.0\" encoding=\"gb2312\"?><MASP><RESULT>202</RESULT><CONTENT>transido error</CONTENT></MASP>");
				return null;
			} else {
				ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(consumecode);
				
				int chnnelId = -1;
				
				if ("2".equals(content)) {
					chnnelId = Integer.parseInt(Config.get().get("gs_fixedchannelid"));
				} else {
					String chid = content.substring(consumeInfo.getFeeurl().length(), content.indexOf(consumeInfo.getFeeurl()) + consumeInfo.getFeeurl().length() + 1);
					String[] chnIdArray = Config.get().get("gs_lt_chid").split("\\|");
					
					for (int i = 0; i < chnIdArray.length; i++) {
						String[] a = chnIdArray[i].split(",");
						if (a[0].equalsIgnoreCase(chid)) {
							chnnelId = Integer.parseInt(a[1]);
							break;
						}
					}
				}
				
				String province = getStateByMobile(phoneno);
				province = GetState(province);
				
				Long id = orderService.getNextOrderId();
				
				ReceiveSms receiveSms = new ReceiveSms();
				receiveSms.setId(id);
				receiveSms.setUserid(phoneno);
				receiveSms.setCpserviceid("none");
				receiveSms.setConsumecode(consumecode);
				receiveSms.setCpparam(content);
				
				if ("0".equals(status)) {
					receiveSms.setHret(HRET_FIRST);
					receiveSms.setStatus(STATUS_FIRST);
				} else {
					receiveSms.setHret(HRET_FAILED);
					if ("-1".equals(status)) {
						receiveSms.setStatus("2001");
					} else if ("-2".equals(status)) {
						receiveSms.setStatus("2002");
					} else if ("-3".equals(status)) {
						receiveSms.setStatus("2003");
					} else if ("-4".equals(status)) {
						receiveSms.setStatus("2004");
					} else {
						receiveSms.setStatus(STATUS_FAILED);
					}
				}
				
				receiveSms.setTransido(transido);
				receiveSms.setVersionid("none");
				receiveSms.setPackageid("1000");
				receiveSms.setSyncflag(0);
				receiveSms.setSyncchnl(0);
				receiveSms.setChnl_id(chnnelId);
				receiveSms.setFee(consumeInfo.getFee());
				receiveSms.setDec_flag(1);
				receiveSms.setState(province);
				receiveSms.setPhoneno(phoneno);
				
				orderService.createReceiveSms(receiveSms);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("gsMo error : " + e.getMessage());
		}
		
		String respStr = "<?xml version=\"1.0\" encoding=\"gb2312\"?><MASP><RESULT>200</RESULT><CONTENT>ok</CONTENT></MASP>";

		logBuffer.append(" ,服务端响应为：" + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}
	
	@RequestMapping("/gsMr.do")
	public ModelAndView gsMr(HttpServletRequest request, HttpServletResponse response) {
		StringBuffer logBuffer = new StringBuffer();
		
		PrintWriter pw = null;
		
		String respStr = "<?xml version=\"1.0\" encoding=\"gb2312\"?><MASP_RESP><RESULT>200</RESULT><RESULT_MSG>OK</RESULT_MSG></MASP_RESP>";
		
		try {
			pw = response.getWriter();
			
			InputStream in = request.getInputStream();
			byte b[] = new byte[10000];
			in.read(b, 0, 10000);
			String requestStr = new String(b, "GBK");
			in.close();
			
			logBuffer.append("滚石传统MR请求参数为：" + requestStr);
			
			if(null != requestStr && !"".equals(requestStr)){
				if(requestStr.indexOf("<") != -1 && requestStr.lastIndexOf(">") != -1 && requestStr.lastIndexOf(">") > requestStr.indexOf("<"))
				requestStr = requestStr.substring(requestStr.indexOf("<"), requestStr.lastIndexOf(">") + 1);
			}
			
			Document document = DocumentHelper.parseText(requestStr);
			Element root = document.getRootElement();
			
			String status = root.elementText("STATUS");
			String transido = root.elementText("TRANSACTIONID");
			String feeStr = root.elementText("FEE_CODE");
			
			
			List<ReceiveSms> receiveSmsList = orderService.queryReceiveSmsByTransido(transido);
			
			if (receiveSmsList != null && receiveSmsList.size() == 1) {
				ReceiveSms receiveSms = receiveSmsList.get(0);
				int fee = Integer.parseInt(feeStr);
				int over_arpu = 0;
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				if (fee > 0 && "DELIVRD".equals(status)) {
					receiveSms.setStatus(STATUS_SUCCESS);
					receiveSms.setHret(HRET_SUCCESS);
				} else {
					receiveSms.setStatus(status);
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
			} else {
				respStr = "<?xml version=\"1.0\" encoding=\"gb2312\"?><MASP_RESP><RESULT>202</RESULT><RESULT_MSG>ERROR</RESULT_MSG></MASP_RESP>";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("gsMr error : " + e.getMessage());
		}

		logBuffer.append(" ,服务端响应为：" + respStr);
		logger.info(logBuffer.toString());
		
		pw.print(respStr);
		return null;
	}
}
