package com.jh.mng.mapper;

import java.util.List;

import com.jh.mng.pojo.RoleActionMap;


public interface RoleActionMapMapper {

	public List<RoleActionMap> queryMapByConditions(RoleActionMap roleActionMap);
	
	public int createRoleActionMap(RoleActionMap roleActionList);
	
	public int deleteRoleActionMap(Long roleId);
}
