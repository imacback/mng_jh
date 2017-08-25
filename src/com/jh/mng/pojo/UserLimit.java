package com.jh.mng.pojo;

import java.io.Serializable;

public class UserLimit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户标识
	 */
	private String mdn;
	
	/**
	 * 日期 yyyyMMdd
	 */
	private Long rq;
	
	/**
	 * 日值
	 */
	private Long r_fee;
	
	/**
	 * 月值
	 */
	private Long y_fee;

	/**
	 * @return the mdn
	 */
	public String getMdn() {
		return mdn;
	}

	/**
	 * @param mdn the mdn to set
	 */
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	/**
	 * @return the r_fee
	 */
	public Long getR_fee() {
		return r_fee;
	}

	/**
	 * @param r_fee the r_fee to set
	 */
	public void setR_fee(Long r_fee) {
		this.r_fee = r_fee;
	}

	/**
	 * @return the y_fee
	 */
	public Long getY_fee() {
		return y_fee;
	}

	/**
	 * @param y_fee the y_fee to set
	 */
	public void setY_fee(Long y_fee) {
		this.y_fee = y_fee;
	}

	/**
	 * @return the rq
	 */
	public Long getRq() {
		return rq;
	}

	/**
	 * @param rq the rq to set
	 */
	public void setRq(Long rq) {
		this.rq = rq;
	}

}
