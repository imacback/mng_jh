package com.jh.mng.service.order.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jh.mng.mapper.OrderMapper;
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
import com.jh.mng.pojo.Income.GroupCondition;
import com.jh.mng.service.CommonService;
import com.jh.mng.service.order.IOrderService;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.page.Page;

@Service("IOrderService")
public class OrderServiceImpl extends CommonService implements IOrderService {
	
	private static final Logger logger = Logger.getLogger(OrderServiceImpl.class);

	@Autowired
	private OrderMapper orderMapper;
	
	@Override
	public List<GameInfo> queryAllGame() {
		List<GameInfo> list = null;
		
		try {
			list = orderMapper.queryAllGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ChnlInfo> queryAllChnl() {
		List<ChnlInfo> list = null;
		
		try {
			list = orderMapper.queryAllChnl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ConsumeInfo> getConsumeListByGameId(Long gameId) {
		List<ConsumeInfo> list = null;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("gameId", gameId);
			list = orderMapper.getConsumeListById(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Income> queryIncomeByConditions(Income income, Page<Income> page) {
		String sql = "";
		List<Income> list = null;
		try {
			List<String> orderMonthList = income.getOrderMonthList();
			
			List<String> groupList = Arrays.asList(income.getGroupConditions());
			
			Collections.sort(groupList);
			
			String groupStr = "";
			for (int i = 0; i < groupList.size(); i++) {
				if (i != groupList.size() - 1) {
					groupStr += groupList.get(i) + ",";
				} else {
					groupStr += groupList.get(i);
				}
			}
			
			sql = "select * from (";
			sql += "select " + groupStr + ", Sum(Successtimes) as Successtimes ,Sum(income) as Income from (";
			
			if (orderMonthList.size() == 1) {
				sql += generateQueryIncomeSql(orderMonthList.get(0), income);
			} else {
				for (int i = 0; i < orderMonthList.size(); i++) {
					if (i == 0) {
						sql += generateQueryIncomeSql(orderMonthList.get(i), income);
					} else {
						sql += " union all ";
						sql += generateQueryIncomeSql(orderMonthList.get(i), income);
					}
				}
			}
			
			sql += ")";
			sql += " group by " + groupStr;
			if (groupStr.contains(GroupCondition.GROUP_INCOMEDATE.getCode())) {
				sql += " order by recvtime , income desc,Successtimes Desc ";
			} else {
				sql += " order by income desc,Successtimes Desc ";
			}
			sql += ")";
			
			logger.info("渠道收入查询sql为：" + sql);
			
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			page.setParams(params);
			list = orderMapper.queryIncomeListByCons(page);
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}
		return list;
	}
	
	private String generateQueryIncomeSql(String orderMonth, Income income) {
		List<String> groupList = Arrays.asList(income.getGroupConditions());
		
		Collections.sort(groupList);
		
		String groupStr = "";
		for (int i = 0; i < groupList.size(); i++) {
			if (i != groupList.size() - 1) {
				groupStr += groupList.get(i) + ",";
			} else {
				groupStr += groupList.get(i);
			}
		}
		
		String prefix = orderMonth;
		String currentMonth = DateTool.getCurrentDate("yyMM");
		
		if (orderMonth.equals(currentMonth)) {
			prefix = "";
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select " + groupStr);
		sb.append(", count(*) as successTimes, sum(fee) / 100 as income");
		sb.append(" from (");
		sb.append(" Select to_char(a.recvtime, 'yyyyMMdd') As recvtime, b.Id As consumeId, a.chnl_id as chnlId, a.state as provinceName, c.Id As gameId, a.fee  From t_receivesms" + prefix + " a, t_consume b, t_gameinfo c Where a.consumecode  = b.consumecode And b.prod_id = c.Id And a.status = '1101'");
		
		if (income.getDec_flag() != null) {
			sb.append(" and a.dec_flag = ");
			sb.append(income.getDec_flag());
		}
		if (income.getChnlId() != null) {
			sb.append(" and a.chnl_id = ");
			sb.append(income.getChnlId());
		}
		
		if (income.getGameId() != null) {
			sb.append(" and c.id = ");
			sb.append(income.getGameId());
		}
		
		if (income.getConsumeId() != null) {
			sb.append(" and b.id = ");
			sb.append(income.getConsumeId());
		}
		
		if (income.getProvinceName() != null) {
			sb.append(" and a.state = '");
			sb.append(income.getProvinceName());
			sb.append("'");
		}
		
		if (income.getStartDate() != null) {
			sb.append(" and a.recvtime >=");
			sb.append(" to_date('");
			sb.append(income.getStartDate());
			sb.append(" 00:00:00',");
			sb.append("'yyyyMMdd hh24:mi:ss')");
		}
		
		if (income.getEnDate() != null) {
			sb.append(" and a.recvtime <=");
			sb.append(" to_date('");
			sb.append(income.getEnDate());
			sb.append(" 23:59:59',");
			sb.append("'yyyyMMdd hh24:mi:ss')");
		}
		
		sb.append(") group by " + groupStr);
		
		return sb.toString();
	}
	
	@Override
	public List<Income> queryIncomeByConditions(Income income) {
		String sql = "";
		List<Income> list = null;
		try {
			List<String> orderMonthList = income.getOrderMonthList();
			List<String> groupList = Arrays.asList(income.getGroupConditions());
			
			Collections.sort(groupList);
			
			String groupStr = "";
			for (int i = 0; i < groupList.size(); i++) {
				if (i != groupList.size() - 1) {
					groupStr += groupList.get(i) + ",";
				} else {
					groupStr += groupList.get(i);
				}
			}
			
			StringBuffer sb = new StringBuffer();
			
			if (groupStr.contains(GroupCondition.GROUP_PROVINCE.getCode())) {
				
				sb.append("select * from (");
				sb.append("select " + groupStr + ", Sum(Successtimes) as Successtimes ,Sum(income) as Income from (");
				
				if (orderMonthList.size() == 1) {
					sb.append(generateProvinceSplitSql(income, orderMonthList.get(0) , groupStr));
				} else {
					for (int i = 0; i < orderMonthList.size(); i++) {
						if (i == 0) {
							sb.append(generateProvinceSplitSql(income, orderMonthList.get(i), groupStr));
						} else {
							sb.append(" union all ");
							sb.append(generateProvinceSplitSql(income, orderMonthList.get(i), groupStr));
						}
					}
				}
				sb.append(") group by ");
				sb.append(groupStr);
				
				if (groupStr.contains(GroupCondition.GROUP_INCOMEDATE.getCode())) {
					sb.append(" order by recvtime , income desc,Successtimes Desc ");
				} else {
					sb.append(" order by income desc,Successtimes Desc ");
				}
				sb.append(")");
				
			} else {
				sb.append("select * from (");
				sb.append("select " + groupStr + ", Sum(Successtimes) as Successtimes ,Sum(income) as Income from (");
				
				if (orderMonthList.size() == 1) {
					sb.append(generateExportSql(orderMonthList.get(0), income));
				} else {
					for (int i = 0; i < orderMonthList.size(); i++) {
						if (i == 0) {
							sb.append(generateExportSql(orderMonthList.get(i), income));
						} else {
							sb.append(" union all ");
							sb.append(generateExportSql(orderMonthList.get(i), income));
						}
					}
				}
				sb.append(") group by ");
				sb.append(groupStr);
				if (groupStr.contains(GroupCondition.GROUP_INCOMEDATE.getCode())) {
					sb.append(" order by recvtime , income desc,Successtimes Desc ");
				} else {
					sb.append(" order by income desc,Successtimes Desc ");
				}
				sb.append(")");
			}
			
			sql = sb.toString();
			
			logger.info("渠道收入导出sql为：" + sql);
			
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			list = orderMapper.exportIncomeListByCons(params);
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}
		return list;
	}

	public String generateExportSql(String orderMonth, Income income){
		List<String> groupList = Arrays.asList(income.getGroupConditions());
		
		Collections.sort(groupList);
		
		String groupStr = "";
		for (int i = 0; i < groupList.size(); i++) {
			if (i != groupList.size() - 1) {
				groupStr += groupList.get(i) + ",";
			} else {
				groupStr += groupList.get(i);
			}
		}
		
		String prefix = orderMonth;
		String currentMonth = DateTool.getCurrentDate("yyMM");
		
		if (orderMonth.equals(currentMonth)) {
			prefix = "";
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("select " + groupStr);
		sb.append(", count(*) as successTimes, sum(fee) / 100 as income");
		sb.append(" from (");
		sb.append(" Select to_char(a.recvtime, 'yyyyMMdd') As recvtime, b.Id As consumeId, a.chnl_id as chnlId, a.state as provinceName, c.Id As gameId, a.fee  From t_receivesms" + prefix + " a, t_consume b, t_gameinfo c Where a.consumecode  = b.consumecode And b.prod_id = c.Id And a.status = '1101'");
		
		if (income.getDec_flag() != null) {
			sb.append(" and a.dec_flag = ");
			sb.append(income.getDec_flag());
		}
		if (income.getChnlId() != null) {
			sb.append(" and a.chnl_id = ");
			sb.append(income.getChnlId());
		}
		
		if (income.getGameId() != null) {
			sb.append(" and c.id = ");
			sb.append(income.getGameId());
		}
		
		if (income.getConsumeId() != null) {
			sb.append(" and b.id = ");
			sb.append(income.getConsumeId());
		}
		
		if (income.getProvinceName() != null) {
			sb.append(" and a.state = '");
			sb.append(income.getProvinceName());
			sb.append("'");
		}
		
		if (income.getStartDate() != null) {
			sb.append(" and a.recvtime >=");
			sb.append(" to_date('");
			sb.append(income.getStartDate());
			sb.append(" 00:00:00',");
			sb.append("'yyyyMMdd hh24:mi:ss')");
		}
		
		if (income.getEnDate() != null) {
			sb.append(" and a.recvtime <=");
			sb.append(" to_date('");
			sb.append(income.getEnDate());
			sb.append(" 23:59:59',");
			sb.append("'yyyyMMdd hh24:mi:ss')");
		}
		
		sb.append(") group by " + groupStr);
		
		return sb.toString();
	}
	
	public String generateProvinceSplitSql(Income income, String orderMonth, String groupStr) {
		
		StringBuffer sb = new StringBuffer();
		List<String> tmpList = Arrays.asList(income.getGroupConditions());
		
		
		List<String> arrayList = new ArrayList<String>(tmpList);
		arrayList.remove(GroupCondition.GROUP_PROVINCE.getCode());
		arrayList.add("pstate");
		
		Collections.sort(arrayList);
		
		String tmpGroup = "";
		for (int i = 0; i < arrayList.size(); i++) {
			if (i != arrayList.size() - 1) {
				tmpGroup += arrayList.get(i) + ",";
			} else {
				tmpGroup += arrayList.get(i);
			}
		}
		
		String prefix = orderMonth;
		String currentMonth = DateTool.getCurrentDate("yyMM");
		
		if (orderMonth.equals(currentMonth)) {
			prefix = "";
		}
		
		sb.append(generateExportSql(orderMonth, income));
		
		if (groupStr.contains(GroupCondition.GROUP_INCOMEDATE.getCode())
				&& !income.getStartDate().equals(income.getEnDate())) { //选择了日期分组条件
			
			Date startDate = DateTool.StringToDate(income.getStartDate(), "yyyyMMdd");
			Date endDate = DateTool.StringToDate(income.getEnDate(), "yyyyMMdd");
			
			int distance = DateTool.differentDays(startDate, endDate);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			
			for (int i = 0; i <= distance; i++) {
				String dateStr = DateTool.increaseDate(cal, i, "yyyyMMdd");
				cal.setTime(startDate);
				
				sb.append(" union all ");
				sb.append("select " + tmpGroup);
				sb.append(" ,Sum(f.Successtimes),sum(f.Income) ");
				sb.append(" From(");
				sb.append("Select a.state as pstate From sms_province a Where Not Exists (Select 1 From(");
				
				sb.append("select " + groupStr);
				sb.append(", count(*) as successTimes, sum(fee) / 100 as income");
				sb.append(" from (");
				sb.append(" Select '" + dateStr + "' As recvtime, b.Id As consumeId, a.chnl_id as chnlId, a.state as provinceName, c.Id As gameId, a.fee  From t_receivesms" + prefix + " a, t_consume b, t_gameinfo c Where a.consumecode  = b.consumecode And b.prod_id = c.Id And a.status = '1101'");
				
				if (income.getDec_flag() != null) {
					sb.append(" and a.dec_flag = ");
					sb.append(income.getDec_flag());
				}
				if (income.getChnlId() != null) {
					sb.append(" and a.chnl_id = ");
					sb.append(income.getChnlId());
				}
				
				if (income.getGameId() != null) {
					sb.append(" and c.id = ");
					sb.append(income.getGameId());
				}
				
				if (income.getConsumeId() != null) {
					sb.append(" and b.id = ");
					sb.append(income.getConsumeId());
				}
				
				if (income.getProvinceName() != null) {
					sb.append(" and a.state = '");
					sb.append(income.getProvinceName());
					sb.append("'");
				}
				
				sb.append(" and to_char(a.recvtime, 'yyyyMMdd') = '");
				sb.append(dateStr);
				sb.append("'");
				
				sb.append(") group by " + groupStr);
				
				sb.append(")t Where a.state = t.Provincename)) t, (");
				
				sb.append("select " + groupStr);
				sb.append(", 0 as  successTimes, 0 as income");
				sb.append(" from (");
				sb.append(" Select '" + dateStr + "' As recvtime, b.Id As consumeId, a.chnl_id as chnlId, a.state as provinceName, c.Id As gameId, a.fee  From t_receivesms" + prefix + " a, t_consume b, t_gameinfo c Where a.consumecode  = b.consumecode And b.prod_id = c.Id And a.status = '1101'");
				
				if (income.getDec_flag() != null) {
					sb.append(" and a.dec_flag = ");
					sb.append(income.getDec_flag());
				}
				if (income.getChnlId() != null) {
					sb.append(" and a.chnl_id = ");
					sb.append(income.getChnlId());
				}
				
				if (income.getGameId() != null) {
					sb.append(" and c.id = ");
					sb.append(income.getGameId());
				}
				
				if (income.getConsumeId() != null) {
					sb.append(" and b.id = ");
					sb.append(income.getConsumeId());
				}
				
				if (income.getProvinceName() != null) {
					sb.append(" and a.state = '");
					sb.append(income.getProvinceName());
					sb.append("'");
				}
				
				sb.append(" and to_char(a.recvtime, 'yyyyMMdd') = '");
				sb.append(dateStr);
				sb.append("'");
				
				sb.append(") group by " + groupStr);
				sb.append(") f Group By ");
				sb.append(tmpGroup);
			}
		} else {
			sb.append(" union all ");
			sb.append("select " + tmpGroup);
			sb.append(" ,Sum(f.Successtimes),sum(f.Income) ");
			sb.append(" From(");
			sb.append("Select a.state as pstate From sms_province a Where Not Exists (Select 1 From(");
			
			sb.append(generateExportSql(orderMonth, income));
			
			sb.append(")t Where a.state = t.Provincename)) t, (");
			
			sb.append("select " + groupStr);
			sb.append(", 0 as  successTimes, 0 as income");
			sb.append(" from (");
			sb.append(" Select to_char(a.recvtime, 'yyyyMMdd') As recvtime, b.Id As consumeId, a.chnl_id as chnlId, a.state as provinceName, c.Id As gameId, a.fee  From t_receivesms" + prefix + " a, t_consume b, t_gameinfo c Where a.consumecode  = b.consumecode And b.prod_id = c.Id And a.status = '1101'");
			
			if (income.getDec_flag() != null) {
				sb.append(" and a.dec_flag = ");
				sb.append(income.getDec_flag());
			}
			if (income.getChnlId() != null) {
				sb.append(" and a.chnl_id = ");
				sb.append(income.getChnlId());
			}
			
			if (income.getGameId() != null) {
				sb.append(" and c.id = ");
				sb.append(income.getGameId());
			}
			
			if (income.getConsumeId() != null) {
				sb.append(" and b.id = ");
				sb.append(income.getConsumeId());
			}
			
			if (income.getProvinceName() != null) {
				sb.append(" and a.state = '");
				sb.append(income.getProvinceName());
				sb.append("'");
			}
			
			if (income.getStartDate() != null) {
				sb.append(" and a.recvtime >=");
				sb.append(" to_date('");
				sb.append(income.getStartDate());
				sb.append(" 00:00:00',");
				sb.append("'yyyyMMdd hh24:mi:ss')");
			}
			
			if (income.getEnDate() != null) {
				sb.append(" and a.recvtime <=");
				sb.append(" to_date('");
				sb.append(income.getEnDate());
				sb.append(" 23:59:59',");
				sb.append("'yyyyMMdd hh24:mi:ss')");
			}
			
			sb.append(") group by " + groupStr);
			sb.append(") f Group By ");
			sb.append(tmpGroup);
		}
		
		return sb.toString();
	}
	
	@Override
	public List<ConsumeInfo> queryAllConsume() {
		List<ConsumeInfo> list = null;
		
		try {
			list = orderMapper.queryAllConsume();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Income> queryConversionRate(Income income, Page<Income> page) {
		String sql = "";
		List<Income> list = null;
		try {
			List<String> orderMonthList = income.getOrderMonthList();
			
			sql = "select * from (";
			
			if (orderMonthList.size() == 1) {
				sql += generateConversionRateSql(orderMonthList.get(0), income);
			} else {
				for (int i = 0; i < orderMonthList.size(); i++) {
					if (i == 0) {
						sql += generateConversionRateSql(orderMonthList.get(i), income);
					} else {
						sql += " union all ";
						sql += generateConversionRateSql(orderMonthList.get(i), income);
					}
				}
			}
			
			sql += ")";
			
			logger.info("转化率分析查询sql为：" + sql);
			
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			page.setParams(params);
			list = orderMapper.queryIncomeListByCons(page);
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}
		return list;
	}
	
	private String generateConversionRateSql(String orderMonth, Income income) {
		List<String> groupList = Arrays.asList(income.getGroupConditions());
		
		Collections.sort(groupList);
		
		String groupStr = "";
		for (int i = 0; i < groupList.size(); i++) {
			if (i != groupList.size() - 1) {
				groupStr += groupList.get(i) + ",";
			} else {
				groupStr += groupList.get(i);
			}
		}
		
		String prefix = orderMonth;
		String currentMonth = DateTool.getCurrentDate("yyMM");
		
		if (orderMonth.equals(currentMonth)) {
			prefix = "";
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("select " + groupStr);
		sb.append(", count(*) as reqTimes, Count(Case When status = '1101' Then 1 Else Null End ) As succTimes ,Count(Case When status != '1101' Then 1 Else Null End) As failtimes");
		sb.append(" from (");
		sb.append(" Select to_char(a.recvtime, 'yyyyMMdd') As recvtime, to_char(a.Recvtime, 'hh24') As recvHour, b.Id As consumeId, a.chnl_id as chnlId, a.state as provinceName, c.Id As gameId, a.status  From t_receivesms"  + prefix + " a, t_consume b, t_gameinfo c Where a.consumecode  = b.consumecode And b.prod_id = c.Id ");
		
		if (income.getChnlId() != null) {
			sb.append(" and a.chnl_id = ");
			sb.append(income.getChnlId());
		}
		
		if (income.getGameId() != null) {
			sb.append(" and c.id = ");
			sb.append(income.getGameId());
		}
		
		if (income.getConsumeId() != null) {
			sb.append(" and b.id = ");
			sb.append(income.getConsumeId());
		}
		
		if (income.getProvinceName() != null) {
			sb.append(" and a.state = '");
			sb.append(income.getProvinceName());
			sb.append("'");
		}
		
		if (income.getStartDate() != null) {
			sb.append(" and a.recvtime >=");
			sb.append(" to_date('");
			sb.append(income.getStartDate());
			sb.append(" 00:00:00',");
			sb.append("'yyyyMMdd hh24:mi:ss')");
		}
		
		if (income.getEnDate() != null) {
			sb.append(" and a.recvtime <=");
			sb.append(" to_date('");
			sb.append(income.getEnDate());
			sb.append(" 23:59:59',");
			sb.append("'yyyyMMdd hh24:mi:ss')");
		}
		
		if (income.getRecvHour() != null) {
			sb.append(" and to_char(a.Recvtime, 'hh24') = '");
			sb.append(income.getRecvHour());
			sb.append("'");
		}
		sb.append(") group by " + groupStr);
		
		return sb.toString();
	}

	@Override
	public List<Income> queryLastFeeByConditions(Income income,
			Page<Income> page) {
		String sql = "";
		List<Income> list = null;
		try {
			List<String> orderMonthList = income.getOrderMonthList();
			
			sql = "select * from (";
			
			if (orderMonthList.size() == 1) {
				sql += generateFeeSql(orderMonthList.get(0), income);
			} else {
				for (int i = 0; i < orderMonthList.size(); i++) {
					if (i == 0) {
						sql += generateFeeSql(orderMonthList.get(i), income);
					} else {
						sql += " union all ";
						sql += generateFeeSql(orderMonthList.get(i), income);
					}
				}
			}
			
			sql += ")";
			
			logger.info("最后计费时间查询sql为：" + sql);
			
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			page.setParams(params);
			list = orderMapper.queryIncomeListByCons(page);
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}
		return list;
	}
	
	private String generateFeeSql(String orderMonth, Income income) {
		List<String> groupList = Arrays.asList(income.getGroupConditions());
		
		Collections.sort(groupList);
		
		String groupStr = "";
		for (int i = 0; i < groupList.size(); i++) {
			if (i != groupList.size() - 1) {
				groupStr += groupList.get(i) + ",";
			} else {
				groupStr += groupList.get(i);
			}
		}
		
		String prefix = orderMonth;
		String currentMonth = DateTool.getCurrentDate("yyMM");
		
		if (orderMonth.equals(currentMonth)) {
			prefix = "";
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("select " + groupStr);
		sb.append(", status, max(lastFeeTime) as lastFeeTime");
		sb.append(" from (");
		sb.append(" Select to_char(a.recvtime, 'yyyyMMdd') As recvtime, b.Id As consumeId, a.chnl_id as chnlId, a.state as provinceName, c.Id As gameId, a.status, a.recvtime as lastFeeTime  From t_receivesms" + prefix + " a, t_consume b, t_gameinfo c Where a.consumecode  = b.consumecode And b.prod_id = c.Id ");
		
		if (income.getChnlId() != null) {
			sb.append(" and a.chnl_id = ");
			sb.append(income.getChnlId());
		}
		
		if (income.getGameId() != null) {
			sb.append(" and c.id = ");
			sb.append(income.getGameId());
		}
		
		if (income.getConsumeId() != null) {
			sb.append(" and b.id = ");
			sb.append(income.getConsumeId());
		}
		
		if (income.getProvinceName() != null) {
			sb.append(" and a.state = '");
			sb.append(income.getProvinceName());
			sb.append("'");
		}
		
		if (income.getStartDate() != null) {
			sb.append(" and a.recvtime >=");
			sb.append(" to_date('");
			sb.append(income.getStartDate());
			sb.append(" 00:00:00',");
			sb.append("'yyyyMMdd hh24:mi:ss')");
		}
		
		if (income.getEnDate() != null) {
			sb.append(" and a.recvtime <=");
			sb.append(" to_date('");
			sb.append(income.getEnDate());
			sb.append(" 23:59:59',");
			sb.append("'yyyyMMdd hh24:mi:ss')");
		}
		sb.append(") group by " + groupStr);
		sb.append(" ,status");
		
		return sb.toString();
	}

	@Override
	public ConsumeInfo queryConsumeInfoByCode(String consumeCode) {
		ConsumeInfo consumeInfo = null;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("consumeCode", consumeCode);
			
			consumeInfo = orderMapper.queryConsumeInfoByCode(params);
		} catch (Exception e) {
			e.printStackTrace();
			consumeInfo = null;
		}
		return consumeInfo;
	}

	@Override
	public List<ChnlResource> queryResourceByConsumeId(Long today,
			Long consumeId) {
		List<ChnlResource> list = null;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("consumeId", consumeId);
			params.put("today", today);
			
			list = orderMapper.queryResourceByConsumeId(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Mask> queryMaskByResIdAndState(Long resourceId, String state) {
		List<Mask> list = null;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("resourceId", resourceId);
			params.put("state", state);
			
			list = orderMapper.queryMaskByResIdAndState(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Long getNextOrderId() {
		Long nextId = null;
		
		try {
			nextId = orderMapper.getNextOrderInfoId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextId;
	}

	@Override
	public UserLimit getUserLimitByMdn(String mdn) {
		UserLimit userLimit = null;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mdn", mdn);
			
			userLimit = orderMapper.getUserLimitByMdn(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userLimit;
	}

	@Override
	public int updateUserLimit(UserLimit userLimit) {
		int result = -1;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mdn", userLimit.getMdn());
			params.put("rq", userLimit.getRq());
			params.put("r_fee", userLimit.getR_fee());
			params.put("y_fee", userLimit.getY_fee());
			
			result = orderMapper.updateUserlimit(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Long sumMonthFee(String today, String mdn) {
		Long result = 0L;
		
		try {
			String sql = "select sum(fee) as sum_fee_y from t_receivesms where userid='"+mdn.trim()+"'"+
			" and recvtime>=to_date('"+today.substring(0,6)+"01000000','yyyymmddhh24miss') and fee>0";
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", sql);
			
			result = orderMapper.sumMonthFee(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Long sumDayFee(String today, String mdn) {
		Long result = 0L;
		
		try {
			String sql = "select sum(fee) as sum_fee_r from t_receivesms where userid='"+mdn.trim()+"'"+
			" and recvtime>=to_date('"+today+"000000','yyyymmddhh24miss') and fee>0";
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", sql);
			
			result = orderMapper.sumMonthFee(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int createUserLimit(UserLimit userLimit) {
		int result = 0;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mdn", userLimit.getMdn());
			params.put("rq", userLimit.getRq());
			params.put("r_fee", userLimit.getR_fee());
			params.put("y_fee", userLimit.getY_fee());
			
			orderMapper.createUserLimit(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int createReceiveSms(ReceiveSms receiveSms) {
		int result = 0;
		
		try {
			orderMapper.createReceiveSms(receiveSms);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("createReceiveSms error : " + e.getMessage());
		}
		return result;
	}

	@Override
	public ChnlDecPercent queryDecPercent(int chnl_id, String consumecode,
			String packageid, String today) {
		ChnlDecPercent chnlDecPercent = null;
		
		try {
			String str = "select * from t_chnl_dec_percent where bztype=1 and chnl_id=" +
	          chnl_id + " and consume_id in " +
	          "(" +
	          "select id from t_consume where consumecode='" + consumecode +
	          "' and prod_id in (select id from t_gameinfo where packageid=" +
	          packageid + ")" +
	          ")" +
	          " and percent > 0 and startrq<=" + today + " and endrq >= " + today;
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", str);
			
			chnlDecPercent = orderMapper.queryDecPercent(params);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("queryDecPercent error :" + e.getMessage());
		}
		return chnlDecPercent;
	}

	@Override
	public void delPercent2(int chnl_id, String consumecode, String packageid) {
		try {
			String str = "delete from t_chnl_dec_percent2 where chnl_id=" + chnl_id +
            " and consumecode='" + consumecode + "' and packageid=" +
            packageid;
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", str);
			
			orderMapper.delPercent2(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPercent2(int chnl_id, String consumecode,
			String packageid, String today, double percent, int baseline) {
		try {
			String str = "insert into t_chnl_dec_percent2(id,chnl_id,state,packageid,consumecode,baseline,percent,curday,curcnt) " +
            "select seq_chnl_dec_percent_id.nextval," + chnl_id + ",state," +
            packageid + ",'" + consumecode + "'," + baseline + "," + percent +
            "," + today + ",0" +
            " from sms_province";
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", str);
			
			orderMapper.createPercent2(params);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("createPercent2 error :" + e.getMessage());
		}
		
	}

	@Override
	public void updatePercent(String today, Long id) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("today", today);
			params.put("id", id);
			
			orderMapper.updatePercent(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public ChnlDecPercent queryDecPercent2(int chnl_id, String consumecode,
			String packageid, String state) {
		ChnlDecPercent chnlDecPercent = null;
		
		try {
			String str = "select * from t_chnl_dec_percent2 where chnl_id=" + chnl_id +
            " and consumecode='" + consumecode +
            "' and packageid=" + packageid + " and state='" + state + "'";
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", str);
			
			chnlDecPercent = orderMapper.queryDecPercent(params);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("queryDecPercent error :" + e.getMessage());
		}
		return chnlDecPercent;
	}

	@Override
	public void updatePercent2(int chnl_id, String consumecode,
			String packageid, String state) {
		try {
			String str = "update t_chnl_dec_percent2 set curcnt=curcnt+1 where chnl_id=" +
            chnl_id + " and consumecode='" + consumecode +
            "' and packageid=" + packageid ;
         //   + " and state='" + state + "'";
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", str);
			
			orderMapper.updatePercent2(params);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("queryDecPercent error :" + e.getMessage());
		}
	}

	@Override
	public List<StatusQuery> queryOrderStatus(StatusQuery statusQuery,
			Page<StatusQuery> page) {
		List<StatusQuery> list = null;
		
		try {
			List<String> orderMonthList = statusQuery.getOrderMonthList();
			
			String sql = "select * from (";
			
			if (orderMonthList.size() == 1) {
				sql += generateOrderStatusQuerySql(orderMonthList.get(0), statusQuery);
			} else {
				for (int i = orderMonthList.size() - 1; i >= 0; i--) {
					if (i == orderMonthList.size() - 1) {
						sql += generateOrderStatusQuerySql(orderMonthList.get(i), statusQuery);
					} else {
						sql += " union all ";
						sql += generateOrderStatusQuerySql(orderMonthList.get(i), statusQuery);
					}
				}
			}
			
			sql += ")";
					
			logger.info("状态查询sql为 ： " + sql);
				Map<String, Object> params = new HashMap<String, Object>(); 
				params.put("sql", sql);
				page.setParams(params);
				list = orderMapper.queryStatusByCons(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private String generateOrderStatusQuerySql(String orderMonth, StatusQuery statusQuery) {
		
		String sql = "select * from (";
		
		String prefix = orderMonth;
		String currentMonth = DateTool.getCurrentDate("yyMM");
		
		if (orderMonth.equals(currentMonth)) {
			prefix = "";
		}
		
		if (statusQuery.getIsCount() == 1) { //汇总
			sql += "Select a.hret, a.status,";
			
			if (statusQuery.isExtBox()) {
				sql += " a.ext, ";
			}
			sql += 	"Count(*) As times From t_receivesms" + prefix + " a Left Join t_Channel b On a.chnl_id = b.Id, t_gameinfo c,t_consume d ";
		} else if (statusQuery.getIsCount() == 2) {
			sql += "Select a.hret, a.recvtime,a.cpparam,a.status,b.chnlname,c.prodname, a.phoneno,a.syncchnl,d.consumename,a.ext, a.state " +
					"From t_receivesms" + prefix + " a Left Join t_Channel b On a.chnl_id = b.Id , t_gameinfo c,t_consume d ";
		}
		
		sql += "Where  a.consumecode = d.consumecode And d.prod_id = c.id ";
		if (statusQuery.getStatusInteger() != null) {
			if (statusQuery.getStatusInteger().intValue() == 1) {
					sql += " and a.status = '1101'";
			} else {
					sql += " and a.status != '1101'";
			}
		}
				
		if (statusQuery.getGameId() != null) {
			sql += " and c.id = ";
			sql += statusQuery.getGameId();
		}
				
		if (statusQuery.getChnlId() != null) {
			sql += " and b.id = ";
			sql += statusQuery.getChnlId();
		}
				
		if (statusQuery.getPhoneno() != null) {
			
			String[] phones = statusQuery.getPhoneno().split("\r\n");
			
			StringBuffer phoneStr = new StringBuffer();
			
			for (int i = 0; i < phones.length; i++) {
				if (i != phones.length - 1) {
					phoneStr.append("'");
					phoneStr.append(phones[i]);
					phoneStr.append("',");
				} else {
					phoneStr.append("'");
					phoneStr.append(phones[i]);
					phoneStr.append("'");
				}
			}
			sql += " and a.phoneno in(";
			sql += phoneStr.toString();
			sql += ")";
		}
				
		if (statusQuery.getStartDate() != null) {
			sql += " and a.recvtime >=";
			sql += " to_date('";
			sql += statusQuery.getStartDate();
			sql += " 00:00:00',";
			sql += "'yyyyMMdd hh24:mi:ss')";
		}
			
		if (statusQuery.getEnDate() != null) {
			sql += " and a.recvtime <=";
			sql += " to_date('";
			sql += statusQuery.getEnDate();
			sql += " 23:59:59',";
			sql += "'yyyyMMdd hh24:mi:ss')";
		}
				
		if (statusQuery.getProviderId() != null) {
			sql += " and a.state = '";
			sql += statusQuery.getProviderId();
			sql += "'";
		}
		
		
		if (statusQuery.getIsCount() == 1) {
			sql += " Group By a.hret, a.status";
			if (statusQuery.isExtBox()) {
				sql += ", a.ext ";
			}
		} else if (statusQuery.getIsCount() == 2) {
			sql += " order by a.recvtime desc ";
		}
		
		sql += ")";
		return sql;
	}

	@Override
	public ReceiveSms queryReceiveSmsById(String id) {
		ReceiveSms receiveSms = null;
		
		try {
			receiveSms = orderMapper.queryReceiveSmsById(id);
		} catch (Exception e) {
		}
		return receiveSms;
	}

	@Override
	public void updateReceiveSms(ReceiveSms receiveSms) {
		try {
			orderMapper.updateReceiveSms(receiveSms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<ReceiveSms> queryReceiveSmsByTransido(String transido) {
		List<ReceiveSms> receiveSmsList = null;
		
		try {
			receiveSmsList = orderMapper.queryReceiveSmsByTransido(transido);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiveSmsList;
	}

	@Override
	public int getStateIdByMobile(String mobile) {
		int state = 0;
		
		try {
			String sql = "select id from sms_province where id in ("+
			"select state from sms_haoduan_new where phone='" + mobile.substring(0,7)+"')";
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", sql);
			
			Integer result = orderMapper.getStateByMobile(params);
			
			if (result != null) {
				state = result.intValue();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public ChnlInfo getChnlInfoById(Long id) {
		ChnlInfo chnlInfo = null;
		
		try {
			chnlInfo = orderMapper.getChnlInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chnlInfo;
	}

	@Override
	public List<GameInfo> getGameInfoByChnlId(Long chnlId) {
		List<GameInfo> list = null;
		
		try {
			String sql = "Select c.Id,c.prodname From t_chnlresource a, t_consume b, t_gameinfo c Where a.consume_id = b.Id And b.prod_id = c.Id";
					sql += " and a.chnl_id = " + chnlId;
					sql += " Group By c.Id ,c.prodname";
					
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", sql);
					
			list = orderMapper.getGameInfoByChnlId(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public int getLastSuccessTime(Long gameInfoId) {
		int result = 0;
		
		try {
			String sql = "select (sysdate-nvl(max(recvtime),sysdate-1))*24*60 from t_receivesms Where consumecode In(Select b.consumecode From t_gameinfo a , t_consume b Where a.Id = b.prod_id And a.Id = " + gameInfoId + ")";
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sql", sql);
			
			result = orderMapper.getSuccessTime(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<ChnlIncomPercent> queryIncomePercent(
			ChnlIncomPercent chnlIncomPercent, Page<ChnlIncomPercent> page) {
		List<ChnlIncomPercent> list = null;
		String sql = "";
		try {
			List<String> orderMonthList = chnlIncomPercent.getOrderMonthList();
			
			sql = "select * from (";
			
			if (orderMonthList.size() == 1) {
				sql += generatePercentSql(orderMonthList.get(0), chnlIncomPercent);
			} else {
				for (int i = 0; i < orderMonthList.size(); i++) {
					if (i == 0) {
						sql += generatePercentSql(orderMonthList.get(i), chnlIncomPercent);
					} else {
						sql += " union all ";
						sql += generatePercentSql(orderMonthList.get(i), chnlIncomPercent);
					}
				}
			}
			
			sql += ")";
			
	        logger.info("渠道收入分省占比查询sql: " + sql);
	        
	        Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			page.setParams(params);
			
			list = orderMapper.queryIncomePercent(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private String generatePercentSql(String orderMonth, ChnlIncomPercent chnlIncomPercent) {
		
		String prefix = orderMonth;
		String currentMonth = DateTool.getCurrentDate("yyMM");
		
		if (orderMonth.equals(currentMonth)) {
			prefix = "";
		}
		
		String sql = "Select t.Chnlname As chnlName, t.Recvtime As orderDate, t.Prodname As gameName, t.State, t.Chnlfee As chnlInCome, f.Totalfee As sumInCome ";
		   sql += "From (Select d.Chnlname, To_Char(a.Recvtime, 'yyyy-MM-dd') As Recvtime, c.Prodname, State, Sum(a.Fee) As Chnlfee ";
		   sql += "From t_Receivesms" ;
		   sql += prefix;
		   sql += " a, t_Consume b, t_Gameinfo c, t_Channel d ";
		   sql += "Where a.Consumecode = b.Consumecode And b.Prod_Id = c.Id And a.Status = '1101' And a.Chnl_Id = d.Id ";
		   
		   if (chnlIncomPercent.getGameInfoId() != null) {
			   sql += " and c.id = ";
			   sql += chnlIncomPercent.getGameInfoId();
		   }
		   
		   if (chnlIncomPercent.getChnlId() != null) {
			   sql += " and d.id = ";
			   sql += chnlIncomPercent.getChnlId();
		   }
		   
		   if (chnlIncomPercent.getState() != null) {
			   sql += " and a.state = '";
			   sql += chnlIncomPercent.getState();
			   sql += "' ";
		   }
		   
		   if (chnlIncomPercent.getStartDate() != null) {
				sql += " and a.recvtime >=";
				sql += " to_date('";
				sql += chnlIncomPercent.getStartDate();
				sql += " 00:00:00',";
				sql += "'yyyyMMdd hh24:mi:ss')";
			}
			
			if (chnlIncomPercent.getEnDate() != null) {
				sql += " and a.recvtime <=";
				sql += " to_date('";
				sql += chnlIncomPercent.getEnDate();
				sql += " 23:59:59',";
				sql += "'yyyyMMdd hh24:mi:ss')";
			}
		   
		   sql += " Group By d.Chnlname, To_Char(a.Recvtime, 'yyyy-MM-dd'), c.Prodname, State) t, ";
		   sql += " (Select c.Prodname, To_Char(a.Recvtime, 'yyyy-MM-dd') As Recvtime, State, Sum(a.Fee) As Totalfee ";
		   sql += " From t_Receivesms";
		   sql += prefix;
		   sql +=" a, t_Consume b, t_Gameinfo c";
		   sql += " Where a.Consumecode = b.Consumecode And b.Prod_Id = c.Id And a.Status = '1101' ";
		   
		   if (chnlIncomPercent.getGameInfoId() != null) {
			   sql += " and c.id = ";
			   sql += chnlIncomPercent.getGameInfoId();
		   }
		   
		   if (chnlIncomPercent.getState() != null) {
			   sql += " and a.state = '";
			   sql += chnlIncomPercent.getState();
			   sql += "' ";
		   }
		   
		   if (chnlIncomPercent.getStartDate() != null) {
				sql += " and a.recvtime >=";
				sql += " to_date('";
				sql += chnlIncomPercent.getStartDate();
				sql += " 00:00:00',";
				sql += "'yyyyMMdd hh24:mi:ss')";
			}
			
			if (chnlIncomPercent.getEnDate() != null) {
				sql += " and a.recvtime <=";
				sql += " to_date('";
				sql += chnlIncomPercent.getEnDate();
				sql += " 23:59:59',";
				sql += "'yyyyMMdd hh24:mi:ss')";
			}
			
		   sql += " Group By c.Prodname, To_Char(a.Recvtime, 'yyyy-MM-dd'), State) f ";
		   sql += " Where t.Prodname = f.Prodname And t.State = f.State And t.Recvtime = f.Recvtime ";
		   
		   return sql;
	}
	
	@Override
	public DdoPhone queryByPhone(String phone) {
		DdoPhone ddoPhone = null;
		
		try {
			ddoPhone = orderMapper.getDdoPhoneByPhone(phone);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ddoPhone;
	}

	@Override
	public void createDdoPhone(String phone) {
		try {
			orderMapper.createDdoPhone(phone);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public List<StatusQuery> queryOrderStatus(StatusQuery statusQuery) {
		List<StatusQuery> list = null;
		
		try {
			List<String> orderMonthList = statusQuery.getOrderMonthList();
			
			String sql = "select * from (";
			
			if (orderMonthList.size() == 1) {
				sql += generateOrderStatusQuerySql(orderMonthList.get(0), statusQuery);
			} else {
				for (int i = 0; i < orderMonthList.size(); i++) {
					if (i == 0) {
						sql += generateOrderStatusQuerySql(orderMonthList.get(i), statusQuery);
					} else {
						sql += " union all ";
						sql += generateOrderStatusQuerySql(orderMonthList.get(i), statusQuery);
					}
				}
			}
			
			sql += ")";
					
			logger.info("状态查询导出sql为 ： " + sql);
				Map<String, Object> params = new HashMap<String, Object>(); 
				params.put("sql", sql);
				list = orderMapper.exportStatusByCons(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<String> getUnknowMobile() {
		List<String> list = null;
		
		try {
//			String sql = "Select phoneno From t_receivesms a Where  consumecode In(Select consumecode From t_consume Where prod_id = 581) And status = '1101'  and state = '未知' And length(phoneno) = 11 And Not Exists (Select 1 From sms_haoduan_new b Where substr(a.phoneno,0,7) = b.phone)";
			String sql = "Select phoneno From t_receivesms a Where state = '未知' And length(phoneno) = 11 And Not Exists (Select 1 From sms_haoduan_new b Where substr(a.phoneno,0,7) = b.phone) And a.phoneno Like '1%'";
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			
			list = orderMapper.getUnknowMobile(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void createSmsHaoduan(String mobileStr, int state) {
		try {
			String sql = "Insert Into sms_haoduan_new (Id,area_id,phone,state,create_time)Values( sms_haoduan_new_seq.Nextval,'68', '" + mobileStr +"', '" + state + "', sysdate)";
			
			System.out.println(sql);
			
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			
			orderMapper.createSmsHaoduan(params);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
	}

	@Override
	public List<ChnlResource> queryResourceByChnlId(Long chnlId) {
		List<ChnlResource> list = null;
		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("chnlId", chnlId);
			
			list = orderMapper.queryResourceByChnlId(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ReceiveSms> getCityIncomeByGameId(String month, String gameId) {
		List<ReceiveSms> list = null;
		
		try {
			String sql = "Select * From t_Receivesms" + month + "_backup Where Consumecode In (Select Consumecode From t_Consume Where Prod_Id = " + gameId + ") And Status = '1101' And Length(Phoneno) = 11 and PROVINCENAME is  null and city is null order by id desc";
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			
			list = orderMapper.getCityIncome(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void updateCity(Long id, String month, String provinceName, String cityName) {
		try {
			String sql = "Update t_Receivesms" + month + "_backup Set PROVINCENAME = '" + provinceName + "', city = '" + cityName + "' Where Id =" + id;
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			
			orderMapper.updateCityName(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public List<ReceiveSms> queryReceiveSmsByExt(String ext) {
		List<ReceiveSms> receiveSmsList = null;
		
		try {
			receiveSmsList = orderMapper.queryReceiveSmsByExt(ext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiveSmsList;
	}

	@Override
	public GameInfo getGameInfoById(Long id) {
		GameInfo gameInfo = null;
		
		try {
			gameInfo = orderMapper.getGameInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameInfo;
	}

	@Override
	public ConsumeInfo queryConsumeInfoById(Long id) {
		ConsumeInfo consumeInfo = null;
		
		try {
			consumeInfo = orderMapper.getConsumeInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return consumeInfo;
	}

	@Override
	public List<ReceiveSms> getChnlIncomeByGameId(String day, String gameId, Long chnlId) {
		List<ReceiveSms> list = null;
		
		try {
			String sql = "Select * From t_Receivesms Where to_char(recvtime,'yyyy-MM-dd') = '" + day + "' and Consumecode In (Select Consumecode From t_Consume Where Prod_Id = " + gameId + ") And Status = '1101' And Length(Phoneno) = 11 and state = '未知' and chnl_id = '" + chnlId + "' order by id desc";
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			
			list = orderMapper.getCityIncome(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void updateState(Long id, String provinceName) {
		try {
			String sql = "Update t_Receivesms Set state = '" + provinceName + "' Where Id =" + id;
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("sql", sql);
			
			orderMapper.updateCityName(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public List<ReceiveSms> queryReceiveSmsByCondition(ReceiveSms receiveSms) {
		return orderMapper.queryListByCondition(receiveSms);
	}

}
