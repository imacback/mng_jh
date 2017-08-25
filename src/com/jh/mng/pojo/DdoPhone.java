package com.jh.mng.pojo;

import java.io.Serializable;

/**
 * DDO业务已处理过的手机号
 * @author admin
 *
 */
public class DdoPhone implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8821259637134733645L;
	
	private String phone;

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

}
