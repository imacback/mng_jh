package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作方法POJO
 * @author admin
 *
 */
public class UriAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2766783235614701865L;
	
	/**
	 * 索引操作
	 */
	public static final int COLUMN_VALUE_ACTIONTYPE_INDEX = 1;
	
	/**
	 * 普通操作
	 */
	public static final int COLUMN_VALUE_ACTIONTYPE_NOMAL = 2;
	
	/**
	 * 激活状态
	 */
	public static final Integer COLUMN_VALUE_STATUS_ACTIVE = 1;
	
	/**
	 * 未激活状态
	 */
	public static final Integer COLUMN_VALUE_STATUS_INACTIVE = 0;
	
	private Long id;
	
	/**
	 * 操作所属uri
	 */
	private Uri uri;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 状态
	 */
	private Integer status;
	
	/**
	 * 关键词
	 */
	private String opKey;
	
	/**
	 * 操作方法值
	 */
	private String actionValue;
	
	/**
	 * 操作方法类型，1：索引 2：常归
	 */
	private Integer actionType;
	
	/**
	 * 所属组件
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

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getOpKey() {
		return opKey;
	}

	public void setOpKey(String opKey) {
		this.opKey = opKey;
	}

	public String getActionValue() {
		return actionValue;
	}

	public void setActionValue(String actionValue) {
		this.actionValue = actionValue;
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

	public Integer getActionType() {
		return actionType;
	}

	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

}
