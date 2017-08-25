package com.jh.mng.util.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.jh.mng.config.MyConfig;
import com.jh.mng.pojo.Admin;
import com.jh.mng.pojo.AdminRoleMap;
import com.jh.mng.pojo.Component;
import com.jh.mng.pojo.RoleActionMap;
import com.jh.mng.service.admin.IAdminService;


/**
 * 
 * @ClassName: RequestDiagnosticFilter
 * @Description: 页面响应时间过滤器
 * @author gs
 * @date Nov 10, 2011 4:07:39 PM
 * 
 */

public class URIFilter implements Filter {
//	private Logger log = Logger.getLogger(URIFilter.class);
	
	private IAdminService adminService;
	private String[] excepts = null;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		
		WebApplicationContext wac;
		wac = WebApplicationContextUtils.getWebApplicationContext(
				filterConfig.getServletContext());
		String adminServiceName = filterConfig.getInitParameter("adminServiceName");
		if((adminServiceName != null)
				&& (adminServiceName.trim().length() > 0)){
			this.adminService = (IAdminService)wac.getBean(adminServiceName);
		}
		excepts = filterConfig.getInitParameter("exceptOption").split(",");
		
	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// 设置请求的字符编码
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 获取到当前URI
		String uri = req.getRequestURI();
		String uriPath = uri.split("/")[2];
		String action = uri.split("/")[3];
		String checkStr = uriPath + "/" + action;
		uri = uri.split("/")[2];
		String queryString = req.getQueryString();
//		log.info("*** the URI is: " + uri + ",op=" + queryString);

		if ((queryString != "") && (queryString != null)) {
			uri = uri + "?" + queryString;
		}

		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute(MyConfig.LOGIN_USER);
		// 判断Session中的对象是否为空；判断请求的URI是否为不允许过滤的URI
		if (uri == null && "".equals(uri)) {
			chain.doFilter(request, response);
			return;
		} else {
			boolean flag = false;
			for (int i = 0; i < excepts.length; i++) {
				if (checkStr.startsWith(excepts[i]) || checkStr.startsWith("resource")) { // 不在过滤范围内
					flag = true;
					break;
				}
			}
			if (flag) {
				chain.doFilter(request, response);
				return;
			} else {
				if (obj == null || checkStr.indexOf(".do") < 0) {
					res.sendRedirect(req.getContextPath()
							+ "/login/error.do");
					return;
				}
				Admin userInfo = (Admin) obj;
				if (session.getAttribute(MyConfig.PURVIEW_SESSION) == null) { // session没有用户权限
					List<AdminRoleMap> roleList = adminService.queryRoleByAdminId(userInfo.getId());
					List<RoleActionMap> theRoleActionList = new ArrayList<RoleActionMap>();
					for (AdminRoleMap adminRole : roleList) {
						List<RoleActionMap> aRoleActionList = adminService.queryRoleActionMapByRoleId(adminRole.getId(), Component.COLUMN_VALUE_KINDTYPE_OPMNG, null);
						if ((aRoleActionList != null)
								&& (aRoleActionList.size() > 0)) {
							theRoleActionList.addAll(aRoleActionList);
						}
					}
					session.setAttribute(MyConfig.PURVIEW_SESSION,
							theRoleActionList);
				}
				List<RoleActionMap> actionList = (List<RoleActionMap>) session
						.getAttribute(MyConfig.PURVIEW_SESSION);

				if (URIValidate(session, userInfo, actionList, uriPath, action.split("\\.")[0])) {
					chain.doFilter(request, response);
					return;
				} else {
					res.sendRedirect(req.getContextPath()
							+ "/login/error.do");
					return;
				}
			}
		}
	}

	public void destroy() {
	}
	
	@SuppressWarnings("unchecked")
	public boolean URIValidate(HttpSession session, Admin admin, List<RoleActionMap> list, String uriStr, String queryStr) {
		boolean result = false;
		List<RoleActionMap> actionList = (List<RoleActionMap>) session.getAttribute(MyConfig.PURVIEW_SESSION);
		RoleActionMap roleActionMap = null;
		String uriString = ""; //  表中 uri字段
		for (int i = 0; i < actionList.size(); i++) {
			roleActionMap = actionList.get(i);
			uriString = roleActionMap.getUriAction().getUri().getUri();
			if (uriStr.equals(uriString) && queryStr.equals(roleActionMap.getUriAction().getActionValue())) {
				result = true;
				break;
			} 
		}
		return result;
		
	}
}