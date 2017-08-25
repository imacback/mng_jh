package com.jh.mng.controller.ds;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import com.jh.mng.util.MD5Util;
import com.jh.mng.util.RequestUtil;

/**
 * 世纪佳缘代收代务
 * @author admin
 *
 */
@Controller
@RequestMapping("/ds")
public class JiaYuanController extends AbstractMultiActionController {
	
	private final static Logger logger = Logger.getLogger(JiaYuanController.class);

	/**
	 * 订购验证码下发
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/jyDdoOrder.do")
//	public ModelAndView jyDdoOrder(HttpServletRequest request, HttpServletResponse response) {
//		String params = request.getQueryString();
//
//		logger.info("世纪佳缘DDO业务验证码下发请求参数：" + params);
//		
//		JSONObject jObject = new JSONObject();
//		jObject.put("status", STATUS_RESP_FAILED);
//		PrintWriter pw = null;
//		
//		try {
//			pw = response.getWriter();
//			
//			String subchannel = RequestUtil.GetParamString(request, "subchannel", null);
//			String feecode = RequestUtil.GetParamString(request, "feecode", null);
//			String partnerid = RequestUtil.GetParamString(request, "partnerid", null);
//			String mobile = RequestUtil.GetParamString(request, "mobile", null);
//			String imei = RequestUtil.GetParamString(request, "imei", null);
//			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
//			String ip = request.getRemoteAddr();
//			
//			String mmsubsnumb = Config.get().get("mmsubsnumb_ddo");
//			String feeway = Config.get().get("feeway_ddo");
//
//			String consumeCode = feecode;
//			String transido = "";
//			
//			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(consumeCode);
//			
//			Long id = orderService.getNextOrderId();
//			String ext = "";
//			
//			StringBuffer sb = new StringBuffer();
//			sb.append("mmsubsnumb=");
//			sb.append(mmsubsnumb);
//			sb.append("&clienttype=");
//			sb.append("&channel=");
//			sb.append("&subchannel=");
//			sb.append(subchannel);
//			sb.append("&feecode=");
//			sb.append(feecode);
//			sb.append("&partnerid=");
//			sb.append(partnerid);
//			sb.append("&partnerfeeid=");
//			sb.append(id);
//			sb.append("&userid=");
//			sb.append(imei);
//			sb.append("&mobile=");
//			sb.append(mobile);
//			sb.append("&ip=");
//			sb.append(ip);
//			sb.append("&feeway=");
//			sb.append(feeway);
//			sb.append("&_mvcode=");
//			sb.append(MD5Util.getMD5(partnerid + id + consumeInfo.getFeeurl()));
//			
//			StrIntBag resp = HttpClientUtil.executeGet(Config.get().get("orderurl_ddo") + "?" + sb.toString());
//			
//			logger.info("jyDdoOrder request : " + sb.toString() + ", resp: " + resp);
//			
//			if (resp != null && resp._str != null) {
//				JSONObject respObject = JSONObject.parseObject(resp._str);
//				
//				String status = respObject.getString("status");
//				
//				if (status != null && status.equals("1")) {
//					transido = respObject.getString("feeid");
//					String payUrl = respObject.getString("payUrl");
//					
////					StrIntBag orderResp = HttpClientUtil.executeGet(payUrl);
////					
////					logger.debug("vcode orderResp is : " + orderResp);
//			    	
////			    	Document doc = Jsoup.parse(orderResp._str);  
////			    	
////			    	Element form = doc.select("form").first();
////			    	String action = form.attr("action");
//					
//					String ssoUrl = this.getSsoUrl(payUrl);
//					
////					StrIntBag orderResp = HttpClientUtil.executeGet(ssoUrl);
//					
//					HttpClient httpClient = new HttpClient();
////					
////					GetMethod getMethod = new GetMethod(payUrl);
////					
////					httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
////		            httpClient.executeMethod(getMethod);
////		            
////		            Cookie[] cookies=httpClient.getState().getCookies();
////		            String tmpcookies= "";
////		            for(Cookie c:cookies){
////		                tmpcookies += c.toString()+";";
////		            }
////		            
////		            String text2 = getMethod.getResponseBodyAsString();
////		            
////		            System.out.println(text2);
////					
//					
//					GetMethod getMethod2 = new GetMethod(ssoUrl);
//					httpClient.executeMethod(getMethod2);
//					
////					getMethod2.setRequestHeader("cookie",tmpcookies);
//					
//					String text = getMethod2.getResponseBodyAsString();
//					
//					
//					Document doc = Jsoup.parse(text); 
//					Element form = doc.select("form").first();
//			    	String action = form.attr("action");
//			    	
//					
//					
//					//模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
//			        PostMethod postMethod = new PostMethod("http://wap.dm.10086.cn" + action);
//			 
//			        //设置登陆时要求的信息，一般就用户名和密码，验证码自己处理了
//			        NameValuePair[] data = {
//			                new NameValuePair("username", "13810878435"),
//			                new NameValuePair("password", "zhuan8578"),
//			                new NameValuePair("lt", doc.select("input[name=lt]").val()),
//			                new NameValuePair("execution", doc.select("input[name=execution]").val()),
//			                new NameValuePair("_eventId", doc.select("input[name=_eventId]").val()),
//			                new NameValuePair("user_hackRemember", doc.select("input[name=user_hackRemember]").val()),
//			        };
//			        postMethod.setRequestBody(data);
//			        
//			        
//			        String payResp = HttpTookit.methodPost("http://wap.dm.10086.cn" + action,data);
//			        
//			        try {
//			            //设置 HttpClient 接收 Cookie,用与浏览器一样的策略
//			            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//			            int statusCode = httpClient.executeMethod(postMethod);
//			            
//			            String text1111 = postMethod.getResponseBodyAsString();
//			            
//			            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY  
//			                    || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {  
//			                // 从头中取出转向的地址  
//			                Header locationHeader = postMethod.getResponseHeader("location");  
////			                String location = null;  
//			                if (locationHeader != null) {  
//			                    String location = locationHeader.getValue();  
////			                    log.info("The page was redirected to:" + location);  
////			                    response= methodPost(location,data);//用跳转后的页面重新请求。  
//			                } else {  
//			                    logger.error("Location field value is null.");  
//			                }  
//			            } else {
//			            	logger.info(postMethod.getStatusLine());
//			            }
//			            
//			 
//			            //获得登陆后的 Cookie
//			            Cookie[] cookies2=httpClient.getState().getCookies();
//			            String tmpcookies2= "";
//			            for(Cookie c:cookies2){
//			                tmpcookies2 += c.toString()+";";
//			            }
//			 
//			            //进行登陆后的操作
//			            GetMethod getMethod = new GetMethod(ssoUrl);
//			 
//			            //每次访问需授权的网址时需带上前面的 cookie 作为通行证
//			            getMethod.setRequestHeader("cookie",tmpcookies2);
//			 
//			            //你还可以通过 PostMethod/GetMethod 设置更多的请求后数据
//			            //例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
////			            postMethod.setRequestHeader("Referer", "http://unmi.cc");
////			            postMethod.setRequestHeader("User-Agent","Unmi Spot");
//			 
//			            httpClient.executeMethod(getMethod);
//			 
//			            //打印出返回数据，检验一下是否成功
//			            String text23 = getMethod.getResponseBodyAsString();
//			            System.out.println(text23);
//			 
//			        } catch (Exception e) {
//			            e.printStackTrace();
//			        }   
//					
//					
//					String payStr = payUrl.substring(payUrl.indexOf("PayCode=") + 8);
//					
//					String payCode = payStr.substring(0, payStr.indexOf("&"));
//					
//					String[] str = getDmINfo(payUrl);
//					
//					String sessionId = str[0];
//					String orderId = str[1];
//					
//					ext = payCode + "|" + sessionId + "|" + orderId;
//					
//					String urlGetCode = "http://wap.dm.10086.cn/apay/handle.jsp?t=checkcode&paycode=" + payCode + "&sss=" + sessionId + "&order=" + orderId + "&msisdn=" + mobile;
//					
//					StrIntBag getCodeBag = HttpClientUtil.executeGet(urlGetCode);
//					
//					if (getCodeBag != null && getCodeBag._int == 200) {
//						
//						JSONObject jObject2 = JSONObject.parseObject(getCodeBag._str);
//						String returnCode = jObject2.getString("ReturnCode");
//						
//						if ("0".equals(returnCode)) {
//							jObject.put("orderid", id);
//							jObject.put("status", STATUS_RESP_SUCCESS);
//						} else {
//							jObject.put("status", returnCode);
//						}
//					}
//				} else {
//					jObject.put("status", respObject.get("subStatus"));
//				}
//			}
//			
//			//生成本地订单
//			String userid = mobile;
//			String cpserviceid = "none";
//			String versionid = "none";
//			
//			if (StringUtils.isEmpty(transido)) {
//				transido = String.valueOf(System.currentTimeMillis());
//			}
//			
//			String phoneno = mobile;
//			String packageid = "1000";
//			
//			String province = getStateByMobile(phoneno);
//			province = GetState(province);
////			String province = Methods.request(mobile);
//			
//			ReceiveSms receiveSms = new ReceiveSms();
//			receiveSms.setId(id);
//			receiveSms.setUserid(userid);
//			receiveSms.setCpserviceid(cpserviceid);
//			receiveSms.setConsumecode(consumeCode);
//			receiveSms.setCpparam(cpparam);
//			receiveSms.setHret(HRET_FIRST);
//			
//			if (jObject.get("status").toString().equals(STATUS_RESP_SUCCESS)) {
//				receiveSms.setStatus(STATUS_FIRST);
//			} else {
//				receiveSms.setStatus(jObject.get("status").toString());
//			}
//			receiveSms.setTransido(transido);
//			receiveSms.setVersionid(versionid);
//			receiveSms.setPackageid(packageid);
//			receiveSms.setSyncflag(0);
//			receiveSms.setSyncchnl(0);
//			receiveSms.setChnl_id(Integer.parseInt(subchannel));
//			receiveSms.setFee(consumeInfo.getFee());
//			receiveSms.setDec_flag(1);
//			receiveSms.setState(province);
//			receiveSms.setPhoneno(phoneno);
//			receiveSms.setExt(ext);
//			
//			orderService.createReceiveSms(receiveSms);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("jyDdoOrder error : " + e.getMessage());
//		}
//		pw.print(jObject.toString());
//		return null;
//	}
	
	/**
	 * 验证码回填
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/jyDdoConfirm.do")
	public ModelAndView jyDdoConfirm(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject respJson = new JSONObject();
			respJson.put("status", STATUS_RESP_FAILED);
			
			String requestStr = request.getQueryString();
			
			logger.info("佳缘DDO业务短信验证参数为： " + requestStr);
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "vcode", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			
			if (receiveSms != null && receiveSms.getStatus().equals(STATUS_FIRST)) {
				receiveSms.setHret(HRET_SECOND);
					   
				String ext = receiveSms.getExt();
				String[] strings = ext.split("\\|");
				
				String paycode = strings[0];
				String sessionId = strings[1];
				String orderId = strings[2];
					   
			    String url = "http://wap.dm.10086.cn/apay/handle.jsp?t=pay&paycode=" + paycode + "&sss=" + sessionId + "&order=" + orderId + "&msisdn=" + receiveSms.getPhoneno() + "&checkcode=" + verify_code;
				StrIntBag resp = HttpClientUtil.executeGet(url);
				
				logger.info("jyDdoConfirm request : " + url + ", resp: " + resp);
				
				if (resp != null && resp._str != null) {
					JSONObject respObject = JSONObject.parseObject(resp._str);
					
					String status = respObject.getString("ReturnCode");
					
					if (status != null && status.equals("0")) {
						respJson.put("status", STATUS_RESP_SUCCESS);
						receiveSms.setStatus(STATUS_SECOND); //计费短信发送成功
					} else {
						respJson.put("status", respObject.getString("ReturnCode"));
						receiveSms.setStatus(status);
					}
				}
				
				orderService.updateReceiveSms(receiveSms);
			}
			pw.print(respJson.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("jyDdoConfirm error : " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 佳缘数据同步
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/jyReport.do")
	public ModelAndView jyReport(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("佳缘数据同步参数为：" + request.getQueryString());
			PrintWriter pw = response.getWriter();
			
			String frontOrderNo = RequestUtil.GetParamString(request, "frontOrderNo", null);
			String returnCode = RequestUtil.GetParamString(request, "returnCode", null);
			String phone = RequestUtil.GetParamString(request, "phone", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(frontOrderNo);
			
			if (receiveSms != null) {
				int fee = -1;
				int over_arpu = 0;
				String today = DateTool.getCurrentDate("yyyyMMdd");
				
				if (StringUtils.isNotEmpty(phone)) {
					receiveSms.setPhoneno(phone);
				}
				
				if (returnCode != null && "0".equals(returnCode)) {
					receiveSms.setStatus(STATUS_SUCCESS);
					receiveSms.setHret(HRET_SUCCESS);
				} else {
					String epara = request.getParameter("epara");
					String status = "-1";
					if (epara != null && !"".equals(epara)) {
						status = epara;
					} else {
						status = returnCode;
					}
					receiveSms.setStatus(status);
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
				over_arpu = checkArpu(Config.get().getInt("MAX_R_FEE_JYDDO", 1000000), Config.get().getInt("MAX_Y_FEE_JYDDO", 1000000), today, receiveSms.getUserid(), fee);
				
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
			logger.error("jyReport error : " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 世纪佳缘RDO短验下单请求
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/jyRdoOrder.do")
	public ModelAndView jyRdoOrder(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getQueryString();

		logger.info("世纪佳缘RDO短验下单请求参数：" + params);
		
		JSONObject jObject = new JSONObject();
		jObject.put("status", "9999");
		PrintWriter pw = null;
		
		try {
			pw = response.getWriter();
			
			String subchannel = RequestUtil.GetParamString(request, "subchannel", null);
			String feecode = RequestUtil.GetParamString(request, "feecode", null);
			String partnerid = RequestUtil.GetParamString(request, "partnerid", null);
			String mobile = RequestUtil.GetParamString(request, "mobile", null);
			String imei = RequestUtil.GetParamString(request, "imei", null);
			String cpparam = RequestUtil.GetParamString(request, "cpparam", null);
			String ip = request.getRemoteAddr();
			
			String mmsubsnumb = Config.get().get("mmsubsnumb_rdo");
			String feeway = Config.get().get("feeway_rdo");

			String consumeCode = feecode;
			String transido = "";
			
			ConsumeInfo consumeInfo = orderService.queryConsumeInfoByCode(consumeCode);
			
			Long id = orderService.getNextOrderId();
			//生成本地订单
			String userid = mobile;
			String cpserviceid = "none";
			String hret = "none";
			String versionid = "none";
			
			if (StringUtils.isEmpty(transido)) {
				transido = String.valueOf(System.currentTimeMillis());
			}
			
			String phoneno = mobile;
			String packageid = "1000";
			String chnl_id = subchannel;
//			String province = cpparam.substring(0, 2);
//			province = GetState(province);
			
//			String province = Methods.request(mobile);
			
			String province = getStateByMobile(phoneno);
			province = GetState(province);
			
			ReceiveSms receiveSms = new ReceiveSms();
			receiveSms.setId(id);
			receiveSms.setUserid(userid);
			receiveSms.setCpserviceid(cpserviceid);
			receiveSms.setConsumecode(consumeCode);
			receiveSms.setCpparam(cpparam);
			receiveSms.setHret(hret);
			
			StringBuffer sb = new StringBuffer();
			sb.append("mmsubsnumb=");
			sb.append(mmsubsnumb);
			sb.append("&clienttype=");
			sb.append("&channel=");
			sb.append("&subchannel=");
			sb.append(subchannel);
			sb.append("&feecode=");
			sb.append(feecode);
			sb.append("&partnerid=");
			sb.append(partnerid);
			sb.append("&partnerfeeid=");
			sb.append(id);
			sb.append("&userid=");
			sb.append(imei);
			sb.append("&mobile=");
			sb.append(mobile);
			sb.append("&ip=");
			sb.append(ip);
			sb.append("&returl=");
			sb.append("&feeway=");
			sb.append(feeway);
			sb.append("&_mvcode=");
			sb.append(MD5Util.getMD5(partnerid + id + consumeInfo.getFeeurl()));
			
			StrIntBag resp = HttpClientUtil.executeGet(Config.get().get("orderurl_rdo") + "?" + sb.toString());
			
			logger.info("jyRdoOrder request : " + sb.toString() + ", resp: " + resp);
			
			if (resp != null && resp._str != null) {
				JSONObject respObject = JSONObject.parseObject(resp._str);
				
				String status = respObject.getString("status");
				
				if (status != null && status.equals("1")) {
					receiveSms.setStatus("1");
					
					StrIntBag orderResp = HttpClientUtil.executeGet(respObject.getString("payUrl"));
					
					logger.debug("vcode orderResp is : " + orderResp);
			    	
			    	Document doc = Jsoup.parse(orderResp._str);  
			    	
			    	Element form = doc.select("form").first();
			    	String action = form.attr("action");
			    	
			    	Map<String, String> map = new HashMap<String, String>();
			    	map.put("msisdn", mobile);
			    	
			    	NameValuePair msisdn = new NameValuePair("msisdn", mobile);
			        NameValuePair[] data = {msisdn}; 
			       
			        String payResp = HttpTookit.methodPost("http://wap.cmread.com" + action,data);
			    	
			    	logger.debug("payResp is : " + payResp);
			    	
			    	receiveSms.setExt(payResp);
					jObject.put("orderid", id);
					jObject.put("status", 1);
				} else {
					jObject.put("status", respObject.get("subStatus"));
				}
			}
			receiveSms.setTransido(transido);
			receiveSms.setVersionid(versionid);
			receiveSms.setPackageid(packageid);
			receiveSms.setSyncflag(0);
			receiveSms.setSyncchnl(0);
			receiveSms.setChnl_id(Integer.parseInt(chnl_id));
			receiveSms.setFee(consumeInfo.getFee());
			receiveSms.setDec_flag(1);
			receiveSms.setState(province);
			receiveSms.setPhoneno(phoneno);
			
			orderService.createReceiveSms(receiveSms);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("jyRdoOrder error : " + e.getMessage());
		}
		pw.print(jObject.toString());
		return null;
	}
	
	/**
	 * RDO短验确认
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/jyRdoConfirm.do")
	public ModelAndView jyRdoConfirm(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter pw = response.getWriter();
			
			JSONObject respJson = new JSONObject();
			respJson.put("status", "1");
			respJson.put("msg", "成功");
			
			String requestStr = request.getQueryString();
			
			logger.info("佳缘RDO短信确认验证参数为： " + requestStr);
			
			String orderid = RequestUtil.GetParamString(request, "orderid", null);
			String verify_code = RequestUtil.GetParamString(request, "verifyCode", null);
			
			ReceiveSms receiveSms = orderService.queryReceiveSmsById(orderid);
			
			if (receiveSms != null && receiveSms.getStatus().equals("1")) {
					   
			    if (receiveSms.getExt() != null) {
			    	Document docmDocument = Jsoup.connect(receiveSms.getExt()).get();
			    	Element payForm = docmDocument.select("form").first();
			    	String confrimAction = payForm.attr("action");
			    	
			    	NameValuePair verifyCode = new NameValuePair("verifyCode", verify_code);
			        NameValuePair[] data = {verifyCode}; 
			    	
			    	String confirmResp = HttpTookit.methodPost("http://wap.cmread.com" + confrimAction, data);
			    	logger.info("confirmResp is : " + confirmResp);
			    }
			}
			pw.print(respJson.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("iydSmsConfirm error : " + e.getMessage());
		}
		return null;
	}
	
}
