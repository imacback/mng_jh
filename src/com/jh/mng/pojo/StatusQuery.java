package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class StatusQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5675290786482054279L;
	
	private String hret;
	
	private String cpparam;
	
	private String productName;
	
	private Long gameId;
	
	private Long chnlId;
	
	private String chnlName;
	
	private String status;
	
	private Date recvTime;
	
	private String startDate;
	
	private String enDate;
	
	private String phoneno;
	
	private String providerId;
	
	private int times;
	
	private int isCount;
	
	private Integer statusInteger;
	
	private String consumeName;
	
	private Integer syncchnl;
	
	private List<String> orderMonthList;
	
	private String ext;
	
	private String state;
	
	private boolean extBox;

	/**
	 * @return the cpparam
	 */
	public String getCpparam() {
		return cpparam;
	}

	/**
	 * @param cpparam the cpparam to set
	 */
	public void setCpparam(String cpparam) {
		this.cpparam = cpparam;
	}

	/**
	 * @return the chnlName
	 */
	public String getChnlName() {
		return chnlName;
	}

	/**
	 * @param chnlName the chnlName to set
	 */
	public void setChnlName(String chnlName) {
		this.chnlName = chnlName;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the recvTime
	 */
	public Date getRecvTime() {
		return recvTime;
	}

	/**
	 * @param recvTime the recvTime to set
	 */
	public void setRecvTime(Date recvTime) {
		this.recvTime = recvTime;
	}

	/**
	 * @return the gameId
	 */
	public Long getGameId() {
		return gameId;
	}

	/**
	 * @param gameId the gameId to set
	 */
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	/**
	 * @return the chnlId
	 */
	public Long getChnlId() {
		return chnlId;
	}

	/**
	 * @param chnlId the chnlId to set
	 */
	public void setChnlId(Long chnlId) {
		this.chnlId = chnlId;
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the phoneno
	 */
	public String getPhoneno() {
		return phoneno;
	}

	/**
	 * @param phoneno the phoneno to set
	 */
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the enDate
	 */
	public String getEnDate() {
		return enDate;
	}

	/**
	 * @param enDate the enDate to set
	 */
	public void setEnDate(String enDate) {
		this.enDate = enDate;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	/**
	 * @return the times
	 */
	public int getTimes() {
		return times;
	}

	/**
	 * @param times the times to set
	 */
	public void setTimes(int times) {
		this.times = times;
	}

	/**
	 * @return the isCount
	 */
	public int getIsCount() {
		return isCount;
	}

	/**
	 * @param isCount the isCount to set
	 */
	public void setIsCount(int isCount) {
		this.isCount = isCount;
	}

	/**
	 * @return the hret
	 */
	public String getHret() {
		return hret;
	}

	/**
	 * @param hret the hret to set
	 */
	public void setHret(String hret) {
		this.hret = hret;
	}

	/**
	 * @return the statusInteger
	 */
	public Integer getStatusInteger() {
		return statusInteger;
	}

	/**
	 * @param statusInteger the statusInteger to set
	 */
	public void setStatusInteger(Integer statusInteger) {
		this.statusInteger = statusInteger;
	}

	/**
	 * @return the consumeName
	 */
	public String getConsumeName() {
		return consumeName;
	}

	/**
	 * @param consumeName the consumeName to set
	 */
	public void setConsumeName(String consumeName) {
		this.consumeName = consumeName;
	}

	/**
	 * @return the syncchnl
	 */
	public Integer getSyncchnl() {
		return syncchnl;
	}

	/**
	 * @param syncchnl the syncchnl to set
	 */
	public void setSyncchnl(Integer syncchnl) {
		this.syncchnl = syncchnl;
	}

	/**
	 * @return the orderMonthList
	 */
	public List<String> getOrderMonthList() {
		return orderMonthList;
	}

	/**
	 * @param orderMonthList the orderMonthList to set
	 */
	public void setOrderMonthList(List<String> orderMonthList) {
		this.orderMonthList = orderMonthList;
	}

	/**
	 * @return the ext
	 */
	public String getExt() {
		return ext;
	}

	/**
	 * @param ext the ext to set
	 */
	public void setExt(String ext) {
		this.ext = ext;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the extBox
	 */
	public boolean isExtBox() {
		return extBox;
	}

	/**
	 * @param extBox the extBox to set
	 */
	public void setExtBox(boolean extBox) {
		this.extBox = extBox;
	}

}
