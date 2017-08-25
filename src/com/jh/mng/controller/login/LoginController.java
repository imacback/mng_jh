package com.jh.mng.controller.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jh.mng.config.MyConfig;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.Admin;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.MD5Util;
import com.jh.mng.util.Methods;
import com.jh.mng.util.RequestUtil;


@Controller
@RequestMapping("/login")
public class LoginController extends AbstractMultiActionController{

	private static final Logger logger = Logger.getLogger(LoginController.class);
	
	private String adminMainPage = "/login/admin.vm";
	private String leftPage = "/login/left.vm";
	private String rightPage = "/login/right.vm";
	private String topPage = "/login/top.vm";
	private String indexPage = "login/index.do";
	private String updatePassword = "/login/updatePassword.vm";
	private String updateOK = "/login/updateOK.vm";
	
	/**
	 * 首页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/index.do")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		return login( request, response);
	}
	
	/**
	 * 用户登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/login.do")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		
		try {
			HttpSession session = request.getSession(true);
			Object object = session.getAttribute(MyConfig.LOGIN_USER);
			
			if (object != null) {
				return new ModelAndView(adminMainPage);
			} else {
				
				String currentYear = DateTool.getCurrentDate("yyyy");
				request.setAttribute("currentYear", currentYear);
				
				String userName = RequestUtil.GetParamString(request, "username", null);
				String password = RequestUtil.GetParamString(request, "password", null);
				
				if (Methods.isEmpty(userName) || Methods.isEmpty(password)) {
					request.setAttribute("showmsg", "用户名密码不能为空,请重试！");
					return new ModelAndView("/login/login.vm");
				} else {
					password = MD5Util.getUpperCaseMd5(password);
					Admin admin = adminService.getAdminByUserNameAndPwd(userName, password, Admin.COLUMN_VALUE_STATUS_ACTIVE);
					
					if (admin == null) {
						request.setAttribute("showmsg", "用户名密码错误,请重试！");
						return new ModelAndView("/login/login.vm");
					} else {
						session.setAttribute(MyConfig.LOGIN_USER, admin);
						return new ModelAndView(adminMainPage);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return new ModelAndView("error.vm");
		}
	}

	/**
	 * 左侧菜单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/showLeft.do")
	public ModelAndView showLeft(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute(MyConfig.LOGIN_USER);
		if (obj != null) {
			Admin userInfo = (Admin) obj;
			request.setAttribute("username", userInfo.getName());
		}
		URIForUserInToSession(request);
		return new ModelAndView(leftPage);
	}

	/**
	 * 右侧区域
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/showRight.do")
	public ModelAndView showRight(HttpServletRequest request,
			HttpServletResponse response) {
		return new ModelAndView(rightPage);
	}

	/**
	 * 顶部区域
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/showTop.do")
	public ModelAndView showTop(HttpServletRequest request,
			HttpServletResponse response) {
		Admin admin = this.getLoginAdminFromSession(request);
		request.setAttribute("admin", admin);
		return new ModelAndView(topPage);
	}
	
	/**
	 * 出错页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/error.do")
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) {
		return new ModelAndView("/error.vm");
	}
	
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/logout.do")
	public ModelAndView logout(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.getSession(true).removeAttribute(MyConfig.LOGIN_USER);
			request.getSession(true).removeAttribute(MyConfig.PURVIEW_SESSION);
			redirectToLogin(request , response);
			return null;
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			return new ModelAndView("error.vm");
		}
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
	
	@RequestMapping("/updatePassword.do")
	public ModelAndView updatePassword(HttpServletRequest request,
			HttpServletResponse response) {
		Admin admin = this.getLoginAdminFromSession(request);
		String username = admin.getName();
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");
		oldPassword = MD5Util.getUpperCaseMd5(oldPassword);
		newPassword = MD5Util.getUpperCaseMd5(newPassword);
		Admin oldAdmin = adminService.getAdminByUserNameAndPwd(username, oldPassword, null, null);
		String updatemsg = null;
		if(oldAdmin == null){
			if(oldPassword != null){
				updatemsg = "原始密码输入错误！";
				request.setAttribute("updatemsg", updatemsg);
			}
		}else if(newPassword == null){
			updatemsg = "新密码不能为空！";
			request.setAttribute("updatemsg", updatemsg);
		}else{
			int result = adminService.updateUserPwd(admin.getId(), newPassword, admin.getName());
			
			if (result == 1) {
				return new ModelAndView(updateOK);
			} else {
				updatemsg = "修改失败！";
				request.setAttribute("updatemsg", updatemsg);
			}
		}
		
		return new ModelAndView(updatePassword);
	}
	@RequestMapping("/update.do")
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) {
		return new ModelAndView(updatePassword);
	}
}
