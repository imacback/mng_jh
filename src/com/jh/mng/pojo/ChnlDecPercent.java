package com.jh.mng.pojo;

import java.io.Serializable;

public class ChnlDecPercent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private int baseline;
	
	private Double percent;
	
	private String curday;
	
	private int chnl_id;
	
	private String state;
	
	private String packageid;
	
	private String consumecode;
	
	private int curcnt;

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
	 * @return the baseline
	 */
	public int getBaseline() {
		return baseline;
	}

	/**
	 * @param baseline the baseline to set
	 */
	public void setBaseline(int baseline) {
		this.baseline = baseline;
	}

	/**
	 * @return the curday
	 */
	public String getCurday() {
		return curday;
	}

	/**
	 * @param curday the curday to set
	 */
	public void setCurday(String curday) {
		this.curday = curday;
	}

	/**
	 * @return the chnl_id
	 */
	public int getChnl_id() {
		return chnl_id;
	}

	/**
	 * @param chnl_id the chnl_id to set
	 */
	public void setChnl_id(int chnl_id) {
		this.chnl_id = chnl_id;
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
	 * @return the packageid
	 */
	public String getPackageid() {
		return packageid;
	}

	/**
	 * @param packageid the packageid to set
	 */
	public void setPackageid(String packageid) {
		this.packageid = packageid;
	}

	/**
	 * @return the consumecode
	 */
	public String getConsumecode() {
		return consumecode;
	}

	/**
	 * @param consumecode the consumecode to set
	 */
	public void setConsumecode(String consumecode) {
		this.consumecode = consumecode;
	}

	/**
	 * @return the curcnt
	 */
	public int getCurcnt() {
		return curcnt;
	}

	/**
	 * @param curcnt the curcnt to set
	 */
	public void setCurcnt(int curcnt) {
		this.curcnt = curcnt;
	}

	/**
	 * @return the percent
	 */
	public Double getPercent() {
		return percent;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(Double percent) {
		this.percent = percent;
	} 
}
