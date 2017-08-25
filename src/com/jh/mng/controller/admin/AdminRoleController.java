package com.jh.mng.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jh.mng.common.Config;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.Admin;
import com.jh.mng.pojo.AdminRoleMap;
import com.jh.mng.pojo.Component;
import com.jh.mng.pojo.Role;
import com.jh.mng.pojo.RoleActionMap;
import com.jh.mng.pojo.UriAction;
import com.jh.mng.service.CommonService;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.RequestUtil;
import com.jh.mng.util.page.Page;
import com.jh.mng.util.page.PagedModelList;

@Controller
@RequestMapping("/admin")
public class AdminRoleController extends AbstractMultiActionController{
private final static Logger logger = Logger.getLogger(AdminRoleController.class);
	
	/**
	 * 用户管理首页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/index.do")
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response) {
		return new ModelAndView("/admin/adminList.vm");
	}
	

//	/**
//	 * 跳转到新增用户信息页面
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/preAddAdmin.do")
//	public ModelAndView preAddAdmin(HttpServletRequest request,HttpServletResponse response){
//		//CP信息
//		List<CpInfo> cpInfoList = cpService.queryAllCpInfo();
//		request.setAttribute("cpInfoList", cpInfoList);
//		
//		//角色信息
//		List<Role> roleList = adminService.queryAllRoleInfo();
//		request.setAttribute("roleList", roleList);
//		
//		return new ModelAndView("/admin/addAdmin.vm");
//	}
	
	/**
	 * 跳转到修改用户信息页面
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/preUpdateAdmin.do")
//	public ModelAndView preUpdateAdmin(HttpServletRequest request,HttpServletResponse response){
//		Long id = RequestUtil.GetParamLong(request,"id");
//		if(id != null){
//			//用户信息
//			Admin adminInfo = adminService.getAdminMoreById(id);
//			request.setAttribute("adminInfo", adminInfo);
//			
//			//CP信息
//			List<CpInfo> cpInfoList = cpService.queryAllCpInfo();
//			request.setAttribute("cpInfoList", cpInfoList);
//			
//			//角色信息
//			List<Role> roleList = adminService.queryAllRoleInfo();
//			request.setAttribute("roleList", roleList);
//		}
//		return new ModelAndView("/admin/adminInfo.vm");
//	}
	
	/**
	 * 查询用户信息列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/queryAdminList.do")
	public ModelAndView queryAdminList(HttpServletRequest request,HttpServletResponse response) {
		String userName = RequestUtil.GetParamString(request, "userName", null);
		Integer userType = RequestUtil.GetParamInteger(request, "userType", null);
		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
		String beginTime = RequestUtil.GetParamString(request, "beginTime", null);
		String endTime = RequestUtil.GetParamString(request, "endTime", null);
		
		request.setAttribute("_userName", userName);
		request.setAttribute("_userType", userType);
		request.setAttribute("_beginTime", beginTime);
		request.setAttribute("_endTime", endTime);
		
		Date start = null;
		if (beginTime != null && !beginTime.isEmpty()) {
			start = DateTool.StringToDate(beginTime + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
		}
		Date end = null;
		if (endTime != null && !endTime.isEmpty()) {
			end = DateTool.StringToDate(endTime + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		}
		
		Admin adminInfo = new Admin();
		adminInfo.setName(userName);
		adminInfo.setType(userType);
		adminInfo.setBeginTime(start);
		adminInfo.setEndTime(end);
		
		Page<Admin> page = new Page<Admin>();
		page.setPageNo(pageId);
		page.setPageSize(Config.get().getInt("pageSize", 200));
		
		List<Admin> list = adminService.queryAdminByConditions(page, adminInfo);
		
		PagedModelList<Admin> pagedModelList = new PagedModelList<Admin>(pageId, Config.get().getInt("pageSize", 200), page.getTotalRecord());
		
		request.setAttribute("adminList", list);
		request.setAttribute("pagedModelList", pagedModelList);
		return new ModelAndView("/admin/adminList.vm");
	}
	
	/**
	 * 新增用户信息
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/addAdmin.do")
//	public ModelAndView addAdmin(HttpServletRequest request,HttpServletResponse response) {
//		String userName = RequestUtil.GetParamString(request, "userName", null);
//		String pad = RequestUtil.GetParamString(request, "psd", null);
//		Integer userType = RequestUtil.GetParamInteger(request, "userType", null);
//		Integer status = RequestUtil.GetParamInteger(request, "status", null);
//		String realName = RequestUtil.GetParamString(request, "realName", null);
//		String idCard = RequestUtil.GetParamString(request, "idCard", null);
//		String tel = RequestUtil.GetParamString(request, "tel", null);
//		String email = RequestUtil.GetParamString(request, "email", null);
//		String address = RequestUtil.GetParamString(request, "address", null);
//		Integer isRoot = 0;
//		Long roleId = RequestUtil.GetParamLong(request, "roleId",null);
//		Long cpId = RequestUtil.GetParamLong(request, "cpId", null);
//		Admin admin = this.getLoginAdminFromSession(request);
//
//		CpInfo cpInfo = new CpInfo();
//		cpInfo.setId(cpId);
//
//		Admin adminInfo = new Admin();
//		adminInfo.setName(userName);
//		adminInfo.setPassword(MD5Util.getMD5(pad));
//		adminInfo.setType(userType);
//		adminInfo.setStatus(status);
//		adminInfo.setRealName(realName);
//		adminInfo.setIdCard(idCard);
//		adminInfo.setTel(tel);
//		adminInfo.setEmail(email);
//		adminInfo.setAddress(address);
//		adminInfo.setIsRoot(isRoot);
//		adminInfo.setCpInfo(cpInfo);
//		adminInfo.setCreater(admin.getName());
//
//		AdminRoleMap adminRoleMap = new AdminRoleMap();
//		Role role = new Role();
//		role.setId(roleId);
//		adminRoleMap.setAdmin(adminInfo);
//		adminRoleMap.setRole(role);
//		adminRoleMap.setStatus(1);
//		adminRoleMap.setCreater(admin.getName());
//
//		try {
//			Long id = adminService.createAdmin(adminInfo,adminRoleMap);
//			if (id != null && id.intValue() > 0) {
//				request.setAttribute("msg", "操作成功！");
//			} else {
//				request.setAttribute("msg", "操作失败！");
//			}
//		} catch (Exception e) {
//			request.setAttribute("msg", "数据报错异常！");
//		}
//		return new ModelAndView("/result.vm");
//	}
	
	/**
	 * 修改用户信息
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/updateAdmin.do")
//	public ModelAndView updateAdmin(HttpServletRequest request,HttpServletResponse response) {
//		Long id = RequestUtil.GetParamLong(request, "id");
//		String userName = RequestUtil.GetParamString(request, "userName", null);
//		Integer userType = RequestUtil.GetParamInteger(request, "userType", null);
//		Integer status = RequestUtil.GetParamInteger(request, "status", null);
//		String realName = RequestUtil.GetParamString(request, "realName", null);
//		String idCard = RequestUtil.GetParamString(request, "idCard", null);
//		String tel = RequestUtil.GetParamString(request, "tel", null);
//		String email = RequestUtil.GetParamString(request, "email", null);
//		String address = RequestUtil.GetParamString(request, "address", null);
//		Integer isRoot = 0;
//		Long roleId = RequestUtil.GetParamLong(request, "roleId",null);
//		Long cpId = RequestUtil.GetParamLong(request, "cpId", null);
//		Admin admin = this.getLoginAdminFromSession(request);
//		
//		CpInfo cpInfo = new CpInfo();
//		cpInfo.setId(cpId);
//		
//		Admin adminUpdate = new Admin();
//		adminUpdate.setId(id);
//		adminUpdate.setName(userName);
//		adminUpdate.setType(userType);
//		adminUpdate.setStatus(status);
//		adminUpdate.setRealName(realName);
//		adminUpdate.setIdCard(idCard);
//		adminUpdate.setTel(tel);
//		adminUpdate.setEmail(email);
//		adminUpdate.setAddress(address);
//		adminUpdate.setIsRoot(isRoot);
//		adminUpdate.setCpInfo(cpInfo);
//		adminUpdate.setUpdater(admin.getName());
//		
//		//记录操作日志
//		StringBuffer sb = new StringBuffer();
//		Admin adminInfo = adminService.getAdminById(id);
//		sb.append("管理员：" + admin.getName() + " 修改了 id:" + id + " 的信息，修改前为：" + adminInfo.toString());
//		
//		AdminRoleMap adminRoleMap = new AdminRoleMap();
//		Role role = new Role();
//		role.setId(roleId);
//		
//		adminRoleMap.setAdmin(adminInfo);
//		adminRoleMap.setRole(role);
//		adminRoleMap.setStatus(1);
//		adminRoleMap.setCreater(admin.getName());
//		adminRoleMap.setUpdater(admin.getName());
//		try {
//			int result = adminService.updateAdmin(adminUpdate,adminRoleMap);
//			sb.append("修改了：" + adminUpdate.toString());
//			logger.info(sb.toString());
//			
//			if (result == CommonService.RESULT_UPDATE_DEL_SUCCESS) {
//				request.setAttribute("msg", "操作成功！");
//			} else {
//				request.setAttribute("msg", "操作失败！");
//			}
//		} catch (Exception e) {
//			request.setAttribute("msg", "数据保存异常！");
//		}
//		return new ModelAndView("/result.vm");
//	}
	
	/**
	 * 删除用户信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/deleteAdmin.do")
	public ModelAndView deleteAdmin(HttpServletRequest request,HttpServletResponse response){
		Long id = RequestUtil.GetParamLong(request, "id");
		
		//记录操作日志
		Admin admin = this.getLoginAdminFromSession(request);
		StringBuffer sb = new StringBuffer();
		Admin adminInfo = adminService.getAdminById(id);
		sb.append("管理员：" + admin.getName() + " 删除了 adminId:" + id + " 的信息，删除前为：" + adminInfo.toString());
		
		try {
			int result = adminService.deleteAdminById(id);
			
			sb.append("删除结果为：" + result);
			logger.info(sb.toString());
			
			if(result == CommonService.RESULT_UPDATE_DEL_SUCCESS){
				request.setAttribute("msg", "操作成功！");
			}else {
				request.setAttribute("msg","操作失败！");
			}
		} catch (Exception e) {
			request.setAttribute("msg","删除数据发生异常！");
		}
		return new ModelAndView("/result.vm");
	}
	
	/**
	 * 角色管理首页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/roleIndex.do")
	public ModelAndView roleIndex(HttpServletRequest request,HttpServletResponse response) {
		return new ModelAndView("/admin/roleList.vm");
	}
	

	/**
	 * 跳转到修改角色信息页面
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/preUpdateRole.do")
//	public ModelAndView preUpdateRole(HttpServletRequest request,HttpServletResponse response){
//		Long id = RequestUtil.GetParamLong(request,"id");
//		if(id != null){
//			Role roleInfo = adminService.getRoleById(id);
//			request.setAttribute("roleInfo", roleInfo);
//			
//			List<CpInfo> cpInfoList = cpService.queryAllCpInfo();
//			request.setAttribute("cpInfoList", cpInfoList);
//			
//			List<Component> componentList = adminService.queryAllComponent();
//			request.setAttribute("componentList", componentList);
//
//			List<UriAction> uriActionList = adminService.queryAllUriAction();
//			request.setAttribute("uriActionList", uriActionList);
//			
//			List<RoleActionMap> uriActionCheckList = adminService.queryRoleActionMapByRoleId(id,null,null);
//			request.setAttribute("uriActionCheckList", uriActionCheckList);
//		}
//		return new ModelAndView("/admin/roleInfo.vm");
//	}

	/**
	 * 跳转到新增角色信息页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/preAddRole.do")
	public ModelAndView preAddRole(HttpServletRequest request,HttpServletResponse response){
		List<Component> componentList = adminService.queryAllComponent();
		request.setAttribute("componentList", componentList);
		
		List<UriAction> uriActionList = adminService.queryAllUriAction();
		request.setAttribute("uriActionList", uriActionList);
		
		return new ModelAndView("/admin/addRole.vm");
	}

	/**
	 * 查询角色信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/queryRoleList.do")
	public ModelAndView queryRoleList(HttpServletRequest request,HttpServletResponse response) {
		String userName = RequestUtil.GetParamString(request, "name", null);
		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
		String beginTime = RequestUtil.GetParamString(request, "beginTime", null);
		String endTime = RequestUtil.GetParamString(request, "endTime", null);
		
		request.setAttribute("_name", userName);
		request.setAttribute("_beginTime", beginTime);
		request.setAttribute("_endTime", endTime);
		
		Date start = null;
		if (beginTime != null && !beginTime.isEmpty()) {
			start = DateTool.StringToDate(beginTime + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
		}
		Date end = null;
		if (endTime != null && !endTime.isEmpty()) {
			end = DateTool.StringToDate(endTime + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		}
		
		Role roleInfo = new Role();
		roleInfo.setName(userName);
		roleInfo.setBeginTime(start);
		roleInfo.setEndTime(end);
		
		Page<Role> page = new Page<Role>();
		page.setPageNo(pageId);
		page.setPageSize(Config.get().getInt("pageSize", 200));
		
		List<Role> list = adminService.queryRoleByConditions(page, roleInfo);
		
		PagedModelList<Role> pagedModelList = new PagedModelList<Role>(pageId, Config.get().getInt("pageSize", 200), page.getTotalRecord());
		
		request.setAttribute("roleList", list);
		request.setAttribute("pagedModelList", pagedModelList);
		return new ModelAndView("/admin/roleList.vm");
	}

	/**
	 * 新增角色信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/addRole.do")
	public ModelAndView addRole(HttpServletRequest request,HttpServletResponse response) {
		String name = RequestUtil.GetParamString(request, "name", null);
		String description = RequestUtil.GetParamString(request, "description", null);
		Integer status = RequestUtil.GetParamInteger(request, "status", 0);
		String[] uriActionCheckList = request.getParameterValues("uriActionCheck");
		Admin admin = this.getLoginAdminFromSession(request);
		
		//构造角色信息
		Role roleInfo = new Role();
		roleInfo.setName(name);
		roleInfo.setDescription(description);
		roleInfo.setStatus(status);
		roleInfo.setCreater(admin.getName());
		
		//构造操作方法信息
		List<RoleActionMap> roleActionList = new ArrayList<RoleActionMap>();
		if(uriActionCheckList!=null&&uriActionCheckList.length>0){
			for(String uriActionStr : uriActionCheckList){
				String[] roleActions = uriActionStr.split("_");
				if(roleActions.length!=3) continue;
				Long actionId = Long.valueOf(roleActions[0]);
				Long uriId = Long.valueOf(roleActions[1]);
				Long componentId = Long.valueOf(roleActions[2]);
				RoleActionMap roleActionMap = new RoleActionMap();
				UriAction uriAction = new UriAction();
				uriAction.setId(actionId);

				Component component = new Component();
				component.setId(componentId);

				roleActionMap.setUriAction(uriAction);
				roleActionMap.setUriId(uriId);
				roleActionMap.setComponent(component);
				roleActionMap.setStatus(1);
				roleActionMap.setCreater(admin.getName());
				roleActionList.add(roleActionMap);
			}
		}
		
		try {
			Long id = adminService.createRole(roleInfo,roleActionList);

			if (id != null && id.intValue() > 0) {
				request.setAttribute("msg", "操作成功！");
			} else {
				request.setAttribute("msg", "操作失败！");
			}
		} catch (Exception e) {
			request.setAttribute("msg", "数据保存异常！");
		}

		return new ModelAndView("/result.vm");
	}
	
	/**
	 * 修改角色信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/updateRole.do")
	public ModelAndView updateRole(HttpServletRequest request,HttpServletResponse response) {
		Long id = RequestUtil.GetParamLong(request, "id");
		String name = RequestUtil.GetParamString(request, "name", null);
		String description = RequestUtil.GetParamString(request, "description", null);
		Integer status = RequestUtil.GetParamInteger(request, "status", 0);
		String[] uriActionCheckList = request.getParameterValues("uriActionCheck");
		Admin admin = this.getLoginAdminFromSession(request);
		
		//构造角色信息
		Role roleUpdate = new Role();
		roleUpdate.setId(id);
		roleUpdate.setName(name);
		roleUpdate.setDescription(description);
		roleUpdate.setStatus(status);
		roleUpdate.setUpdater(admin.getName());
		
		//构造操作方法信息
		List<RoleActionMap> roleActionList = new ArrayList<RoleActionMap>();
		if(uriActionCheckList!=null&&uriActionCheckList.length>0){
			for(String uriActionStr : uriActionCheckList){
				String[] roleActions = uriActionStr.split("_");
				if(roleActions.length!=3) continue;
				Long actionId = Long.valueOf(roleActions[0]);
				Long uriId = Long.valueOf(roleActions[1]);
				Long componentId = Long.valueOf(roleActions[2]);
				RoleActionMap roleActionMap = new RoleActionMap();
				UriAction uriAction = new UriAction();
				uriAction.setId(actionId);

				Component component = new Component();
				component.setId(componentId);

				roleActionMap.setUriAction(uriAction);
				roleActionMap.setUriId(uriId);
				roleActionMap.setComponent(component);
				roleActionMap.setStatus(1);
				roleActionMap.setCreater(admin.getName());
				roleActionList.add(roleActionMap);
			}
		}
		
		//记录操作日志
		StringBuffer sb = new StringBuffer();
		Role roleInfo = adminService.getRoleById(id);
		sb.append("管理员：" + admin.getName() + " 修改了 id:" + id + " 的信息，修改前为：" + roleInfo.toString());
		
		try {
			int result = adminService.updateRole(roleUpdate,roleActionList);
			sb.append("修改了：" + roleUpdate.toString());
			logger.info(sb.toString());

			if (result == CommonService.RESULT_UPDATE_DEL_SUCCESS) {
				request.setAttribute("msg", "操作成功！");
			} else {
				request.setAttribute("msg", "操作失败！");
			}
		} catch (Exception e) {
			request.setAttribute("msg", "数据报错异常！");
		}
		return new ModelAndView("/result.vm");
	}
	
	/**
	 * 删除角色信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/deleteRole.do")
	public ModelAndView deleteRole(HttpServletRequest request,HttpServletResponse response){
		Long id = RequestUtil.GetParamLong(request, "id");
		
		//判断该角色下是否有建用户
		List<AdminRoleMap> adminRoleMapList = adminService.queryAdminRoleMapByRoleId(id);
		if(adminRoleMapList.size()>0){
			request.setAttribute("msg", "该角色下已建有用户，不可删除");
		}else {
			//记录操作日志
			Admin admin = this.getLoginAdminFromSession(request);
			StringBuffer sb = new StringBuffer();
			Role roleInfo = adminService.getRoleById(id);
			sb.append("管理员：" + admin.getName() + " 删除了 roleId:" + id + " 的信息，删除前为：" + roleInfo.toString());

			try {
				int result = adminService.deleteRoleById(id);

				sb.append("删除结果为：" + result);
				logger.info(sb.toString());

				if(result == CommonService.RESULT_UPDATE_DEL_SUCCESS){
					request.setAttribute("msg", "操作成功！");
				}else {
					request.setAttribute("msg","操作失败！");
				}
			} catch (Exception e) {
				request.setAttribute("msg", "数据保存异常！");
			}
		}
		return new ModelAndView("/result.vm");
	}
}
