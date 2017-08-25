package com.jh.mng.pojo;

import java.io.Serializable;

public class GameInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String gameName;
	
	private String contentCode;
	
	private String feeCode;
	
	private String busiCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	/**
	 * @return the contentCode
	 */
	public String getContentCode() {
		return contentCode;
	}

	/**
	 * @param contentCode the contentCode to set
	 */
	public void setContentCode(String contentCode) {
		this.contentCode = contentCode;
	}

	/**
	 * @return the feeCode
	 */
	public String getFeeCode() {
		return feeCode;
	}

	/**
	 * @param feeCode the feeCode to set
	 */
	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	/**
	 * @return the busiCode
	 */
	public String getBusiCode() {
		return busiCode;
	}

	/**
	 * @param busiCode the busiCode to set
	 */
	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}

}
