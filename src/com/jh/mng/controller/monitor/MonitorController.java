package com.jh.mng.controller.monitor;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jh.mng.common.Config;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.ChnlInfo;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.GameInfo;
import com.jh.mng.pojo.Income;
import com.jh.mng.pojo.Income.GroupCondition;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.RequestUtil;
import com.jh.mng.util.page.Page;
import com.jh.mng.util.page.PagedModelList;

/**
 * 业务监控
 * @author admin
 *
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController extends AbstractMultiActionController {
	
	/**
	 * 最后计费首页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/lastfee.do")
	public ModelAndView lastfee(HttpServletRequest request, HttpServletResponse response) {
		List<GroupCondition> groupList = Arrays.asList(GroupCondition.values());
		request.setAttribute("groupList", groupList);
		
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		request.setAttribute("gameList", gameinfoList);
		
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		request.setAttribute("chnlList", chnlInfoList);
		
		String date = DateTool.getCurrentDate("yyyyMMdd");
		request.setAttribute("_startDay", date);
		request.setAttribute("_endDay", date);
		request.setAttribute("colspanpage", 3);
		
		return new ModelAndView("/monitor/lastFeeList.vm");
	}
	
	/**
	 * 查询最后一次计费时间
	 * @param request
	 * @param response
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@RequestMapping("/queryLastFee.do")
	public ModelAndView queryLastFee(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		request.setAttribute("gameList", gameinfoList);
		
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		request.setAttribute("chnlList", chnlInfoList);
		
		Long gameInfoId = RequestUtil.GetParamLong(request, "gameInfoId", null);
		
		request.setAttribute("_gameInfoId", gameInfoId);
		
		if (gameInfoId != null) {
			List<ConsumeInfo> list = orderService.getConsumeListByGameId(gameInfoId);
			request.setAttribute("consumeList", list);
		}
		
		Long consumeId = RequestUtil.GetParamLong(request, "consumeId", null);
		request.setAttribute("_consumeId", consumeId);
		
		Long chnlInfoId = RequestUtil.GetParamLong(request, "chnlInfoId", null);
		request.setAttribute("_chnlInfoId", chnlInfoId);
		
		String providerId = RequestUtil.GetParamString(request, "providerId", null);
		request.setAttribute("_providerId", providerId);
		
		String startDay = RequestUtil.GetParamString(request, "startDay", null);
		String endDay = RequestUtil.GetParamString(request, "endDay", null);
		
		request.setAttribute("_startDay", startDay);
		request.setAttribute("_endDay", endDay);
		
		String[] groupConditions = request.getParameterValues("groupCondition");
		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
		
		
		Income income = new Income();
		income.setGameId(gameInfoId);
		income.setConsumeId(consumeId);
		income.setChnlId(chnlInfoId);
		income.setProvinceName(providerId);
		income.setStartDate(startDay);
		income.setEnDate(endDay);
		income.setGroupConditions(groupConditions);
		
		List<String> orderMonthList = new ArrayList<String>();
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		int num = DateTool.compareDate(startDay, endDay, "yyyyMMdd");
		
		Calendar c = Calendar.getInstance();
		
		for (int i = 0; i <= num; i++) {
			c.setTime(df.parse(startDay));
			c.add(Calendar.MONTH, i);
			orderMonthList.add(DateTool.DateToString(c.getTime(), "yyMM"));
		}
		
		income.setOrderMonthList(orderMonthList);
		
		List<GroupCondition> groupList = Arrays.asList(GroupCondition.values());
		
		int colspan = 0;
		
		for (int i = 0; i < groupList.size(); i++) {
			groupList.get(i).setIsChecked(Income.COLUMN_VALUE_GROUPISCHECKED_NO);
			for (int j = 0; j < groupConditions.length; j++) {
				if (groupConditions[j].equals(groupList.get(i).getCode())) {
					colspan++;
					groupList.get(i).setIsChecked(Income.COLUMN_VALUE_GROUPISCHECKED_YES);
					break;
				}
			}
		}
		request.setAttribute("groupList", groupList);
		request.setAttribute("colspanpage", colspan + 3);
		
		List<String> groupDescList = new LinkedList<String>();
		List<String> groupCodeList = new LinkedList<String>();
		List<String> nameCodeList = new LinkedList<String>();
		
		for (int i = 0; i < groupConditions.length; i++) {
			for (int j = 0; j < groupList.size(); j++) {
				if (groupList.get(j).getCode().equals(groupConditions[i])) {
					groupDescList.add(groupList.get(j).getDesc());
					groupCodeList.add(groupList.get(j).getCode());
					nameCodeList.add(groupList.get(j).getNameCode());
					break;
				}
			}
		}
		
		request.setAttribute("groupConditionList", groupDescList);
		
		Page<Income> page = new Page<Income>();
		page.setPageNo(pageId);
		page.setPageSize(Config.get().getInt("pageSize", 200));
		
		List<Income> incomeList = orderService.queryLastFeeByConditions(income, page);
		
		if (incomeList != null && incomeList.size() > 0) {
			
			List<ConsumeInfo> conList = orderService.queryAllConsume();
			Income os = null;
			List<Object> valueList = null;
//			NumberFormat percentFormat = NumberFormat.getPercentInstance(); 
			
			for (int i = 0; i < incomeList.size(); i++) {
				valueList = new LinkedList<Object>();
				os = incomeList.get(i);
				os.setChnList(chnlInfoList);
				os.setGameInfoList(gameinfoList);
				os.setConsumeList(conList);
				for (int j = 0; j < groupDescList.size(); j++) {
					BeanUtils.getProperty(os, groupCodeList.get(j));
					valueList.add(BeanUtils.getProperty(os, nameCodeList.get(j)));
				}
				os.setValueList(valueList);
			}
		}
		
		PagedModelList<Income> pagedModelList = new PagedModelList<Income>(pageId, Config.get().getInt("pageSize", 200), page.getTotalRecord());
		
		request.setAttribute("incomeList", incomeList);
		request.setAttribute("pagedModelList", pagedModelList);
		
		return new ModelAndView("/monitor/lastFeeList.vm");
	}

}
