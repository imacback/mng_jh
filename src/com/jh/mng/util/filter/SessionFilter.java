package com.jh.mng.util.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jh.mng.config.MyConfig;
import com.jh.mng.pojo.Admin;


/**
 * 
 * @ClassName: SessionFilter
 * @Description: 用户登录session过滤器
 * @author T410
 * @date Nov 10, 2011 4:08:12 PM
 * 
 */
public class SessionFilter implements Filter {

	private static final Logger logger = Logger.getLogger(SessionFilter.class);

	private String[] except = { "login", "ds"};

	private String indexPage = "login/index.do";

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// 获取账号信息UserInfo
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse)response;
		
		if (logger.isDebugEnabled()) {
			logger.debug("req.getRequestURI():" + req.getRequestURI());
		}
		String uri = req.getRequestURI();
		String uriPath = uri.substring(0, uri.lastIndexOf("/"));
		
		String applicatonName = req.getContextPath();
		for (int i = 0; i < this.except.length; i++) {
			if ((uriPath).equals(applicatonName + "/" + this.except[i])) {
				chain.doFilter(request, response);
				return;
			}
		}
		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute(MyConfig.LOGIN_USER);
		if (null != obj) {
			Admin userInfo = (Admin) obj;
			if (userInfo != null) {
				request.setAttribute(MyConfig.LOGIN_USER, userInfo);
			} else {
				redirectToLogin(req, res);	
			}
		} else {
			redirectToLogin(req, res);
			return;
		}
		chain.doFilter(request, response);
	}
	
	protected void redirectToLogin(HttpServletRequest req, 
			HttpServletResponse res){
		java.io.PrintWriter out = null;
		try{
			out = res.getWriter();
			out.println("<html>");
			out.println("<script>");
			out.println("top.location='" + req.getContextPath() + "/" + this.indexPage + "';");
			out.println("</script>");
			out.println("</html>");
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
		return;
	}

	public void init(FilterConfig arg0) throws ServletException {

	}

}
