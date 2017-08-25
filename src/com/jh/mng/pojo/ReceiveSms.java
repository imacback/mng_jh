package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;

public class ReceiveSms implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Date recvtime;
	
	private String userid;
	
	private String cpserviceid;
	
	private String consumecode;
	
	private String cpparam;
	
	private String hret;
	
	private String status;
	
	private String versionid;
	
	private String transido;
	
	private int syncflag;
	
	private Date synctime;
	
	private int syncchnl;
	
	private Date syncchnltime;
	
	private int chnl_id;
	
	private int fee;
	
	private int dec_flag;
	
	private String state;
	
	private String packageid;
	
	private String phoneno;
	
	private Long sum_fee_y;
	
	private Long sum_fee_r;
	
	private String ext;

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
	 * @return the recvtime
	 */
	public Date getRecvtime() {
		return recvtime;
	}

	/**
	 * @param recvtime the recvtime to set
	 */
	public void setRecvtime(Date recvtime) {
		this.recvtime = recvtime;
	}

	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return the cpserviceid
	 */
	public String getCpserviceid() {
		return cpserviceid;
	}

	/**
	 * @param cpserviceid the cpserviceid to set
	 */
	public void setCpserviceid(String cpserviceid) {
		this.cpserviceid = cpserviceid;
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
	 * @return the versionid
	 */
	public String getVersionid() {
		return versionid;
	}

	/**
	 * @param versionid the versionid to set
	 */
	public void setVersionid(String versionid) {
		this.versionid = versionid;
	}

	/**
	 * @return the transido
	 */
	public String getTransido() {
		return transido;
	}

	/**
	 * @param transido the transido to set
	 */
	public void setTransido(String transido) {
		this.transido = transido;
	}

	/**
	 * @return the syncflag
	 */
	public int getSyncflag() {
		return syncflag;
	}

	/**
	 * @param syncflag the syncflag to set
	 */
	public void setSyncflag(int syncflag) {
		this.syncflag = syncflag;
	}

	/**
	 * @return the synctime
	 */
	public Date getSynctime() {
		return synctime;
	}

	/**
	 * @param synctime the synctime to set
	 */
	public void setSynctime(Date synctime) {
		this.synctime = synctime;
	}

	/**
	 * @return the syncchnl
	 */
	public int getSyncchnl() {
		return syncchnl;
	}

	/**
	 * @param syncchnl the syncchnl to set
	 */
	public void setSyncchnl(int syncchnl) {
		this.syncchnl = syncchnl;
	}

	/**
	 * @return the syncchnltime
	 */
	public Date getSyncchnltime() {
		return syncchnltime;
	}

	/**
	 * @param syncchnltime the syncchnltime to set
	 */
	public void setSyncchnltime(Date syncchnltime) {
		this.syncchnltime = syncchnltime;
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
	 * @return the dec_flag
	 */
	public int getDec_flag() {
		return dec_flag;
	}

	/**
	 * @param dec_flag the dec_flag to set
	 */
	public void setDec_flag(int dec_flag) {
		this.dec_flag = dec_flag;
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
	 * @return the sum_fee_y
	 */
	public Long getSum_fee_y() {
		return sum_fee_y;
	}

	/**
	 * @param sum_fee_y the sum_fee_y to set
	 */
	public void setSum_fee_y(Long sum_fee_y) {
		this.sum_fee_y = sum_fee_y;
	}

	/**
	 * @return the sum_fee_r
	 */
	public Long getSum_fee_r() {
		return sum_fee_r;
	}

	/**
	 * @param sum_fee_r the sum_fee_r to set
	 */
	public void setSum_fee_r(Long sum_fee_r) {
		this.sum_fee_r = sum_fee_r;
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

}
