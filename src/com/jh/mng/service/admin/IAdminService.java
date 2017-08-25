package com.jh.mng.service.admin;

import java.util.List;

import com.jh.mng.pojo.Admin;
import com.jh.mng.pojo.AdminRoleMap;
import com.jh.mng.pojo.Component;
import com.jh.mng.pojo.Role;
import com.jh.mng.pojo.RoleActionMap;
import com.jh.mng.pojo.UriAction;
import com.jh.mng.util.page.Page;


/**
 * 用户服务
 * @author T410
 *
 */
public interface IAdminService {

	/**
	 * 根据用户ID查询用户
	 * @param id 用户ID
	 * @return 用户
	 */
	public Admin getAdminById(Long id);
	
	public Admin getAdminMoreById(Long id);
	
	/**
	 * 根据用户名，密码查询用户信息
	 * @param userName 用户名
	 * @param password 密码
	 * @param status  用户状态
	 * @param type 用户类型
	 * @return 用户信息
	 */
	public Admin getAdminByUserNameAndPwd(String userName, String password, Integer status, Integer type);
	
	/**
	 * 根据用户名，密码查询用户信息
	 * @param userName 用户名
	 * @param password 密码
	 * @param status  用户状态
	 * @return 用户信息
	 */
	public Admin getAdminByUserNameAndPwd(String userName, String password, Integer status);
	
	/**
	 * 根据用户ID，查询用户角色列表
	 * @param adminId 用户ID
	 * @return 用户角色列表
	 */
	public List<AdminRoleMap> queryRoleByAdminId(Long adminId);
	
	/**
	 * 根据角色ID查询，平台类型所有权限操作列表
	 * @param roleId 角色ID
	 * @param mngType 平台类型
	 * @param actionType 菜单类型
	 * @return
	 */
	public List<RoleActionMap> queryRoleActionMapByRoleId(Long roleId, Integer mngType, Integer actionType);
	
	/**
	 * 根据用户ID更新用户密码
	 * @param id 用户ID
	 * @param newPwd 用户密码
	 * @param updater 更新人
	 * @return 更新结果 1为成功
	 */
	public int updateUserPwd(Long id, String newPwd, String updater);
	
	/**
	 * 根据条件查询用户列表
	 * @param page 分页信息
	 * @param adminInfo 查询条件
	 * @return 用户列表
	 */
	public List<Admin> queryAdminByConditions(Page<Admin> page, Admin adminInfo);
	
	/**
	 * 新建AdminInfo
	 * @param adminInfo 需要新增的用户信息
	 * @return 新建成功后的应用ID
	 */
	public Long createAdmin (Admin adminInfo,AdminRoleMap adminRoleMap) throws Exception;
	
	/**
	 * 修改Admin信息
	 * @param adminInfo 需要修改的adminInfo
	 * @return 影响结果记录数 1为成功
	 */
	public int updateAdmin(Admin adminInfo,AdminRoleMap adminRoleMap) throws Exception;
	
	/**
	 * 根据ID删除用户信息
	 * @param id 用户ID
	 * @return 成功删除数，1为成功
	 */
	public int deleteAdminById(Long id) throws Exception;
	
	/**
	 * 根据条件查询用户列表
	 * @param page 分页信息
	 * @param roleInfo 查询条件
	 * @return 用户列表
	 */
	public List<Role> queryRoleByConditions(Page<Role> page, Role roleInfo);
	
	/**
	 * 新建RoleInfo
	 * @param RoleInfo 需要新增的用户信息
	 * @return 新建成功后的应用ID
	 */
	public Long createRole(Role roleInfo,List<RoleActionMap> roleActionList) throws Exception;
	
	/**
	 * 修改Role信息
	 * @param roleInfo 需要修改的roleInfo
	 * @return 影响结果记录数 1为成功
	 */
	public int updateRole(Role roleInfo,List<RoleActionMap> roleActionList) throws Exception;
	

	/**
	 * 根据ID删除角色信息
	 * @param id 角色ID
	 * @return 成功删除数，1为成功
	 */
	public int deleteRoleById(Long id) throws Exception;
	
	/**
	 * 根据用户ID查询角色
	 * @param id 角色ID
	 * @return 角色
	 */
	public Role getRoleById(Long id);
	
	/**
	 * 根据角色ID查询用户信息
	 * @param roleId
	 * @return
	 */
	public List<AdminRoleMap> queryAdminRoleMapByRoleId(Long roleId);
	
	/**
	 * 获取所有角色信息
	 * @return
	 */
	public List<Role> queryAllRoleInfo();
	
	/**
	 * 获取所有的管理列表
	 * @return
	 */
	public List<Component> queryAllComponent();
	
	/**
	 * 获取所有操作方法
	 * @return
	 */
	public List<UriAction> queryAllUriAction();
	
	/**
	 * 根据CPID查询用户信息
	 * @param cpId
	 * @return
	 */
	public List<Admin> queryAdminByCpId(Long cpId);
}
