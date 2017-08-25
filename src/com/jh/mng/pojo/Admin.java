package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * 管理员用户POJO
 * @author T410
 *
 */
public class Admin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7588636505097033804L;
	
	/**
	 * 运营管理员
	 */
	public static final int COLUMN_VALUE_TYPE_MNG = 1;
	
	/**
	 * CP管理员
	 */
	public static final int COLUMN_VALUE_TYPE_CP = 2;
	
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
	 * 管理员名称
	 */
	private String name;
	
	/**
	 * 管理员密码
	 */
	private String password;
	
	/**
	 * 管理员类型，1：运营管理员 2：CP管理员
	 */
	private Integer type;
	
	/**
	 * 有效状态, 1:有效; 0:无效
	 */
	private Integer status;
	
	/**
	 * 真实姓名
	 */
	private String realName;
	
	/**
	 * 身份证号
	 */
	private String idCard;
	
	/**
	 * 电话
	 */
	private String tel;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 地址
	 */
	private String address;
	
//	/**
//	 * 所属sp
//	 */
//	private Long spId;
	
	/**
	 * 超级管理员标志 1超级管理员 0普通管理员
	 */
	private Integer isRoot;
	
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
	
	private Date beginTime;
	
	private Date endTime;

	private String newPwd;
	
	private Long cpId;
	
	/**
	 * 角色信息
	 */
	private Role roleInfo;
	

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("name", name)
				.append("password", password).append("type", type).append(
						"status", status).append("realName", realName).append(
						"idCard", idCard).append("tel", tel).append("email",
						email).append("address", address).append("isRoot",
						isRoot).append("creater", creater).append("createTime",
						createTime).append("updater", updater).append(
						"updateTime", updateTime).append("newPwd", newPwd)
				.append("roleInfo", roleInfo)
				.toString();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * @param realName the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/**
	 * @return the idCard
	 */
	public String getIdCard() {
		return idCard;
	}

	/**
	 * @param idCard the idCard to set
	 */
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	/**
	 * @return the tel
	 */
	public String getTel() {
		return tel;
	}

	/**
	 * @param tel the tel to set
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

//	/**
//	 * @return the spId
//	 */
//	public Long getSpId() {
//		return spId;
//	}
//
//	/**
//	 * @param spId the spId to set
//	 */
//	public void setSpId(Long spId) {
//		this.spId = spId;
//	}

	/**
	 * @return the isRoot
	 */
	public Integer getIsRoot() {
		return isRoot;
	}

	/**
	 * @param isRoot the isRoot to set
	 */
	public void setIsRoot(Integer isRoot) {
		this.isRoot = isRoot;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	
	public Date getBeginTime() {
		return this.beginTime;
	}
	
	public void setBeginTime(Date beginTime){
		this.beginTime = beginTime;
	}
	
	public Date getEndTime(){
		return this.endTime;
	}
	
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}

	public Role getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(Role roleInfo) {
		this.roleInfo = roleInfo;
	}

	/**
	 * @return the cpId
	 */
	public Long getCpId() {
		return cpId;
	}

	/**
	 * @param cpId the cpId to set
	 */
	public void setCpId(Long cpId) {
		this.cpId = cpId;
	}

}
