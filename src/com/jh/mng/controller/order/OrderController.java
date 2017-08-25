package com.jh.mng.controller.order;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.jh.mng.common.Config;
import com.jh.mng.controller.AbstractMultiActionController;
import com.jh.mng.pojo.Admin;
import com.jh.mng.pojo.ChnlIncomPercent;
import com.jh.mng.pojo.ChnlInfo;
import com.jh.mng.pojo.ChnlResource;
import com.jh.mng.pojo.ConsumeInfo;
import com.jh.mng.pojo.GameInfo;
import com.jh.mng.pojo.Income;
import com.jh.mng.pojo.StatusQuery;
import com.jh.mng.pojo.Income.GroupCondition;
import com.jh.mng.pojo.Income.GroupCondition_Rate;
import com.jh.mng.util.DateTool;
import com.jh.mng.util.RequestUtil;
import com.jh.mng.util.page.Page;
import com.jh.mng.util.page.PagedModelList;


/**
 * 订单管理
 * @author admin
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController extends AbstractMultiActionController {
	
	private static final Logger logger = Logger.getLogger(OrderController.class);
	
	/**
	 * 渠道收入首页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/chnlincome.do")
	public ModelAndView chnlincome(HttpServletRequest request, HttpServletResponse response) {
		Admin admin = this.getLoginAdminFromSession(request);
		
		List<GroupCondition> groupList = Arrays.asList(GroupCondition.values());
		request.setAttribute("groupList", groupList);
		
		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) {
			ChnlInfo chnlInfo = orderService.getChnlInfoById(admin.getCpId());
			List<ChnlInfo> chnlInfoList = new ArrayList<ChnlInfo>();
			chnlInfoList.add(chnlInfo);
			request.setAttribute("chnlList", chnlInfoList);
			request.setAttribute("_chnlInfoId", chnlInfo.getId());
			request.setAttribute("_isCp", "1");
			
			List<GameInfo> gameinfoList = orderService.getGameInfoByChnlId(chnlInfo.getId());
			request.setAttribute("gameList", gameinfoList);
			
		} else {
			List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
			request.setAttribute("chnlList", chnlInfoList);
			
			List<GameInfo> gameinfoList = orderService.queryAllGame();
			request.setAttribute("gameList", gameinfoList);
		}
		
		String date = DateTool.getCurrentDate("yyyyMMdd");
//		String month = DateTool.getCurrentDate("yyyyMM");
		request.setAttribute("_startDay", date);
		request.setAttribute("_endDay", date);
//		request.setAttribute("_month", month);
		
		request.setAttribute("_decFlag", Income.COLUMN_VALUE_DEC_FLAG_NO);
		
		return new ModelAndView("/order/chnlincomeList.vm");
	}
	
	/**
	 * 导出渠道收入数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/exportData.do")
	public ModelAndView exportData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		
		Long gameInfoId = RequestUtil.GetParamLong(request, "gameInfoId", null);
		Long consumeId = RequestUtil.GetParamLong(request, "consumeId", null);
		Long chnlInfoId = RequestUtil.GetParamLong(request, "chnlInfoId", null);
		String providerId = RequestUtil.GetParamString(request, "providerId", null);
		String startDay = RequestUtil.GetParamString(request, "startDay", null);
		String endDay = RequestUtil.GetParamString(request, "endDay", null);
//		String month = RequestUtil.GetParamString(request, "month", null);
		
		String[] groupConditions = request.getParameterValues("groupCondition");
		Integer decFlag = RequestUtil.GetParamInteger(request, "decFlag", null);
		
		Income income = new Income();
		income.setGameId(gameInfoId);
		income.setConsumeId(consumeId);
		income.setChnlId(chnlInfoId);
		income.setProvinceName(providerId);
		income.setStartDate(startDay);
		income.setEnDate(endDay);
		income.setGroupConditions(groupConditions);
		income.setDec_flag(decFlag);
		income.setMonth(startDay.substring(0, 6));
		
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
		
		List<Income> incomeList = orderService.queryIncomeByConditions(income);
		
//		
//		int sumTimes = 0;
//		double sumFee = 0;
		
		if (incomeList != null && incomeList.size() > 0) {
			
			List<ConsumeInfo> conList = orderService.queryAllConsume();
			Income os = null;
			List<Object> valueList = null;
//			NumberFormat percentFormat = NumberFormat.getPercentInstance(); 
			
			for (int i = 0; i < incomeList.size(); i++) {
				valueList = new LinkedList<Object>();
				os = incomeList.get(i);
//				sumTimes += os.getSuccessTimes();
//				sumFee += os.getIncome();
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
		HttpSession session = request.getSession();
		session.setAttribute("state", null);
		// 生成提示信息，
		response.setContentType("application/vnd.ms-excel");
		String codedFileName = null;
		OutputStream fOut = null;
		try {
			// 进行转码，使其支持中文文件名
			String filename = DateTool.DateToString(new Date(), "yyyy-MM-dd");
			codedFileName = java.net.URLEncoder.encode("渠道收入查询" + filename,
					"UTF-8");
			response.setHeader("content-disposition", "attachment;filename="
					+ codedFileName + ".xls");
			// 产生工作簿对象
			HSSFWorkbook workbook = new HSSFWorkbook();
			// 产生工作表对象
			HSSFSheet sheet = workbook.createSheet();
			// 订单统计结果总计
			int length = groupConditions.length + 2;
			
			for (int i = 0; i < length; i++) {
				sheet.setColumnWidth(i, 6000);
			}

			HSSFRow rowOne = sheet.createRow((int) 0);
			HSSFCell cellOne[] = new HSSFCell[length];
			
			for (int i = 0; i < length; i++) {
				cellOne[i] = rowOne.createCell((int) i);
			}

			for (int i = 0; i < cellOne.length - 2; i++) {
				cellOne[i].setCellValue(groupDescList.get(i));
			}
			cellOne[cellOne.length - 2].setCellValue("成功次数");
			cellOne[cellOne.length - 1].setCellValue("收入");
			
			if (incomeList != null && incomeList.size() > 0) {
				Income tmp = null;
				for (int i = 0; i < incomeList.size(); i++) {
					tmp = incomeList.get(i);
					HSSFRow row = sheet.createRow((int) i + 1);// 创建一行
					HSSFCell cell[] = new HSSFCell[length];
					for (int j = 0; j < cell.length; j++) {
						cell[j] = row.createCell((int) j);
					}
					
					for (int h = 0; h < cellOne.length - 2; h++) {
						if (tmp.getValueList().get(h) != null) {
							cell[h].setCellValue(String.valueOf(tmp.getValueList().get(h)));
						} else {
							cell[h].setCellValue("");
						}
					}
					
					if (tmp.getSuccessTimes() != null) {
						cell[cellOne.length - 2].setCellValue(String.valueOf(tmp.getSuccessTimes()));
					} else {
						cell[cellOne.length - 2].setCellValue("0");
					}
					
					if (tmp.getIncome() != null) {
						cell[cellOne.length - 1].setCellValue(String.valueOf(tmp.getIncome()));
					} else {
						cell[cellOne.length - 1].setCellValue("0");
					}
				}
			}
			fOut = response.getOutputStream();
			workbook.write(fOut);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exportData error : " + e.getMessage());
		} finally {
			try {
				fOut.flush();
				fOut.close();
			} catch (IOException e) {
			}
			session.setAttribute("state", "open");
		}
		return null;
	}
	
	@RequestMapping("/getConsumeInfo.do")
	public ModelAndView getConsumeInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Admin admin = this.getLoginAdminFromSession(request);
		String gameInfoId = request.getParameter("gameId");
		
		List<ConsumeInfo> resultList = new ArrayList<ConsumeInfo>();
		
		List<ConsumeInfo> list = orderService.getConsumeListByGameId(Long.parseLong(gameInfoId));
		
		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) {
			List<ChnlResource> resList = orderService.queryResourceByChnlId(admin.getCpId());
			
			ConsumeInfo consumeInfo = null;
			ChnlResource chnlResource = null;
			
			for (int i = 0; i < resList.size(); i++) {
				chnlResource = resList.get(i);
				
				for (int j = 0; j < list.size(); j++) {
					consumeInfo = list.get(j);
					
					if (consumeInfo.getId().intValue() == chnlResource.getConsumeId().intValue()) {
						resultList.add(consumeInfo);
						break;
					}
				}
			}
		} else {
			resultList = list;
		}
		
		if (resultList != null && resultList.size() > 0) {
			PrintWriter pw = response.getWriter();
			
			String jsonString = JSONObject.toJSONString(resultList);
			pw.write(jsonString);
		}
		return null;
	}
	
	@RequestMapping("/queryIncome.do")
	public ModelAndView queryIncome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Admin admin = this.getLoginAdminFromSession(request);
		
		List<ChnlInfo> chnlInfoList = new ArrayList<ChnlInfo>();
		List<GameInfo> gameinfoList = new ArrayList<GameInfo>();
		
		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) {
			ChnlInfo chnlInfo = orderService.getChnlInfoById(admin.getCpId());
			chnlInfoList.add(chnlInfo);
			request.setAttribute("chnlList", chnlInfoList);
			request.setAttribute("_chnlInfoId", chnlInfo.getId());
			request.setAttribute("_isCp", "1");
			
			gameinfoList = orderService.getGameInfoByChnlId(chnlInfo.getId());
			request.setAttribute("gameList", gameinfoList);
		} else {
			chnlInfoList = orderService.queryAllChnl();
			request.setAttribute("chnlList", chnlInfoList);
			gameinfoList = orderService.queryAllGame();
			request.setAttribute("gameList", gameinfoList);
		}
//		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
//		request.setAttribute("chnlList", chnlInfoList);
		
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
//		String month = RequestUtil.GetParamString(request, "month", null);
		
		request.setAttribute("_startDay", startDay);
		request.setAttribute("_endDay", endDay);
//		request.setAttribute("_month", month);
		
		String[] groupConditions = request.getParameterValues("groupCondition");
		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
		
		Integer decFlag = RequestUtil.GetParamInteger(request, "decFlag", null);
		
		request.setAttribute("_decFlag", decFlag);
		
		Income income = new Income();
		income.setGameId(gameInfoId);
		income.setConsumeId(consumeId);
		income.setChnlId(chnlInfoId);
		income.setProvinceName(providerId);
		income.setStartDate(startDay);
		income.setEnDate(endDay);
//		income.setMonth(month);
		income.setGroupConditions(groupConditions);
		income.setDec_flag(decFlag);
		
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
		request.setAttribute("colspanpage", colspan + 1);
		
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
		
		List<Income> incomeList = orderService.queryIncomeByConditions(income, page);
		
		int sumTimes = 0;
		double sumFee = 0;
		
		if (incomeList != null && incomeList.size() > 0) {
			
			List<ConsumeInfo> conList = orderService.queryAllConsume();
			Income os = null;
			List<Object> valueList = null;
//			NumberFormat percentFormat = NumberFormat.getPercentInstance(); 
			
			for (int i = 0; i < incomeList.size(); i++) {
				valueList = new LinkedList<Object>();
				os = incomeList.get(i);
				sumTimes += os.getSuccessTimes();
				sumFee += os.getIncome();
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
		
		return new ModelAndView("/order/chnlincomeList.vm");
	}
	
	/**
	 * 收入查询（含超量）
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/income.do")
	public ModelAndView income(HttpServletRequest request, HttpServletResponse response) {
		List<GroupCondition> groupList = Arrays.asList(GroupCondition.values());
		request.setAttribute("groupList", groupList);
		
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		request.setAttribute("gameList", gameinfoList);
		
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		request.setAttribute("chnlList", chnlInfoList);
		
		String date = DateTool.getCurrentDate("yyyyMMdd");
//		String month = DateTool.getCurrentDate("yyyyMM");
		request.setAttribute("_startDay", date);
		request.setAttribute("_endDay", date);
//		request.setAttribute("_month", month);
		
		return new ModelAndView("/order/chnlincomeList.vm");
	}
	
	/**
	 * 转化率分析
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/conversionrate.do")
	public ModelAndView conversionrate(HttpServletRequest request, HttpServletResponse response) {
		List<GroupCondition_Rate> groupList = Arrays.asList(GroupCondition_Rate.values());
		request.setAttribute("groupList", groupList);
		
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		request.setAttribute("gameList", gameinfoList);
		
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		request.setAttribute("chnlList", chnlInfoList);
		
		String date = DateTool.getCurrentDate("yyyyMMdd");
		request.setAttribute("_startDay", date);
		request.setAttribute("_endDay", date);
		request.setAttribute("colspanpage", 5);
		
		return new ModelAndView("/order/conversionrate.vm");
		
	}
	
	/**
	 * 转化率分析查询
	 * @param request
	 * @param response
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@RequestMapping("/queryConversionRate.do")
	public ModelAndView queryConversionRate(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
		
		String recvHour = RequestUtil.GetParamString(request, "hour", null);
		request.setAttribute("_hour", recvHour);
		
		
		Income income = new Income();
		income.setGameId(gameInfoId);
		income.setConsumeId(consumeId);
		income.setChnlId(chnlInfoId);
		income.setProvinceName(providerId);
		income.setStartDate(startDay);
		income.setEnDate(endDay);
		income.setRecvHour(recvHour);
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
		
		List<GroupCondition_Rate> groupList = Arrays.asList(GroupCondition_Rate.values());
		
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
		request.setAttribute("colspanpage", colspan + 5);
		
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
		
		List<Income> incomeList = orderService.queryConversionRate(income, page);
		
		if (incomeList != null && incomeList.size() > 0) {
			
			List<ConsumeInfo> conList = orderService.queryAllConsume();
			Income os = null;
			List<Object> valueList = null;
			NumberFormat percentFormat = NumberFormat.getPercentInstance(); 
			
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
				os.setRate(percentFormat.format(new Double(os.getSuccTimes()) / new Double(os.getReqTimes())));
			}
		}
		
		PagedModelList<Income> pagedModelList = new PagedModelList<Income>(pageId, Config.get().getInt("pageSize", 200), page.getTotalRecord());
		
		request.setAttribute("incomeList", incomeList);
		request.setAttribute("pagedModelList", pagedModelList);
		
		return new ModelAndView("/order/conversionrate.vm");
	}
//	
//	/**
//	 * 查询订单
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/queryOrder.do")
//	public ModelAndView queryOrder(HttpServletRequest request, HttpServletResponse response) {
//		
//		Admin admin = this.getLoginAdminFromSession(request);
//		
//		this.orderRequestProp(request);
//		Long aisleInfoId = RequestUtil.GetParamLong(request, "aisleInfoId", null);
//		Long spInfoId = RequestUtil.GetParamLong(request, "spInfoId", null);
//		Long providerId = RequestUtil.GetParamLong(request, "providerId", null);
//		Long provinceId = RequestUtil.GetParamLong(request, "provinceId", null);
//		Long orderMonth = RequestUtil.GetParamLong(request, "orderMonth", null);
//		Long orderDay = RequestUtil.GetParamLong(request, "orderDay", null);
//		Integer orderStatus = RequestUtil.GetParamInteger(request, "orderStatus", null);
//		Long orderId = RequestUtil.GetParamLong(request, "orderId", null);
//		String phone = RequestUtil.GetParamString(request, "phone", null);
//		String imsi = RequestUtil.GetParamString(request, "imsi", null);
//		String imei = RequestUtil.GetParamString(request, "imei", null);
//		String packageName = RequestUtil.GetParamString(request, "packageName", null);
//		String appName = RequestUtil.GetParamString(request, "appName", null);
//		String sdkSource = RequestUtil.GetParamString(request, "sdkSource", null);
//		String sdkVersion = RequestUtil.GetParamString(request, "sdkVersion", null);
//		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
//		
//		request.setAttribute("_aisleInfoId", aisleInfoId);
//		request.setAttribute("_spInfoId", spInfoId);
//		request.setAttribute("_providerId", providerId);
//		request.setAttribute("_provinceId", provinceId);
//		request.setAttribute("_orderMonth", orderMonth);
//		request.setAttribute("_orderDay", orderDay);
//		request.setAttribute("_orderStatus", orderStatus);
//		request.setAttribute("_orderId", orderId);
//		request.setAttribute("_phone", phone);
//		request.setAttribute("_imsi", imsi);
//		request.setAttribute("_imei", imei);
//		request.setAttribute("_packageName", packageName);
//		request.setAttribute("_appName", appName);
//		request.setAttribute("_sdkSource", sdkSource);
//		request.setAttribute("_sdkVersion", sdkVersion);
//		
//		List<PayOrderInfo> orderList = new ArrayList<PayOrderInfo>();
//		
//		PayOrderInfo payOrderInfo = new PayOrderInfo();
//		
//		if (orderMonth == null) {
//			orderMonth = Long.parseLong(String.valueOf(orderDay).substring(0, 6));
//		} 
//		payOrderInfo.setProvinceId(provinceId);
//		payOrderInfo.setOrderMonth(orderMonth);
//		payOrderInfo.setOrderDay(orderDay);
//		payOrderInfo.setOrderStatus(orderStatus);
//		payOrderInfo.setId(orderId);
//		payOrderInfo.setPhone(phone);
//		payOrderInfo.setImsi(imsi);
//		payOrderInfo.setImei(imei);
//		payOrderInfo.setPackageName(packageName);
//		payOrderInfo.setAppName(appName);
//		payOrderInfo.setSdkSource(sdkSource);
//		payOrderInfo.setSdkVersion(sdkVersion);
//		
//		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) { //CP管理员只能查询本cp订单
//			payOrderInfo.setCpId(admin.getCpInfo().getId());
//		}
//		
//		Page<PayOrderInfo> page = new Page<PayOrderInfo>();
//		page.setPageNo(pageId);
//		page.setPageSize(myConfig.getPageSize());
//		
//		Long count = 0L;
//		
//		//通道ID，sp ID,运营商ID其中一个不为空，则需要查询payOrderItemInfo表
//		if (aisleInfoId != null || spInfoId != null ||  providerId != null) {
//			PayOrderItemInfo payOrderItemInfo = new PayOrderItemInfo();
//			payOrderItemInfo.setAisleId(aisleInfoId);
//			payOrderItemInfo.setSpId(spInfoId);
//			payOrderItemInfo.setProviderId(providerId);
//			
//			orderList = clientService.queryOrderListByOrderItemConditions(payOrderInfo, payOrderItemInfo, page);
//			count = clientService.countOrderListByOrderItemConditions(payOrderInfo, payOrderItemInfo);
//			
//		} else {
//			orderList = clientService.queryOrderListByOrderConditions(payOrderInfo, page);
//			count = clientService.countOrderListByOrderConditions(payOrderInfo);
//		}
//		
//		if (count == null) {
//			count = 0L;
//		}
//		
//		PagedModelList<PayOrderInfo> pagedModelList = new PagedModelList<PayOrderInfo>(pageId, myConfig.getPageSize(), count.intValue());
//		
//		request.setAttribute("orderList", orderList);
//		request.setAttribute("pagedModelList", pagedModelList);
//		return new ModelAndView("/order/orderList.vm");
//	}
//	
//	/**
//	 * 订单统计首页
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/orderStatistics.do")
//	public ModelAndView orderStatistics(HttpServletRequest request, HttpServletResponse response) {
//		this.orderRequestProp(request);
//		List<OrderStatisticsGroupCondition> groupList = Arrays.asList(OrderStatisticsGroupCondition.values());
//		request.setAttribute("groupList", groupList);
//		return new ModelAndView("/order/orderStatistics.vm");
//	}
//	
//	/**
//	 * 订单统计查询
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws ParseException 
//	 * @throws NoSuchMethodException 
//	 * @throws InvocationTargetException 
//	 * @throws IllegalAccessException 
//	 */
//	@RequestMapping("/queryOrderStatistics.do")
//	public ModelAndView queryOrderStatistics(HttpServletRequest request, HttpServletResponse response) throws ParseException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//		List<ProviderInfo> providerInfoList = aisleService.queryAllProviderInfo();
//		List<SpInfo> spInfoList = spService.queryAllSpInfo();
//		List<ProvinceInfo> provinceInfoList = aisleService.queryAllProvinceInfo();
//		List<AisleInfo> aisleList = aisleService.queryAllAisleInfo();
//		List<OrderStatus> statusList = Arrays.asList(OrderStatus.values());
//		
//		request.setAttribute("providerInfoList", providerInfoList);
//		request.setAttribute("spInfoList", spInfoList);
//		request.setAttribute("provinceInfoList", provinceInfoList);
//		request.setAttribute("aisleList", aisleList);
//		request.setAttribute("statusList", statusList);
//		 
//		
//		List<CpInfo> cpInfoList = new ArrayList<CpInfo>();
//		Admin admin = this.getLoginAdminFromSession(request);
//		
//		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) {
//			cpInfoList.add(admin.getCpInfo());
//		} else {
//			cpInfoList = cpService.queryAllCpInfo();
//		}
//		
//		request.setAttribute("cpInfoList", cpInfoList);
//		
//		Long aisleInfoId = RequestUtil.GetParamLong(request, "aisleInfoId", null);
//		Long spInfoId = RequestUtil.GetParamLong(request, "spInfoId", null);
//		Long providerId = RequestUtil.GetParamLong(request, "providerId", null);
//		Long provinceId = RequestUtil.GetParamLong(request, "provinceId", null);
//		Long cpId = RequestUtil.GetParamLong(request, "cpId", null);
//		String orderStartDay = RequestUtil.GetParamString(request, "startDay", null);
//		String orderEndDay = RequestUtil.GetParamString(request, "endDay", null);
//		String[] groupConditions = request.getParameterValues("groupCondition");
//		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
//		
//		request.setAttribute("_aisleInfoId", aisleInfoId);
//		request.setAttribute("_spInfoId", spInfoId);
//		request.setAttribute("_providerId", providerId);
//		request.setAttribute("_provinceId", provinceId);
//		request.setAttribute("_cpId", cpId);
//		request.setAttribute("_startDay", orderStartDay);
//		request.setAttribute("_endDay", orderEndDay);
//		
//		List<OrderStatisticsGroupCondition> groupList = Arrays.asList(OrderStatisticsGroupCondition.values());
//		
//		for (int i = 0; i < groupList.size(); i++) {
//			groupList.get(i).setIsChecked(OrderStatistics.COLUMN_VALUE_GROUPISCHECKED_NO);
//			for (int j = 0; j < groupConditions.length; j++) {
//				if (groupConditions[j].equals(groupList.get(i).getCode())) {
//					groupList.get(i).setIsChecked(OrderStatistics.COLUMN_VALUE_GROUPISCHECKED_YES);
//					break;
//				}
//			}
//		}
//		request.setAttribute("groupList", groupList);
//		
//		boolean statisticsAisle = false;
//		
//		for (int i = 0; i < groupConditions.length; i++) {
//			if (groupConditions[i].equals("spId") || groupConditions[i].equals("aisleId")) { //分组信息选择了sp或通道信息
//				statisticsAisle = true;
//				break;
//			}
//		}
//		
//		request.setAttribute("statisticsAisle", statisticsAisle);
//		
//		List<String> groupDescList = new LinkedList<String>();
//		List<String> groupCodeList = new LinkedList<String>();
//		List<String> nameCodeList = new LinkedList<String>();
//		
//		for (int i = 0; i < groupConditions.length; i++) {
//			for (int j = 0; j < groupList.size(); j++) {
//				if (groupList.get(j).getCode().equals(groupConditions[i])) {
//					groupDescList.add(groupList.get(j).getDesc());
//					groupCodeList.add(groupList.get(j).getCode());
//					nameCodeList.add(groupList.get(j).getNameCode());
//					break;
//				}
//			}
//		}
//		
//		request.setAttribute("groupConditionList", groupDescList);
//		
//		Page<OrderStatistics> page = new Page<OrderStatistics>();
//		page.setPageNo(pageId);
//		page.setPageSize(myConfig.getPageSize());
//		
//		OrderStatistics orderStatistics = new OrderStatistics();
//		orderStatistics.setAisleId(aisleInfoId);
//		orderStatistics.setSpId(spInfoId);
//		orderStatistics.setProviderId(providerId);
//		orderStatistics.setProvinceId(provinceId);
//		orderStatistics.setCpId(cpId);
//		orderStatistics.setGroupConditions(groupConditions);
//		orderStatistics.setStartDay(orderStartDay);
//		orderStatistics.setEndDay(orderEndDay);
//		
//		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) { //CP管理员只能查询本cp订单
//			orderStatistics.setCpId(admin.getCpInfo().getId());
//		}
//		
//		List<String> orderMonthList = new ArrayList<String>();
//		
//		DateFormat df = new SimpleDateFormat("yyyyMMdd");
//		int num = DateTool.compareDate(orderStartDay, orderEndDay, "yyyyMMdd");
//		
//		Calendar c = Calendar.getInstance();
//		
//		for (int i = 0; i <= num; i++) {
//			c.setTime(df.parse(orderStartDay));
//			c.add(Calendar.MONTH, i);
//			orderMonthList.add(DateTool.DateToString(c.getTime(), "yyyyMM"));
//		}
//		
//		orderStatistics.setOrderMonthList(orderMonthList);
//		
//		List<OrderStatistics> list = new ArrayList<OrderStatistics>();
//		
//		if (statisticsAisle) { //选择了通道、SP纬度
//			list = clientService.queryOrderAisleStatisticsByCons(orderStatistics, page);
//			
//			if (list != null && list.size() > 0) {
//				OrderStatistics os = null;
//				List<Object> valueList = null;
//				NumberFormat percentFormat = NumberFormat.getPercentInstance(); 
//				
//				for (int i = 0; i < list.size(); i++) {
//					valueList = new LinkedList<Object>();
//					os = list.get(i);
//					os.setAisleList(aisleList);
//					os.setCpInfoList(cpInfoList);
//					os.setSpInfoList(spInfoList);
//					os.setProviderInfoList(providerInfoList);
//					os.setProvinceInfoList(provinceInfoList);
//					for (int j = 0; j < groupDescList.size(); j++) {
//						BeanUtils.getProperty(os, groupCodeList.get(j));
//						valueList.add(BeanUtils.getProperty(os, nameCodeList.get(j)));
//					}
//					os.setValueList(valueList);
//					if (os.getIssuedPrice() != null && os.getIssuedPrice() > 0 && os.getMoPrice() != null) {
//						os.setMoRate(percentFormat.format(new Double(os.getMoPrice()) / new Double(os.getIssuedPrice())));
//					} else {
//						os.setMoRate("0%");
//					}
//				}
//			}
//			
//		} else { 
//			list = clientService.queryOrderStatisticsByConditions(orderStatistics, page);
//			
//			if (list != null && list.size() > 0) {
//				OrderStatistics os = null;
//				List<Object> valueList = null;
//				NumberFormat percentFormat = NumberFormat.getPercentInstance(); 
//				
//				for (int i = 0; i < list.size(); i++) {
//					valueList = new LinkedList<Object>();
//					os = list.get(i);
//					os.setAisleList(aisleList);
//					os.setCpInfoList(cpInfoList);
//					os.setSpInfoList(spInfoList);
//					os.setProviderInfoList(providerInfoList);
//					os.setProvinceInfoList(provinceInfoList);
//					for (int j = 0; j < groupDescList.size(); j++) {
//						BeanUtils.getProperty(os, groupCodeList.get(j));
//						valueList.add(BeanUtils.getProperty(os, nameCodeList.get(j)));
//					}
//					os.setValueList(valueList);
//					os.setAisleSuccessRate(percentFormat.format(new Double(os.getSuccessPrice()) / new Double(os.getRequestPrice())));
//					if (os.getSuccessPrice() > 0) {
//						os.setMoRate(percentFormat.format(new Double(os.getMoPrice()) / new Double(os.getSuccessPrice())));
//					} else {
//						os.setMoRate("0%");
//					}
//				}
//			}
//		}
//		
//		PagedModelList<OrderStatistics> pagedModelList = new PagedModelList<OrderStatistics>(pageId, myConfig.getPageSize(), page.getTotalRecord());
//		
//		request.setAttribute("orderStatisticsList", list);
//		request.setAttribute("pagedModelList", pagedModelList);
//		
//		return new ModelAndView("/order/orderStatistics.vm");
//	}
//	
//	/**
//	 * 支付成功统计
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/orderReport.do")
//	public ModelAndView orderReport(HttpServletRequest request, HttpServletResponse response) {
//		List<AppInfo> appList = appService.queryAllAppInfo();
//		
//		List<ProviderInfo> providerInfoList = aisleService.queryAllProviderInfo();
//		List<SpInfo> spInfoList = spService.queryAllSpInfo();
//		List<ProvinceInfo> provinceInfoList = aisleService.queryAllProvinceInfo();
//		List<AisleInfo> aisleList = aisleService.queryAllAisleInfo();
//		List<OrderStatus> statusList = Arrays.asList(OrderStatus.values());
//		
//		request.setAttribute("providerInfoList", providerInfoList);
//		request.setAttribute("spInfoList", spInfoList);
//		request.setAttribute("provinceInfoList", provinceInfoList);
//		request.setAttribute("aisleList", aisleList);
//		request.setAttribute("statusList", statusList);
//		
//		List<CpInfo> cpInfoList = new ArrayList<CpInfo>();
//		Admin admin = this.getLoginAdminFromSession(request);
//		
//		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) {
//			cpInfoList.add(admin.getCpInfo());
//		} else {
//			cpInfoList = cpService.queryAllCpInfo();
//		}
//		
//		request.setAttribute("cpInfoList", cpInfoList);
//		
//		request.setAttribute("appList", appList);
//		return new ModelAndView("/order/orderReport.vm");
//	}
//	
//	/**
//	 * 支付成功统计查询
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	@RequestMapping("/reportQuery.do")
//	public ModelAndView reportQuery(HttpServletRequest request, HttpServletResponse response) {
//		this.orderRequestProp(request);
//		List<AppInfo> appList = appService.queryAllAppInfo();
//		request.setAttribute("appList", appList);
//		
//		
//		Long aisleId = RequestUtil.GetParamLong(request, "aisleInfoId", null);
//		Long appId = RequestUtil.GetParamLong(request, "appInfoId", null);
//		Long cpId = RequestUtil.GetParamLong(request, "cpId", null);
//		String orderMonth = RequestUtil.GetParamString(request, "orderMonth", null);
//		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
//		
//		request.setAttribute("_aisleInfoId", aisleId);
//		request.setAttribute("_appInfoId", appId);
//		request.setAttribute("_cpId", cpId);
//		request.setAttribute("_orderMonth", orderMonth);
//		
//		ThirdReport thirdReport = new ThirdReport();
//		thirdReport.setAisleInfoId(aisleId);
//		thirdReport.setAppId(appId);
//		thirdReport.setCpId(cpId);
//		thirdReport.setReportMonth(Long.parseLong(orderMonth));
//		
//		Admin admin = this.getLoginAdminFromSession(request);
//		
//		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) { //CP管理员只能查询本cp订单
//			thirdReport.setCpId(admin.getCpInfo().getId());
//		}
//		
//		Page<ThirdReport> page = new Page<ThirdReport>();
//		page.setPageNo(pageId);
//		page.setPageSize(myConfig.getPageSize());
//		
//		List<ThirdReport> list = clientService.queryReportByCons(thirdReport, page);
//		
//		if (list != null && list.size() > 0) {
//			List<CpInfo> cpList = (List<CpInfo>) request.getAttribute("cpInfoList");
//			for (ThirdReport tReport : list) {
//				for (int i = 0; i < cpList.size(); i++) {
//					if (tReport.getCpId().intValue() == cpList.get(i).getId().intValue()) {
//						tReport.setCpName(cpList.get(i).getName());
//						break;
//					}
//				}
//				
//				for (int i = 0; i < appList.size(); i++) {
//					if (tReport.getAppId().intValue() == appList.get(i).getId().intValue()) {
//						tReport.setAppName(appList.get(i).getAppName());
//						break;
//					}
//				}
//			}
//			
//			PagedModelList<ThirdReport> pagedModelList = new PagedModelList<ThirdReport>(pageId, myConfig.getPageSize(), page.getTotalPage());
//			
//			request.setAttribute("orderReportList", list);
//			request.setAttribute("pagedModelList", pagedModelList);
//		}
//		
//		return new ModelAndView("/order/orderReport.vm");
//	}
//	
//	
//	/**
//	 * 通道转化率
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/aisleConversionRate.do")
//	public ModelAndView aisleConversionRate(HttpServletRequest request, HttpServletResponse response) {
//		List<AisleInfo> aisleList = aisleService.queryAllAisleInfo();
//		request.setAttribute("aisleList", aisleList);
//		return new ModelAndView("/order/aisleConversionRate.vm");
//	}
//	
//	/**
//	 * 通道转化率查询
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping("/conversionRateQuery.do")
//	public ModelAndView conversionRateQuery(HttpServletRequest request, HttpServletResponse response) {
//		List<AisleInfo> aisleList = aisleService.queryAllAisleInfo();
//		request.setAttribute("aisleList", aisleList);
//		
//		Long aisleId = RequestUtil.GetParamLong(request, "aisleInfoId", null);
//		Long orderMonth = RequestUtil.GetParamLong(request, "orderMonth", null);
//		Long orderDay = RequestUtil.GetParamLong(request, "orderDay", null);
//		Long orderHour = RequestUtil.GetParamLong(request, "orderHour", null);
//		Integer conversionRateType = RequestUtil.GetParamInteger(request, "conversionRateType", 1);
//		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
//		
//		request.setAttribute("_aisleInfoId", aisleId);
//		request.setAttribute("_orderMonth", orderMonth);
//		request.setAttribute("_orderDay", orderDay);
//		request.setAttribute("_orderHour", orderHour);
//		request.setAttribute("_conversionRateType", conversionRateType);
//		
//		AisleConversionRate aisleConversionRate = new AisleConversionRate();
//		aisleConversionRate.setAisleId(aisleId);
//		aisleConversionRate.setConversionRateType(conversionRateType);
//		aisleConversionRate.setOrderMonth(orderMonth);
//		aisleConversionRate.setOrderDay(orderDay);
//		aisleConversionRate.setOrderHour(orderHour);
//		
//		
//		Admin admin = this.getLoginAdminFromSession(request);
//		
//		if (admin.getType().intValue() == Admin.COLUMN_VALUE_TYPE_CP) { //CP管理员只能查询本cp订单
//			aisleConversionRate.setCpId(admin.getCpInfo().getId());
//		}
//		
//		Page<AisleConversionRate> page = new Page<AisleConversionRate>();
//		page.setPageNo(pageId);
//		page.setPageSize(myConfig.getPageSize());
//		
//		List<AisleConversionRate> list = clientService.queryConversionRateByCons(aisleConversionRate, page);
//		
//		if (list != null && list.size() > 0) {
//			NumberFormat percentFormat = NumberFormat.getPercentInstance(); 
//			
//			int size = list.size();
//			AisleConversionRate acr = null;
//			for (int i = 0; i < size; i++) {
//				acr = list.get(i);
//				acr.setConversionRate(percentFormat.format(new Double(acr.getRealSuccessTimes()) / new Double(acr.getMoTimes())));
//				for (int j = 0; j < aisleList.size(); j++) {
//					if (acr.getAisleId().intValue() == aisleList.get(j).getId().intValue()) {
//						acr.setAisleName(aisleList.get(j).getAisleName());
//						break;
//					}
//				}
//			}
//			
//			PagedModelList<AisleConversionRate> pagedModelList = new PagedModelList<AisleConversionRate>(pageId, myConfig.getPageSize(), page.getTotalPage());
//			
//			request.setAttribute("conversionRateList", list);
//			request.setAttribute("pagedModelList", pagedModelList);
//		}
//		
//		return new ModelAndView("/order/aisleConversionRate.vm");
//	}
	
	/**
	 * 状态查询首页
	 */
	@RequestMapping("/statusQueryIndex.do")
	public ModelAndView statusQueryIndex(HttpServletRequest request, HttpServletResponse response) {
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		request.setAttribute("gameList", gameinfoList);
		
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		request.setAttribute("chnlList", chnlInfoList);
		
		String date = DateTool.getCurrentDate("yyyyMMdd");
		request.setAttribute("_startDay", date);
		request.setAttribute("_endDay", date);
		
		return new ModelAndView("/order/statusQueryList.vm");
	}
	
	/**
	 * 状态查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/statusQuery.do")
	public ModelAndView statusQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		request.setAttribute("gameList", gameinfoList);
		
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		request.setAttribute("chnlList", chnlInfoList);
		
		Long gameInfoId = RequestUtil.GetParamLong(request, "gameInfoId", null);
		
		request.setAttribute("_gameInfoId", gameInfoId);
		
		Long chnlInfoId = RequestUtil.GetParamLong(request, "chnlInfoId", null);
		request.setAttribute("_chnlInfoId", chnlInfoId);
		
		String startDay = RequestUtil.GetParamString(request, "startDay", null);
		String endDay = RequestUtil.GetParamString(request, "endDay", null);
		String providerId = RequestUtil.GetParamString(request, "providerId", null);
		Integer isCount = RequestUtil.GetParamInteger(request, "isCount");
		Integer status = RequestUtil.GetParamInteger(request, "status", null);
		String extBox = RequestUtil.GetParamString(request, "extBox", null);
		
		if ("extBox".equals(extBox)) {
			request.setAttribute("extChecked", "1");
		}
		
		
		request.setAttribute("_startDay", startDay);
		request.setAttribute("_endDay", endDay);
		request.setAttribute("_providerId", providerId);
		request.setAttribute("_isCount", isCount);
		request.setAttribute("_status", status);
		
		String phonenos = RequestUtil.GetParamString(request, "phonenos", null);
		request.setAttribute("_phonenos", phonenos);
		
		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
		
		StatusQuery statusQuery = new StatusQuery();
		statusQuery.setChnlId(chnlInfoId);
		statusQuery.setGameId(gameInfoId);
		statusQuery.setStartDate(startDay);
		statusQuery.setEnDate(endDay);
		statusQuery.setPhoneno(phonenos);
		statusQuery.setProviderId(providerId);
		statusQuery.setIsCount(isCount);
		statusQuery.setStatusInteger(status);
		
		if ("extBox".equals(extBox)) {
			statusQuery.setExtBox(true);
		} else {
			statusQuery.setExtBox(false);
		}
		
		List<String> orderMonthList = new ArrayList<String>();
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		int num = DateTool.compareDate(startDay, endDay, "yyyyMMdd");
		
		Calendar c = Calendar.getInstance();
		
		for (int i = 0; i <= num; i++) {
			c.setTime(df.parse(startDay));
			c.add(Calendar.MONTH, i);
			orderMonthList.add(DateTool.DateToString(c.getTime(), "yyMM"));
		}
		
		statusQuery.setOrderMonthList(orderMonthList);
		
		Page<StatusQuery> page = new Page<StatusQuery>();
		page.setPageNo(pageId);
		page.setPageSize(Config.get().getInt("pageSize", 200));
		
		List<StatusQuery> list = orderService.queryOrderStatus(statusQuery, page);
		
		PagedModelList<StatusQuery> pagedModelList = new PagedModelList<StatusQuery>(pageId, Config.get().getInt("pageSize", 200), page.getTotalRecord());
		
		request.setAttribute("statusQueryList", list);
		request.setAttribute("pagedModelList", pagedModelList);
		
		return new ModelAndView("/order/statusQueryList.vm");
	}
	
	@RequestMapping("/exportStatusQuery.do")
	public ModelAndView exportStatusQuery(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Long gameInfoId = RequestUtil.GetParamLong(request, "gameInfoId", null);
		Long chnlInfoId = RequestUtil.GetParamLong(request, "chnlInfoId", null);
		String startDay = RequestUtil.GetParamString(request, "startDay", null);
		String endDay = RequestUtil.GetParamString(request, "endDay", null);
		String providerId = RequestUtil.GetParamString(request, "providerId", null);
		Integer status = RequestUtil.GetParamInteger(request, "status", null);
		String phonenos = RequestUtil.GetParamString(request, "phonenos", null);
		
		StatusQuery statusQuery = new StatusQuery();
		statusQuery.setChnlId(chnlInfoId);
		statusQuery.setGameId(gameInfoId);
		statusQuery.setStartDate(startDay);
		statusQuery.setEnDate(endDay);
		statusQuery.setPhoneno(phonenos);
		statusQuery.setProviderId(providerId);
		statusQuery.setStatusInteger(status);
		statusQuery.setIsCount(2);
		
		List<String> orderMonthList = new ArrayList<String>();
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		int num = DateTool.compareDate(startDay, endDay, "yyyyMMdd");
		
		Calendar c = Calendar.getInstance();
		
		for (int i = 0; i <= num; i++) {
			c.setTime(df.parse(startDay));
			c.add(Calendar.MONTH, i);
			orderMonthList.add(DateTool.DateToString(c.getTime(), "yyMM"));
		}
		
		statusQuery.setOrderMonthList(orderMonthList);
		
		List<StatusQuery> list = orderService.queryOrderStatus(statusQuery);
		
		HttpSession session = request.getSession();
		session.setAttribute("state", null);
		// 生成提示信息，
		response.setContentType("application/vnd.ms-excel");
		String codedFileName = null;
		OutputStream fOut = null;
		try {
			// 进行转码，使其支持中文文件名
			String filename = DateTool.DateToString(new Date(), "yyyy-MM-dd");
			codedFileName = java.net.URLEncoder.encode("状态查询" + filename,
					"UTF-8");
			response.setHeader("content-disposition", "attachment;filename="
					+ codedFileName + ".xls");
			// 产生工作簿对象
			HSSFWorkbook workbook = new HSSFWorkbook();
			// 产生工作表对象
			HSSFSheet sheet = workbook.createSheet();
			// 订单统计结果总计
			int length = 10;
			
			for (int i = 0; i < length; i++) {
				sheet.setColumnWidth(i, 6000);
			}

			HSSFRow rowOne = sheet.createRow((int) 0);
			HSSFCell cellOne[] = new HSSFCell[length];
			
			for (int i = 0; i < length; i++) {
				cellOne[i] = rowOne.createCell((int) i);
			}
			
			cellOne[0].setCellValue("自定义参数");
			cellOne[1].setCellValue("手机号");
			cellOne[2].setCellValue("省份");
			cellOne[3].setCellValue("业务");
			cellOne[4].setCellValue("道具");
			cellOne[5].setCellValue("渠道");
			cellOne[6].setCellValue("计费状态");
			cellOne[7].setCellValue("同步状态");
			cellOne[8].setCellValue("同步时间");
			cellOne[9].setCellValue("hret");
			
			if (list != null && list.size() > 0) {
				StatusQuery tmp = null;
				for (int i = 0; i < list.size(); i++) {
					tmp = list.get(i);
					HSSFRow row = sheet.createRow((int) i + 1);// 创建一行
					HSSFCell cell[] = new HSSFCell[length];
					for (int j = 0; j < cell.length; j++) {
						cell[j] = row.createCell((int) j);
					}
					cell[0].setCellValue(tmp.getCpparam());
					cell[1].setCellValue(tmp.getPhoneno());
					cell[2].setCellValue(tmp.getState());
					cell[3].setCellValue(tmp.getProductName());
					cell[4].setCellValue(tmp.getConsumeName());
					cell[5].setCellValue(tmp.getChnlName());
					cell[6].setCellValue(tmp.getStatus());
					cell[7].setCellValue(tmp.getSyncchnl());
					cell[8].setCellValue(DateTool.DateToString(tmp.getRecvTime(), "yyyy-MM-dd HH:mm:ss"));
					cell[9].setCellValue(tmp.getHret());
				}
			}
			fOut = response.getOutputStream();
			workbook.write(fOut);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exportData error : " + e.getMessage());
		} finally {
			try {
				fOut.flush();
				fOut.close();
			} catch (IOException e) {
			}
			session.setAttribute("state", "open");
		}
		return null;
	}

	
	@RequestMapping("/chnlIncomPercent.do")
	public ModelAndView chnlIncomPercent(HttpServletRequest request, HttpServletResponse response) {
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		request.setAttribute("gameList", gameinfoList);
		
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		request.setAttribute("chnlList", chnlInfoList);
		
		String date = DateTool.getCurrentDate("yyyyMMdd");
		request.setAttribute("_startDay", date);
		request.setAttribute("_endDay", date);
		
		return new ModelAndView("/order/chnlIncomPercentList.vm");
	}
	
	@RequestMapping("/chnlIncomPercentQuery.do")
	public ModelAndView chnlIncomPercentQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<GameInfo> gameinfoList = orderService.queryAllGame();
		request.setAttribute("gameList", gameinfoList);
		
		List<ChnlInfo> chnlInfoList = orderService.queryAllChnl();
		request.setAttribute("chnlList", chnlInfoList);
		
		Long gameInfoId = RequestUtil.GetParamLong(request, "gameInfoId", null);
		
		request.setAttribute("_gameInfoId", gameInfoId);
		
		Long chnlInfoId = RequestUtil.GetParamLong(request, "chnlInfoId", null);
		request.setAttribute("_chnlInfoId", chnlInfoId);
		
		String startDay = RequestUtil.GetParamString(request, "startDay", null);
		String endDay = RequestUtil.GetParamString(request, "endDay", null);
		String providerId = RequestUtil.GetParamString(request, "providerId", null);
		
		request.setAttribute("_startDay", startDay);
		request.setAttribute("_endDay", endDay);
		request.setAttribute("_providerId", providerId);
		
		Integer pageId = RequestUtil.GetParamInteger(request, "pageId", 1);
		
		ChnlIncomPercent chnlIncomPercent = new ChnlIncomPercent();
		chnlIncomPercent.setGameInfoId(gameInfoId);
		chnlIncomPercent.setChnlId(chnlInfoId);
		chnlIncomPercent.setStartDate(startDay);
		chnlIncomPercent.setEnDate(endDay);
		chnlIncomPercent.setState(providerId);
		
		List<String> orderMonthList = new ArrayList<String>();
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		int num = DateTool.compareDate(startDay, endDay, "yyyyMMdd");
		
		Calendar c = Calendar.getInstance();
		
		for (int i = 0; i <= num; i++) {
			c.setTime(df.parse(startDay));
			c.add(Calendar.MONTH, i);
			orderMonthList.add(DateTool.DateToString(c.getTime(), "yyMM"));
		}
		
		chnlIncomPercent.setOrderMonthList(orderMonthList);
		
		Page<ChnlIncomPercent> page = new Page<ChnlIncomPercent>();
		page.setPageNo(pageId);
		page.setPageSize(Config.get().getInt("pageSize", 200));
		
		List<ChnlIncomPercent> list = orderService.queryIncomePercent(chnlIncomPercent, page);
		
		if (list != null && list.size() > 0) {
			NumberFormat percentFormat = NumberFormat.getPercentInstance(); 
			
			for (int i = 0; i < list.size(); i++) {
				ChnlIncomPercent incomPercent = list.get(i);
				incomPercent.setPercent(percentFormat.format(new Double(incomPercent.getChnlInCome()) / new Double(incomPercent.getSumInCome())));
			}
		}
		
		PagedModelList<ChnlIncomPercent> pagedModelList = new PagedModelList<ChnlIncomPercent>(pageId, Config.get().getInt("pageSize", 200), page.getTotalRecord());
		
		request.setAttribute("chnlIncomePercentList", list);
		request.setAttribute("pagedModelList", pagedModelList);
		
		return new ModelAndView("/order/chnlIncomPercentList.vm");
	}
}
