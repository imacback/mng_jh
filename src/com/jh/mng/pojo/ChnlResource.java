package com.jh.mng.pojo;

import java.io.Serializable;

public class ChnlResource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Long id;
	
	private Long chnlId;
	
	private Long consumeId;
	
	private Long startRq;
	
	private Long endRq;
	
	private int status;

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
	 * @return the consumeId
	 */
	public Long getConsumeId() {
		return consumeId;
	}

	/**
	 * @param consumeId the consumeId to set
	 */
	public void setConsumeId(Long consumeId) {
		this.consumeId = consumeId;
	}

	/**
	 * @return the startRq
	 */
	public Long getStartRq() {
		return startRq;
	}

	/**
	 * @param startRq the startRq to set
	 */
	public void setStartRq(Long startRq) {
		this.startRq = startRq;
	}

	/**
	 * @return the endRq
	 */
	public Long getEndRq() {
		return endRq;
	}

	/**
	 * @param endRq the endRq to set
	 */
	public void setEndRq(Long endRq) {
		this.endRq = endRq;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}
