package com.jh.mng.mapper;


import java.util.List;

import com.jh.mng.pojo.Admin;
import com.jh.mng.util.page.Page;


public interface AdminMapper {

	public Admin getAdminById(Long id); 
	
	public Admin getAdminMoreById(Long id);
	
	public Admin getAdminByContidions(Admin admin);
	
	public int updatePwdById(Admin admin);
	
	public List<Admin> queryAdminByConditions(Page<Admin> page);
	
	public List<Admin> queryAdminOtherWriteByConditions(Page<Admin> page);
	
	public int createAdmin(Admin adminInfo);
	
	public int updateAdmin(Admin adminInfo);
	
	public int deleteAdminById(Long id);
	
	public List<Admin> queryAdminByCpId(Long channelId);
	
}
