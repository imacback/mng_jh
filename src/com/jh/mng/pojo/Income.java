package com.jh.mng.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 收入信息
 * @author admin
 *
 */
public class Income implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 收入日期
	 */
	private String recvtime;
	
	/**
	 * 游戏ID
	 */
	private Long gameId;
	
	/**
	 * 道具ID
	 */
	private Long consumeId;
	
	/**
	 * 渠道ID
	 */
	private Long chnlId;
	
	/**
	 * 省份名称
	 */
	private String provinceName;

	/**
	 * 成功次数
	 */
	private Long successTimes;
	
	/**
	 * 收入
	 */
	private Double income;
	
	private Integer dec_flag;
	
	private String startDate;
	
	private String enDate;
	
	private String month;
	
	private String[] groupConditions;
	
	private List<Object> valueList;
	
	private List<GameInfo> gameInfoList;
	
	private List<ChnlInfo> chnList;
	
	private List<ConsumeInfo> consumeList;
	
	private String consumeName;
	
	private String gameName;
	
	private String chnlName;
	
	private List<String> orderMonthList;
	
	/**
	 * 选中作为分组信息
	 */
	public static int COLUMN_VALUE_GROUPISCHECKED_YES = 1;
	
	/**
	 * 未选中作为分组信息
	 */
	public static int COLUMN_VALUE_GROUPISCHECKED_NO = 0;
	
	/**
	 * 扣量
	 */
	public static int COLUMN_VALUE_DEC_FLAG_YES = 1;
	
	/**
	 * 非扣量
	 */
	public static int COLUMN_VALUE_DEC_FLAG_NO = 0;
	
	/**
	 * 请求次数
	 */
	private Integer reqTimes;
	
	/**
	 * 状态
	 */
	private String status;
	
	private int succTimes;
	
	private int failtimes;
	
	private String rate;
	
	private Date lastFeeTime;
	
	/**
	 * 收入小时
	 */
	private String recvHour;
	
	public static enum GroupCondition {
		GROUP_INCOMEDATE("recvtime", "recvtime", "日期"), GROUP_PROVINCE("provinceName", "provinceName", "省份"), GROUP_CONSUME("consumeId", "consumeName", "道具"),
		GROUP_GAME("gameId", "gameName", "业务"), GROUP_CHANNEL("chnlId", "chnlName", "渠道"); 
		private String code;
		private String nameCode;
		private String desc;
		private Integer isChecked;
		
		private GroupCondition(String code, String nameCode, String desc) {
			this.code = code;
			this.nameCode = nameCode;
			this.desc = desc;
		}

		/**
		 * @return the desc
		 */
		public String getDesc() {
			return desc;
		}

		/**
		 * @param desc the desc to set
		 */
		public void setDesc(String desc) {
			this.desc = desc;
		}

		/**
		 * @return the code
		 */
		public String getCode() {
			return code;
		}

		/**
		 * @param code the code to set
		 */
		public void setCode(String code) {
			this.code = code;
		}

		public Integer getIsChecked() {
			return isChecked;
		}

		public void setIsChecked(Integer isChecked) {
			this.isChecked = isChecked;
		}

		public String getNameCode() {
			return nameCode;
		}

		public void setNameCode(String nameCode) {
			this.nameCode = nameCode;
		}
	}
	
	public static enum GroupCondition_Rate {
		GROUP_INCOMEDATE("recvtime", "recvtime", "日期"), GROUP_PROVINCE("provinceName", "provinceName", "省份"), GROUP_CONSUME("consumeId", "consumeName", "道具"),
		GROUP_GAME("gameId", "gameName", "业务"), GROUP_CHANNEL("chnlId", "chnlName", "渠道"), GROUP_INCOMEHOUR("recvHour", "recvHour", "小时"); 
		private String code;
		private String nameCode;
		private String desc;
		private Integer isChecked;
		
		private GroupCondition_Rate(String code, String nameCode, String desc) {
			this.code = code;
			this.nameCode = nameCode;
			this.desc = desc;
		}

		/**
		 * @return the desc
		 */
		public String getDesc() {
			return desc;
		}

		/**
		 * @param desc the desc to set
		 */
		public void setDesc(String desc) {
			this.desc = desc;
		}

		/**
		 * @return the code
		 */
		public String getCode() {
			return code;
		}

		/**
		 * @param code the code to set
		 */
		public void setCode(String code) {
			this.code = code;
		}

		public Integer getIsChecked() {
			return isChecked;
		}

		public void setIsChecked(Integer isChecked) {
			this.isChecked = isChecked;
		}

		public String getNameCode() {
			return nameCode;
		}

		public void setNameCode(String nameCode) {
			this.nameCode = nameCode;
		}
	}

	public Long getGameId() {
		if (gameInfoList != null) {
			for (int i = 0; i < gameInfoList.size(); i++) {
				if (gameId != null) {
					if (gameInfoList.get(i).getId().intValue() == this.gameId.intValue()) {
						this.gameName = gameInfoList.get(i).getGameName();
						break;
					}
				}
			}
		}
		
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Long getConsumeId() {
		if (consumeList != null) {
			for (int i = 0; i < consumeList.size(); i++) {
				if (this.consumeId != null) {
					if (consumeList.get(i).getId().intValue() == this.consumeId.intValue()) {
						this.consumeName = consumeList.get(i).getConsumeName();
						break;
					}
				}
			}
		}
		
		return consumeId;
	}

	public void setConsumeId(Long consumeId) {
		this.consumeId = consumeId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Long getSuccessTimes() {
		return successTimes;
	}

	public void setSuccessTimes(Long successTimes) {
		this.successTimes = successTimes;
	}

	public Long getChnlId() {
		if (chnList != null) {
			for (int i = 0; i < chnList.size(); i++) {
				if (chnlId != null) {
					if (chnList.get(i).getId().intValue() == this.chnlId.intValue()) {
						this.chnlName = chnList.get(i).getChnlName();
						break;
					}
				}
			}
		}
		return chnlId;
	}

	public void setChnlId(Long chnlId) {
		this.chnlId = chnlId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEnDate() {
		return enDate;
	}

	public void setEnDate(String enDate) {
		this.enDate = enDate;
	}

	public String[] getGroupConditions() {
		return groupConditions;
	}

	public void setGroupConditions(String[] groupConditions) {
		this.groupConditions = groupConditions;
	}

	public String getRecvtime() {
		return recvtime;
	}

	public void setRecvtime(String recvtime) {
		this.recvtime = recvtime;
	}

	public List<Object> getValueList() {
		return valueList;
	}

	public void setValueList(List<Object> valueList) {
		this.valueList = valueList;
	}

	public List<GameInfo> getGameInfoList() {
		return gameInfoList;
	}

	public void setGameInfoList(List<GameInfo> gameInfoList) {
		this.gameInfoList = gameInfoList;
	}

	public List<ChnlInfo> getChnList() {
		return chnList;
	}

	public void setChnList(List<ChnlInfo> chnList) {
		this.chnList = chnList;
	}

	public List<ConsumeInfo> getConsumeList() {
		return consumeList;
	}

	public void setConsumeList(List<ConsumeInfo> consumeList) {
		this.consumeList = consumeList;
	}

	public String getConsumeName() {
		return consumeName;
	}

	public void setConsumeName(String consumeName) {
		this.consumeName = consumeName;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getChnlName() {
		return chnlName;
	}

	public void setChnlName(String chnlName) {
		this.chnlName = chnlName;
	}

	public Integer getDec_flag() {
		return dec_flag;
	}

	public void setDec_flag(Integer dec_flag) {
		this.dec_flag = dec_flag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getReqTimes() {
		return reqTimes;
	}

	public void setReqTimes(Integer reqTimes) {
		this.reqTimes = reqTimes;
	}

	public int getFailtimes() {
		return failtimes;
	}

	public void setFailtimes(int failtimes) {
		this.failtimes = failtimes;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getSuccTimes() {
		return succTimes;
	}

	public void setSuccTimes(int succTimes) {
		this.succTimes = succTimes;
	}

	public Date getLastFeeTime() {
		return lastFeeTime;
	}

	public void setLastFeeTime(Date lastFeeTime) {
		this.lastFeeTime = lastFeeTime;
	}

	public String getRecvHour() {
		return recvHour;
	}

	public void setRecvHour(String recvHour) {
		this.recvHour = recvHour;
	}

	/**
	 * @return the income
	 */
	public Double getIncome() {
		return income;
	}

	/**
	 * @param income the income to set
	 */
	public void setIncome(Double income) {
		this.income = income;
	}

	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
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
