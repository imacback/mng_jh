package com.jh.mng.mapper;

import java.util.List;

import com.jh.mng.pojo.AdminRoleMap;


public interface AdminRoleMapMapper {

	public List<AdminRoleMap> queryAdminRoleMapByAdminId(Long adminId);
	
	public List<AdminRoleMap> queryAdminRoleMapByRoleId(Long roleId);
	
	public int createAdminRoleMap(AdminRoleMap adminRoleMap);
	
	public int updateAdminRoleMapByAdminId(AdminRoleMap adminRoleMap);
	
	public int deleteAdminRoleMapByAdminId(Long adminId);
}
