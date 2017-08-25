package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作URI
 * @author admin
 *
 */
public class Uri implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4049681837307611423L;
	
	private Long id;
	
	/**
	 * 操作URI
	 */
	private String uri;
	
	/**
	 * 操作描述
	 */
	private String desc;
	
	/**
	 * 状态
	 */
	private Integer status;
	
	/**
	 * 组件信息
	 */
	private Component component;
	
	/**
	 * 操作所属类型：1：运营 2：渠道
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

}
