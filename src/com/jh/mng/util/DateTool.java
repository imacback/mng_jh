package com.jh.mng.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateTool {
	public static long getCompareDate(String startDate,String endDate) throws ParseException {
	     SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     Date date1=formatter.parse(startDate);    
	     Date date2=formatter.parse(endDate);
	     long l = date2.getTime() - date1.getTime();
	     long m = l/(60*1000);
	     return m;
	 }
	public static long getCompareDate(String startDate,String endDate,long parsetime,String fmt) throws ParseException {
	     SimpleDateFormat formatter=new SimpleDateFormat(fmt);
	     Date date1=formatter.parse(startDate);    
	     Date date2=formatter.parse(endDate);
	     long l = date2.getTime() - date1.getTime();
	     long m = l/parsetime;
	     if ( (int)m < 0 ) {
	    	 m = -1;
	     }
	     return m;
	 }

	public static String getCurrentTime(){
		SimpleDateFormat formater=new SimpleDateFormat();
		Date date=new Date(); 	
		formater.applyPattern("yyyy-MM-dd HH:mm:ss");
		String orderDate=formater.format(date);
		return orderDate;
	}
	public static String getCurrentDate(String format){
		SimpleDateFormat formater=new SimpleDateFormat();
		Date date=new Date(); 	
		formater.applyPattern(format);
		String orderDate=formater.format(date);
		return orderDate;
	}

	@SuppressWarnings("static-access")
	public static String increaseDate(Calendar cal, int inc,String pattern) {
		   String rdate = "";
		   if (cal != null) {
		     cal.add(cal.DATE, inc);
		     SimpleDateFormat formater=new SimpleDateFormat();
				formater.applyPattern(pattern);
		     rdate = formater.format(cal.getTime());
		   }
		   return rdate;
		 }

	@SuppressWarnings("static-access")
	public static String incDate(Calendar cal, int inc,String format) {
		   String rdate = "";
		   if (cal != null) {
		     cal.add(cal.MONTH, inc);
		     SimpleDateFormat formater=new SimpleDateFormat();
				formater.applyPattern(format);
		     rdate = formater.format(cal.getTime());
		   }
		   return rdate;
	}
	
	public static String DateToString(Date dd){

		return DateToString(dd, "yyyyMMddHHmmss");

	}

	public static String DateToString(Date dd, String fmt){
		String time = "";
		SimpleDateFormat tmp = new SimpleDateFormat(fmt);
		try{
			time = tmp.format(dd);
		}catch(Exception E){
			time = "";
		}
		return time;
	}
	
	public static Date StringToDate(String DT, String fmt){

		return StringToDate(DT, fmt, new Date());
	}

	public static Date StringToDate(String DT, String fmt, Date DefaultDT){

		Date dt;

		try{
			dt = new SimpleDateFormat(fmt).parse(DT);
		}catch(Exception E){
			dt = DefaultDT;
		}

		return dt;
	}
	

	public static String incDate(String dateString, int inc,String format) {
		   Date date = StringToDate(dateString, "yyyyMMddHHmmss");
		   Calendar calendar=Calendar.getInstance();  
		   calendar.setTime(date);  
		   return incDate(calendar, inc, format);
		}
	
	/**
	 * 
	 * @param startDate yyyyMMddHHmmss
	 * @param endDate yyyyMMddHHmmss
	 * @param fmt yyyyMMddHHmmss
	 * @return
	 * @throws ParseException
	 */
	public static long getCompareDate ( String startDate,String endDate,String fmt) throws ParseException {
	 long margin = 0;
	     Date date1 =  StringToDate(startDate, fmt);    
	     Date date2= StringToDate(endDate.substring(0, 8)+"235959", fmt);
	     margin = date2.getTime() - date1.getTime();
	     margin = margin/(1000*60*60*24);
	     
	     if ( (int)margin < 0) {
	      margin = -1;
	     }
	     return margin;
	}
	
	/**
	 * 比较2个日期相差多少月份
	 * @param startDay 开始日期
	 * @param endDay 终止日期
	 * @param fmt 日期格式
	 * @return
	 */
	public static int compareDate(String startDay, String endDay, String fmt) {
		int result = -1;
		
		try {
			DateFormat df = new SimpleDateFormat(fmt);
			Date startDate = df.parse(startDay);
			Date enDate = df.parse(endDay);
			
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTime(enDate);
			
			if (c.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) { //年份相同
				result = c2.get(Calendar.MONTH) - c.get(Calendar.MONTH);
			} else {
				c.add(Calendar.YEAR, 1);
				if (c.after(c2)) { //开始时间到终止时间未超过一年
					result = 12 + (c2.get(Calendar.MONTH) - c.get(Calendar.MONTH));
				} else {
					result = 12 * (c2.get(Calendar.YEAR) - c.get(Calendar.YEAR) + 1) + (c2.get(Calendar.MONTH) - c.get(Calendar.MONTH));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}
	
	/**
     * date2比date1多的天数
     * @param date1    
     * @param date2
     * @return    
     */
    public static int differentDays(Date date1,Date date2)
    {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) // 同一年
		{
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) // 闰年
				{
					timeDistance += 366;
				} else // 不是闰年
				{
					timeDistance += 365;
				}
			}

			return timeDistance + (day2 - day1);
		} else // 不同年
		{
			return day2 - day1;
		}
	}
    
    /**
	 * 通过时间秒毫秒数判断两个时间的间隔
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }
    
    public static void main(String[] args) 
    {
        String dateStr = "2016-2-1";
        String dateStr2 = "2016-2-3";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        try 
        {
            Date date2 = format.parse(dateStr2);
            Date date = format.parse(dateStr);
//            
            System.out.println("两个日期的差距：" + differentDays(date,date2));
//            System.out.println("两个日期的差距：" + differentDaysByMillisecond(date,date2));
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(new Date());
        	System.out.println(increaseDate(cal, 1, "yyyy-MM-dd"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}
