package com.jh.mng.service.admin.impl;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jh.mng.mapper.AdminMapper;
import com.jh.mng.mapper.AdminRoleMapMapper;
import com.jh.mng.mapper.ComponentMapper;
import com.jh.mng.mapper.RoleActionMapMapper;
import com.jh.mng.mapper.RoleMapper;
import com.jh.mng.mapper.UriActionMapper;
import com.jh.mng.pojo.Admin;
import com.jh.mng.pojo.AdminRoleMap;
import com.jh.mng.pojo.Component;
import com.jh.mng.pojo.Role;
import com.jh.mng.pojo.RoleActionMap;
import com.jh.mng.pojo.UriAction;
import com.jh.mng.service.CommonService;
import com.jh.mng.service.admin.IAdminService;
import com.jh.mng.util.page.Page;


@Service("IAdminService")
public class AdminServiceImpl extends CommonService implements IAdminService {
	
	@Autowired
	private AdminMapper adminMapper;
	@Autowired
	private AdminRoleMapMapper adminRoleMapMapper;
	@Autowired
	private RoleActionMapMapper roleActionMapMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private ComponentMapper componentMapper;
	@Autowired
	private UriActionMapper uriActionMapper;

	@Override
	public Admin getAdminById(Long id) {
		Admin admin = null;
		try {
			admin = adminMapper.getAdminById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admin;
	}
	
	@Override
	public Admin getAdminMoreById(Long id) {
		Admin admin = null;
		try {
			admin = adminMapper.getAdminMoreById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admin;
	}

	@Override
	public Admin getAdminByUserNameAndPwd(String userName, String password, Integer status, Integer type) {
		Admin admin = null;
		
		try {
			Admin params = new Admin();
			params.setName(userName);
			params.setPassword(password);
			params.setStatus(status);
			params.setType(type);
			admin = adminMapper.getAdminByContidions(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admin;
	}
	
	@Override
	public Admin getAdminByUserNameAndPwd(String userName, String password, Integer status) {
		Admin admin = null;
		
		try {
			Admin params = new Admin();
			params.setName(userName);
			params.setPassword(password);
			params.setStatus(status);
			
			admin = adminMapper.getAdminByContidions(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admin;
	}

	@Override
	public List<AdminRoleMap> queryRoleByAdminId(Long adminId) {
		List<AdminRoleMap> list = null;
		
		try {
			list = adminRoleMapMapper.queryAdminRoleMapByAdminId(adminId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<RoleActionMap> queryRoleActionMapByRoleId(Long roleId,
			Integer mngType, Integer actionType) {
		List<RoleActionMap> list = null;
		
		try {
			RoleActionMap roleActionMap = new RoleActionMap();
			
			Role role = new Role();
			role.setId(roleId);
			roleActionMap.setRole(role);
			
			Component component = new Component();
			component.setKindType(mngType);
			component.setStatus(Component.COLUMN_VALUE_STATUS_ACTIVE);
			roleActionMap.setComponent(component);
			
			UriAction uriAction = new UriAction();
			uriAction.setStatus(UriAction.COLUMN_VALUE_STATUS_ACTIVE);
			if (actionType != null) {
				uriAction.setActionType(actionType);
			}
			roleActionMap.setUriAction(uriAction);
			
			list = roleActionMapMapper.queryMapByConditions(roleActionMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public int updateUserPwd(Long id, String newPwd, String updater) {
		int result = CommonService.RESULT_UPDATE_DEL_FAILED;
		
		try {
			Admin admin = new Admin();
			admin.setId(id);
			admin.setNewPwd(newPwd);
			admin.setUpdater(updater);
			result = adminMapper.updatePwdById(admin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public List<Admin> queryAdminByConditions(Page<Admin> page, Admin adminInfo){
		List<Admin> list = null;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>(); 
			if (adminInfo.getName() != null) {
				params.put("userName", adminInfo.getName());
			}
			if (adminInfo.getType() != null) {
				params.put("userType", adminInfo.getType());
			}
			if (adminInfo.getBeginTime() != null) {
				params.put("beginTime", adminInfo.getBeginTime());
			}
			if (adminInfo.getEndTime() != null) {
				params.put("endTime", adminInfo.getEndTime());
			}
			page.setParams(params);
			list = adminMapper.queryAdminByConditions(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	@Transactional
	public Long createAdmin(Admin adminInfo,AdminRoleMap adminRoleMap) throws Exception{
		Long id = new Long(RESULT_INSERT_FAILED);
		try {
			adminMapper.createAdmin(adminInfo);
			adminRoleMapMapper.createAdminRoleMap(adminRoleMap);
			id = adminInfo.getId();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return id;
	}
	
	@Override
	@Transactional
	public int updateAdmin(Admin adminInfo,AdminRoleMap adminRoleMap) throws Exception{
		int result = RESULT_UPDATE_DEL_FAILED;
		
		try {
			adminMapper.updateAdmin(adminInfo);
			List<AdminRoleMap> list = adminRoleMapMapper.queryAdminRoleMapByAdminId(adminInfo.getId());
			if(list.size()>0){
				result = adminRoleMapMapper.updateAdminRoleMapByAdminId(adminRoleMap);
			}else {
				result = adminRoleMapMapper.createAdminRoleMap(adminRoleMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	@Override
	@Transactional
	public int deleteAdminById(Long id) throws Exception{
		int result = RESULT_UPDATE_DEL_FAILED;
		try {
			adminRoleMapMapper.deleteAdminRoleMapByAdminId(id);
			result = adminMapper.deleteAdminById(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	@Override
	public List<Role> queryRoleByConditions(Page<Role> page, Role roleInfo){
		List<Role> list = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>(); 
			if (roleInfo.getName() != null) {
				params.put("name", roleInfo.getName());
			}
			if (roleInfo.getBeginTime() != null) {
				params.put("beginTime", roleInfo.getBeginTime());
			}
			if (roleInfo.getEndTime() != null) {
				params.put("endTime", roleInfo.getEndTime());
			}
			page.setParams(params);
			list = roleMapper.queryRoleByConditions(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	@Transactional
	public int updateRole(Role roleInfo,List<RoleActionMap> roleActionList) throws Exception{
		int result = RESULT_UPDATE_DEL_FAILED;
		
		try {
			result = roleMapper.updateRole(roleInfo);
			roleActionMapMapper.deleteRoleActionMap(roleInfo.getId());
			for(RoleActionMap roleActionMap : roleActionList){
				roleActionMap.setRole(roleInfo);
				roleActionMapMapper.createRoleActionMap(roleActionMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	@Override
	@Transactional
	public int deleteRoleById(Long id) throws Exception{
		int result = RESULT_UPDATE_DEL_FAILED;
		try {
			roleActionMapMapper.deleteRoleActionMap(id);
			result = roleMapper.deleteRoleById(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	@Override
	@Transactional
	public Long createRole(Role roleInfo,List<RoleActionMap> roleActionList) throws Exception{
		Long id = new Long(RESULT_INSERT_FAILED);
		try {
			roleMapper.createRole(roleInfo);
			id = roleInfo.getId();
			
			for(RoleActionMap roleActionMap : roleActionList){
				Role role = new Role();
				role.setId(id);
				roleActionMap.setRole(role);
				roleActionMapMapper.createRoleActionMap(roleActionMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return id;
	}
	
	@Override
	public Role getRoleById(Long id){
		Role role = null;
		try{
			role = roleMapper.getRoleById(id);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return role;
	}
	
	@Override
	public List<AdminRoleMap> queryAdminRoleMapByRoleId(Long roleId){
		List<AdminRoleMap> adminRoleMapList = null;
		try{
			adminRoleMapList = adminRoleMapMapper.queryAdminRoleMapByRoleId(roleId);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return adminRoleMapList;
	}
	
	@Override
	public List<Role> queryAllRoleInfo(){
		List<Role> roleInfo = null;
		try{
			roleInfo = roleMapper.queryAllRoleInfo();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return roleInfo;
	}
	
	@Override
	public List<Component> queryAllComponent(){
		List<Component> componentInfo = null;
		try{
			componentInfo = componentMapper.queryAllComponent();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return componentInfo;
	}
	
	@Override
	public List<UriAction> queryAllUriAction(){
		List<UriAction> uriActionList = null;
		try{
			uriActionList = uriActionMapper.queryAllUriAction();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return uriActionList;
	}
	
	@Override
	public List<Admin> queryAdminByCpId(Long cpId){
		List<Admin> adminList = null;
		try{
			adminList = adminMapper.queryAdminByCpId(cpId);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return adminList;
	}
}
