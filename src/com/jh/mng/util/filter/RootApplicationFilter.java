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


public class RootApplicationFilter implements Filter {

//	private Logger logger = Logger.getLogger(RootApplicationFilter.class);
	// private static String[] except = { "index.htm", "login.htm", "rand.htm",
	// "logout.htm", "left.htm","list.htm", "changeStatus.htm" };

//	private static String[] except = {};
	
	private String indexPage = "login/index.do";

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		doFilterInternal(request, response, chain);
	}
	
	public void doFilterInternal(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse)response;
		
		req.getRequestDispatcher(this.indexPage).forward(req, res);
		
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
		this.indexPage = filterConfig.getInitParameter("indexPage");
	}

}
