package com.jh.mng.mapper;

import java.util.List;
import java.util.Map;

import com.jh.mng.pojo.ChnlDecPercent;
import com.jh.mng.pojo.ChnlIncomPercent;
import com.jh.mng.pojo.ChnlInfo;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.DdoPhone;
import com.jh.mng.pojo.GameInfo;
import com.jh.mng.pojo.Income;
import com.jh.mng.pojo.Mask;
import com.jh.mng.pojo.ReceiveSms;
import com.jh.mng.pojo.StatusQuery;
import com.jh.mng.pojo.UserLimit;
import com.jh.mng.util.page.Page;

public interface OrderMapper {
	
	public List<GameInfo> queryAllGame();
	
	public List<ChnlInfo> queryAllChnl();
	
	public List<ConsumeInfo> getConsumeListById(Map<String, Object> params);
	
	public List<Income> queryIncomeListByCons(Page<Income> page);
	
	public List<Income> exportIncomeListByCons(Map<String, Object> params);
	
	public List<ConsumeInfo> queryAllConsume();

	public ConsumeInfo queryConsumeInfoByCode(Map<String, Object> params);
	
	public List<ChnlResource> queryResourceByConsumeId(Map<String, Object> params);
	
	public List<Mask> queryMaskByResIdAndState(Map<String, Object> params);
	
	public Long getNextOrderInfoId();
	
	public UserLimit getUserLimitByMdn(Map<String, Object> params);
	
	public int updateUserlimit(Map<String, Object> params);
	
	public Long sumMonthFee(Map<String, Object> params);
	
	public void createUserLimit(Map<String, Object> params);
	
	public void createReceiveSms(ReceiveSms receiveSms);
	
	public ChnlDecPercent queryDecPercent(Map<String, Object> params);
	
	public void delPercent2(Map<String, Object> params);
	
	public void createPercent2(Map<String, Object> params);
	
	public void updatePercent(Map<String, Object> params);
	
	public void updatePercent2(Map<String, Object> params); 
	
	public List<StatusQuery> queryStatusByCons(Page<StatusQuery> page);
	
	public List<StatusQuery> exportStatusByCons(Map<String, Object> params);
	
	public ReceiveSms queryReceiveSmsById(String id);
	
	public void updateReceiveSms(ReceiveSms receiveSms);
	
	public List<ReceiveSms> queryReceiveSmsByTransido(String transido);
	
	public List<ReceiveSms> queryReceiveSmsByExt(String ext);
	
	public Integer getStateByMobile(Map<String, Object> params);
	
	public ChnlInfo getChnlInfoById(Long id);
	
	public List<GameInfo> getGameInfoByChnlId(Map<String, Object> params);
	
	public int getSuccessTime(Map<String, Object> params);
	
	public List<ChnlIncomPercent> queryIncomePercent(Page<ChnlIncomPercent> page);
	
	public DdoPhone getDdoPhoneByPhone(String phone);
	
	public void createDdoPhone(String phone);
	
	public List<String> getUnknowMobile(Map<String, Object> params);
	
	public void createSmsHaoduan(Map<String, Object> params);
	
	public List<ChnlResource> queryResourceByChnlId(Map<String, Object> params);
	
	public List<ReceiveSms> getCityIncome(Map<String, Object> params);
	
	public void updateCityName(Map<String, Object> params);
	
	public GameInfo getGameInfoById(Long id);
	
	public ConsumeInfo getConsumeInfoById(Long id);
	
	public List<ReceiveSms> queryListByCondition(ReceiveSms receiveSms);
}
