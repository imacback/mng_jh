package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色操作对应关系
 * @author admin
 *
 */
public class RoleActionMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6316069415260523950L;

	private Long id;
	
	/**
	 * 角色信息
	 */
	private Role role;
	
	/**
	 * 组件ID
	 */
	private Long componentId;
	
	/**
	 * 操作ID
	 */
	private Long uriId;
	
	/**
	 * 详细操作信息
	 */
	private UriAction uriAction;
	
	/**
	 * 状态
	 */
	private Integer status;
	
	/**
	 * 组件信息
	 */
	private Component component;
	
	/**
	 * 创建者
	 */
	private String creater;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新者
	 */
	private String updater;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UriAction getUriAction() {
		return uriAction;
	}

	public void setUriAction(UriAction uriAction) {
		this.uriAction = uriAction;
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

	public Long getUriId() {
		return uriId;
	}

	public void setUriId(Long uriId) {
		this.uriId = uriId;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public Long getComponentId() {
		return componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}
}
