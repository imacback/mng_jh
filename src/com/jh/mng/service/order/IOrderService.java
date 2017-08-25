package com.jh.mng.service.order;

import java.util.List;

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

/**
 * 收入查询
 * @author admin
 *
 */
public interface IOrderService {
	
	public List<GameInfo> queryAllGame();
	
	public List<ChnlInfo> queryAllChnl();
	
	public List<ConsumeInfo> getConsumeListByGameId(Long gameId);
	
	public List<Income> queryIncomeByConditions(Income income, Page<Income> page);
	
	public List<Income> queryIncomeByConditions(Income income);
	
	public List<ConsumeInfo> queryAllConsume();
	
	public List<Income> queryConversionRate(Income income, Page<Income> page);
	
	public List<Income> queryLastFeeByConditions(Income income, Page<Income> page);
	
	public ConsumeInfo queryConsumeInfoByCode(String consumeCode);
	
	public List<ChnlResource> queryResourceByConsumeId(Long today, Long consumeId);
	
	public List<Mask> queryMaskByResIdAndState(Long resourceId, String state);
	
	public Long getNextOrderId();

	public UserLimit getUserLimitByMdn(String mdn);
	
	public int updateUserLimit(UserLimit userLimit);
	
	public Long sumMonthFee(String today, String mdn);
	
	public Long sumDayFee(String today, String mdn);
	
	public int createUserLimit(UserLimit userLimit);
	
	public int createReceiveSms(ReceiveSms receiveSms);
	
	public ChnlDecPercent queryDecPercent(int chnl_id, String consumecode, String packageid, String today);
	
	public void delPercent2(int chnl_id, String consumecode, String packageid);
	
	public void createPercent2(int chnl_id, String consumecode, String packageid, String today, double percent, int baseline);
	
	public void updatePercent(String today, Long id);
	
	public ChnlDecPercent queryDecPercent2(int chnl_id, String consumecode, String packageid, String state);
	
	public void updatePercent2(int chnl_id, String consumecode, String packageid, String state);
	
	public List<StatusQuery> queryOrderStatus(StatusQuery statusQuery, Page<StatusQuery> page);
	
	public List<StatusQuery> queryOrderStatus(StatusQuery statusQuery);
	
	public ReceiveSms queryReceiveSmsById(String id);
	
	public void updateReceiveSms(ReceiveSms receiveSms);
	
	public List<ReceiveSms> queryReceiveSmsByTransido(String transido);
	
	public List<ReceiveSms> queryReceiveSmsByExt(String ext);
	
	public List<ReceiveSms> queryReceiveSmsByCondition(ReceiveSms receiveSms);
	
	public int getStateIdByMobile(String mobile);
	
	public ChnlInfo getChnlInfoById(Long id);
	
	public List<GameInfo> getGameInfoByChnlId(Long chnlId);
	
	public int getLastSuccessTime(Long gameInfoId);
	
	public List<ChnlIncomPercent> queryIncomePercent(ChnlIncomPercent chnlIncomPercent, Page<ChnlIncomPercent> page);
	
	public DdoPhone queryByPhone(String phone);
	
	public void createDdoPhone(String phone);
	
	public List<String> getUnknowMobile();
	
	public void createSmsHaoduan(String mobileStr, int state);
	
	public List<ChnlResource> queryResourceByChnlId(Long chnlId);
	
	public List<ReceiveSms> getCityIncomeByGameId(String month, String gameId);
	
	public void updateCity(Long id, String month, String provinceName, String cityName);
	
	public List<ReceiveSms> getChnlIncomeByGameId(String day, String gameId, Long chnlId);
	
	public void updateState(Long id, String provinceName);
	
	public GameInfo getGameInfoById(Long id);
	
	public ConsumeInfo queryConsumeInfoById(Long id);
}
