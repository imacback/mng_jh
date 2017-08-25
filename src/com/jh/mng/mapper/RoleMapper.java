package com.jh.mng.mapper;

import java.util.List;

import com.jh.mng.pojo.Role;
import com.jh.mng.util.page.Page;


public interface RoleMapper {

	public Role getRoleById(Long id);

	public List<Role> queryRoleByConditions(Page<Role> page);

	public int createRole(Role roleInfo);

	public int updateRole(Role roleInfo);

	public int deleteRoleById(Long id);
	
	public List<Role> queryAllRoleInfo();
}
