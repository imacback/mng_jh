package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色用户对应关系
 * @author admin
 *
 */
public class AdminRoleMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -763581174820964161L;
	
	private Long id;
	/**
	 * 用户信息
	 */
	private Admin admin;
	
	/**
	 * 角色信息
	 */
	private Role role;
	
	/**
	 * 状态
	 */
	private Integer status;
	
	/**
	 * 创建者
	 */
	private String creater;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新人
	 */
	private String updater;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
