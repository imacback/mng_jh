package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.List;

public class ChnlIncomPercent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long gameInfoId;
	
	private String gameName;
	
	private Long chnlId;
	
	private String chnlName;
	
	private String state;
	
	private Long chnlInCome;
	
	private Long sumInCome;
	
	private String startDate;
	
	private String enDate;
	
	private String percent;
	
	private String orderDate;
	
	private List<String> orderMonthList;

	/**
	 * @return the gameInfoId
	 */
	public Long getGameInfoId() {
		return gameInfoId;
	}

	/**
	 * @param gameInfoId the gameInfoId to set
	 */
	public void setGameInfoId(Long gameInfoId) {
		this.gameInfoId = gameInfoId;
	}

	/**
	 * @return the gameName
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * @param gameName the gameName to set
	 */
	public void setGameName(String gameName) {
		this.gameName = gameName;
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
	 * @return the chnlInCome
	 */
	public Long getChnlInCome() {
		return chnlInCome;
	}

	/**
	 * @param chnlInCome the chnlInCome to set
	 */
	public void setChnlInCome(Long chnlInCome) {
		this.chnlInCome = chnlInCome;
	}

	/**
	 * @return the sumInCome
	 */
	public Long getSumInCome() {
		return sumInCome;
	}

	/**
	 * @param sumInCome the sumInCome to set
	 */
	public void setSumInCome(Long sumInCome) {
		this.sumInCome = sumInCome;
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
	 * @return the orderDate
	 */
	public String getOrderDate() {
		return orderDate;
	}

	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	/**
	 * @return the percent
	 */
	public String getPercent() {
		return percent;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(String percent) {
		this.percent = percent;
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

}
