package com.jh.mng.controller;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.jh.mng.config.MyConfig;
import com.jh.mng.pojo.Admin;
import com.jh.mng.pojo.AdminRoleMap;
import com.jh.mng.pojo.ChnlDecPercent;
import com.jh.mng.pojo.Component;
import com.jh.mng.pojo.RoleActionMap;
import com.jh.mng.service.admin.IAdminService;
import com.jh.mng.service.order.IOrderService;
import com.jh.mng.util.Methods;
import com.jh.mng.util.MobileFromUtil;



/**
 * 
 * @ClassName: AbstractMultiActionController
 * @Description: 
 * @author gs
 * @date Nov 1, 2011 1:43:48 PM
 * 
 */
public class AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(AbstractMultiActionController.class);
	
	public static final String HRET_FIRST = "-1";
	public static final String HRET_SECOND = "-2";
	public static final String HRET_THIRD = "-3";
	public static final String HRET_FOUR = "-4";
	public static final String HRET_SUCCESS = "0";
	public static final String HRET_FAILED = "9999";
	
	public static final String STATUS_FIRST = "-1";
	public static final String STATUS_SECOND = "-2";
	public static final String STATUS_THIRD = "-3";
	public static final String STATUS_SUCCESS = "1101";
	public static final String STATUS_FAILED = "9999";
	
	public static final String STATUS_RESP_SUCCESS = "0";
	public static final String STATUS_RESP_FAILED = "-1";
	
	public static final String LOGINFLAG_SUCCESS = "0";
	public static final String LOGINFLAG_FAILED = "-1";
	
	protected MyConfig myConfig;
	
	@Autowired
	protected IAdminService adminService;
	
	@Autowired
	protected IOrderService orderService;
	
	protected Admin getLoginAdminFromSession(HttpServletRequest request){
		Admin admin = (Admin)request.getSession(true).getAttribute(MyConfig.LOGIN_USER);
		return admin;
	}

	String states[] = {
			"北京", "天津","上海","重庆","河北",
			"山西","河南","辽宁","吉林","黑龙江",
			"内蒙古","江苏","山东","安徽","浙江",
			"福建","湖北","湖南","广东","广西",
			"江西","四川","贵州","云南","西藏",
			"海南","陕西","甘肃","宁夏","青海","新疆"
			};

	public String GetState(String idx) {
		String ret = "未知";
		try {
			int i = Integer.parseInt(idx);
			if (i > 0 && i < 32) {
				ret = states[i - 1];
			}
		} catch (Exception e) {
		}

		return ret;
	}
	
	public String getState(String idex) {
		int i = -1;
		String ret = "未知";
		try {
			if (idex.matches("^[0-9]*$")) {
				i = Integer.parseInt(idex);
			} else {
				if ("a".equals(idex)) {
					i = 10;
				} else if ("b".equals(idex)) {
					i = 11;
				} else if ("c".equals(idex)) {
					i = 12;
				} else if ("d".equals(idex)) {
					i = 13;
				} else if ("e".equals(idex)) {
					i = 14;
				} else if ("f".equals(idex)) {
					i = 15;
				} else if ("g".equals(idex)) {
					i = 16;
				} else if ("h".equals(idex)) {
					i = 17;
				} else if ("i".equals(idex)) {
					i = 18;
				} else if ("j".equals(idex)) {
					i = 19;
				} else if ("k".equals(idex)) {
					i = 20;
				} else if ("l".equals(idex)) {
					i = 21;
				} else if ("m".equals(idex)) {
					i = 22;
				} else if ("n".equals(idex)) {
					i = 23;
				} else if ("o".equals(idex)) {
					i = 24;
				} else if ("p".equals(idex)) {
					i = 25;
				} else if ("q".equals(idex)) {
					i = 26;
				} else if ("r".equals(idex)) {
					i = 27;
				} else if ("s".equals(idex)) {
					i = 28;
				} else if ("t".equals(idex)) {
					i = 29;
				} else if ("u".equals(idex)) {
					i = 30;
				} else if ("v".equals(idex)) {
					i = 31;
				} else if ("w".equals(idex)) {
					i = 32;
				} else if ("x".equals(idex)) {
					i = 33;
				} else if ("y".equals(idex)) {
					i = 34;
				} else if ("z".equals(idex)) {
					i = 35;
				} 
			}
			if (i > 0 && i < 32) {
				ret = states[i - 1];
			}
		} catch (Exception e) {
		}

		return ret;
	}
	
	public int getChnl(String idex) {
		int i = -1;
		try {
			if (idex.matches("^[0-9]*$")) {
				i = Integer.parseInt(idex);
			} else {
				if ("a".equals(idex)) {
					i = 10;
				} else if ("b".equals(idex)) {
					i = 11;
				} else if ("c".equals(idex)) {
					i = 12;
				} else if ("d".equals(idex)) {
					i = 13;
				} else if ("e".equals(idex)) {
					i = 14;
				} else if ("f".equals(idex)) {
					i = 15;
				} else if ("g".equals(idex)) {
					i = 16;
				} else if ("h".equals(idex)) {
					i = 17;
				} else if ("i".equals(idex)) {
					i = 18;
				} else if ("j".equals(idex)) {
					i = 19;
				} else if ("k".equals(idex)) {
					i = 20;
				} else if ("l".equals(idex)) {
					i = 21;
				} else if ("m".equals(idex)) {
					i = 22;
				} else if ("n".equals(idex)) {
					i = 23;
				} else if ("o".equals(idex)) {
					i = 24;
				} else if ("p".equals(idex)) {
					i = 25;
				} else if ("q".equals(idex)) {
					i = 26;
				} else if ("r".equals(idex)) {
					i = 27;
				} else if ("s".equals(idex)) {
					i = 28;
				} else if ("t".equals(idex)) {
					i = 29;
				} else if ("u".equals(idex)) {
					i = 30;
				} else if ("v".equals(idex)) {
					i = 31;
				} else if ("w".equals(idex)) {
					i = 32;
				} else if ("x".equals(idex)) {
					i = 33;
				} else if ("y".equals(idex)) {
					i = 34;
				} else if ("z".equals(idex)) {
					i = 35;
				} 
			}
		} catch (Exception e) {
		}
		return i;
	}
	
	public int checkArpu(int MAX_R_FEE,int MAX_Y_FEE, String today,String mdn,int fee) {
		
//		int ret = 0;
//		
//		int r_fee = 0;
//	    int y_fee = 0;
//		
//		try {
//			UserLimit userLimit = orderService.getUserLimitByMdn(mdn);
//			
//			if (userLimit == null) {
//				userLimit = new UserLimit();
//				r_fee = 0;
//				y_fee = 0;
//				
//				r_fee = orderService.sumDayFee(today, mdn) != null ? orderService.sumDayFee(today, mdn).intValue() : 0;
//				y_fee = orderService.sumMonthFee(today, mdn) != null ? orderService.sumMonthFee(today, mdn).intValue() : 0;
//				
//				r_fee = r_fee + fee;
//				y_fee = y_fee + fee;
//				
//				userLimit.setRq(Long.parseLong(today));
//				userLimit.setMdn(mdn);
//				userLimit.setR_fee(new Long(r_fee));
//				userLimit.setY_fee(new Long(y_fee));
//				
//				orderService.createUserLimit(userLimit);
//				
//			} else {
//				int hist_rq = userLimit.getRq().intValue();
//				int hist_r_fee = 0;
//				int hist_y_fee = 0;
//				
//				if (hist_rq == Integer.parseInt(today)) {
//					hist_r_fee = userLimit.getR_fee().intValue();
//				} else {
//					hist_r_fee = 0;
//				}
//				
//				if (hist_rq / 100 == Integer.parseInt(today) / 100) {
//					hist_y_fee = userLimit.getY_fee().intValue();
//				} else {
//					hist_y_fee = 0;
//				}
//				
//				hist_r_fee = hist_r_fee + fee;
//				hist_y_fee = hist_y_fee + fee;
//				
//				if ( (hist_r_fee >= MAX_R_FEE) || (hist_y_fee >= MAX_Y_FEE) )
//				{
////				  ret = 1;
//				}
//				
//				userLimit.setRq(Long.parseLong(today));
//				userLimit.setR_fee(new Long(hist_r_fee));
//				userLimit.setY_fee(new Long(hist_y_fee));
//				
//				orderService.updateUserLimit(userLimit);
//				
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("checkArpu exception : " + e.getMessage());
//		}
		
		return 0;
	}
	
	synchronized protected int shouldKouliangByState(String today, int chnl_id, String consumecode, String state, String packageid) {
		int id = 0;

	    int baseline = 0;
	    int curcnt = 0;
	    double percent = 0;
	    String workday = "";

	    int skipcnt = 0;
	    int dec_flag = 0;
		
	    try {
	    	ChnlDecPercent chnlDecPercent = orderService.queryDecPercent(chnl_id, consumecode, packageid, today);
	    	
	    	if (chnlDecPercent != null) {
	    		id = chnlDecPercent.getId().intValue();
	    		baseline = chnlDecPercent.getBaseline();
	    		percent = chnlDecPercent.getPercent();
	    		workday = chnlDecPercent.getCurday();
	    		
	    		if (!today.equals(workday)) {
	    			orderService.delPercent2(chnl_id, consumecode, packageid);
	    			orderService.createPercent2(chnl_id, consumecode, packageid, today, percent, baseline);
	    			orderService.updatePercent(today, new Long(id));
	    			
	    			curcnt = 1;
	    		} else {
	    			chnlDecPercent = orderService.queryDecPercent2(chnl_id, consumecode, packageid, state);
	    			
	    			if (chnlDecPercent != null) {
	    				curcnt = chnlDecPercent.getCurcnt() + 1;
	    			} else {
	    				curcnt = 1;
	    			}
	    		}
	    		
	    		if (curcnt > baseline) 
	    		{
	    		  if ( percent > 0 )
	    		  {
	    			  skipcnt = (int)Math.ceil(100d / percent);
	    			  if ( curcnt >= skipcnt && curcnt % skipcnt == 0) {
	    				dec_flag = 1;
	    			  }
	    		  }
	            }
	    		
	    		orderService.updatePercent2(chnl_id, consumecode, packageid, state);
	    	}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ShouldKouliangByState error : " + e.getMessage());
		}
		
		return dec_flag;
	}
	
	/**
	 * 查询登录用户权限
	 * @param request
	 */
	protected void URIForUserInToSession(HttpServletRequest request){
		
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute(MyConfig.LOGIN_USER);
		
		if (obj != null) { //已登录
			Admin admin = (Admin) obj;
			//查询用户角色
			List<AdminRoleMap> roleList = adminService.queryRoleByAdminId(admin.getId());
			
			if (roleList != null && roleList.size() > 0) {
				List<RoleActionMap> roleActionList = new ArrayList<RoleActionMap>();
				for (AdminRoleMap adminRoleMap : roleList) {
					List<RoleActionMap> rAList = adminService.queryRoleActionMapByRoleId(adminRoleMap.getRole().getId(), Component.COLUMN_VALUE_KINDTYPE_OPMNG, null);
					
					if (rAList != null && rAList.size() > 0) { //有操作权限
						roleActionList.addAll(rAList);
					}
				}
				
				request.getSession(true).setAttribute(MyConfig.PURVIEW_SESSION, roleActionList);
				
				List<RoleActionMap> tmpList = null;
				Map<Long, List<RoleActionMap>> map = new HashMap<Long, List<RoleActionMap>>();
				for (RoleActionMap ram : roleActionList) {
					if (map.get(ram.getComponent().getId()) != null) { //组件已存在
						map.get(ram.getComponent().getId()).add(ram);
					} else { //组件不存在
						tmpList = new ArrayList<RoleActionMap>();
						tmpList.add(ram);
						map.put(ram.getComponent().getId(), tmpList);
					}
				}
				request.setAttribute("componentActionMap",  map);
			}
		}
	}
	
	protected String getStateByMobile(String mobile) {
		String state = "00";
		
		int state_int = orderService.getStateIdByMobile(mobile);
		
		if (state_int == 0) {
			try {
				String cityStr = MobileFromUtil.getMobileFrom(mobile);
				
				String[] array = cityStr.split(",");
				String provinceName = array[0];
				state_int = Integer.parseInt(Methods.getStateId(provinceName));
				
				orderService.createSmsHaoduan(mobile.substring(0, 7), state_int);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if ( state_int < 10 )
		{
			state = "0" + state_int;
		}else
		{
			state = "" + state_int;
		}
		
		return state;
	}
	
	protected String getSsoUrl(String payUrl) {
        String line;
        String ssoUrl = null;

        try {
            URL url = new URL(payUrl);
            URLConnection conn;
            conn = url.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),
                            "utf-8"));

            while ((line = reader.readLine()) != null) {
                if (line.indexOf("var ssoUrl") > 0) {
                	ssoUrl = line.trim().substring(14, line.indexOf(";") - 3);
                	System.out.println("ssourl:" + ssoUrl);
                }
                if (ssoUrl != null) {
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ssoUrl;
    }
	
	protected String[] getDmINfo(String dmUrl) {
        String line;
        String str[] = new String[2];
        String str12 = "";
        String str1 = null;
        String str2 = null;
        String str3 = null;

        try {
            URL url = new URL(dmUrl);
            URLConnection conn;
            conn = url.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),
                            "utf-8"));

            while ((line = reader.readLine()) != null) {
                str12 = str12 + line;
                if (line.indexOf("var SessionId") > 0) {

                    str1 = line.trim().substring(16, line.indexOf(";") - 1);
                } else if (line.indexOf("var orderId") > 0) {
                    str2 = line.trim().substring(14, line.indexOf(";") - 1);
                }
                if (line.indexOf("var ssoUrl") > 0) {
                	str3 = line.trim().substring(14, line.indexOf(";") - 3);
                	System.out.println("ssourl:  " + str3);
                }
                if (str2 != null && str1 != null) {
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        str[0] = str1;
        str[1] = str2;

        return str;
    }
}
