package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能组件
 * @author admin
 *
 */
public class Component implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8265894765131772570L;
	
	/**
	 * 运营组件
	 */
	public static final Integer COLUMN_VALUE_KINDTYPE_OPMNG = 1;
	/**
	 * 渠道组件
	 */
	public static final Integer COLUMN_VALUE_KINDTYPE_PROVIDE = 2;
	
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
	 * 组件名
	 */
	private String name;
	
	/**
	 * 组件描述
	 */
	private String description;
	
	/**
	 * 组件状态
	 */
	private Integer status;
	
	/**
	 * 组件类型，1：运营组件 2：渠道组件
	 */
	private Integer kindType;
	
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getKindType() {
		return kindType;
	}

	public void setKindType(Integer kindType) {
		this.kindType = kindType;
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

}
