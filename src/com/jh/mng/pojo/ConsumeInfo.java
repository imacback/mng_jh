package com.jh.mng.pojo;

import java.io.Serializable;

public class ConsumeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String consumeName;
	
	private String consumeCode;
	
	private int fee;

	private String feeurl;
	
	private Long gameInfoId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConsumeName() {
		return consumeName;
	}

	public void setConsumeName(String consumeName) {
		this.consumeName = consumeName;
	}

	/**
	 * @return the fee
	 */
	public int getFee() {
		return fee;
	}

	/**
	 * @param fee the fee to set
	 */
	public void setFee(int fee) {
		this.fee = fee;
	}

	/**
	 * @return the feeurl
	 */
	public String getFeeurl() {
		return feeurl;
	}

	/**
	 * @param feeurl the feeurl to set
	 */
	public void setFeeurl(String feeurl) {
		this.feeurl = feeurl;
	}

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
	 * @return the consumeCode
	 */
	public String getConsumeCode() {
		return consumeCode;
	}

	/**
	 * @param consumeCode the consumeCode to set
	 */
	public void setConsumeCode(String consumeCode) {
		this.consumeCode = consumeCode;
	}

}
