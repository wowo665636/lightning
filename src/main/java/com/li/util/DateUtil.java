package com.li.util;


import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateUtil {

	public static String toLastFriday(Calendar cl){
		cl.set(Calendar.HOUR, 0);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MINUTE, 0);
		int week = cl.get(Calendar.DAY_OF_WEEK);
		//星期5为6
		int day = cl.get(Calendar.DAY_OF_MONTH);
		day =day - week-1;
		cl.set(Calendar.DAY_OF_MONTH, day);
		Date date = cl.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}
	public static String toThursday(Calendar cl){
		cl.set(Calendar.HOUR, 0);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MINUTE, 0);
		int week = cl.get(Calendar.DAY_OF_WEEK);
		//星期5为6
		int day = cl.get(Calendar.DAY_OF_MONTH);
		day =day + (5-week);
		cl.set(Calendar.DAY_OF_MONTH, day);
		Date date = cl.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}
	
	/**
	 * 返回毫秒数
	 * @param date 日期
	 * @return 返回毫秒
	 */
	public static long getMillis(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}

	/**
	 * 日期相加，并按照要求日期格式输出 ,示例：20120305
	 * @param date 被操作日期
	 * @param days 被加天数
	 * @return 标准化操作后日期
	 */
	public static String addDate(Date date, int days){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		calendar.setTimeInMillis(getMillis(date) +  ((long)  days)  *  24  *  3600  *  1000);
		return simpleDateFormat.format(calendar.getTime());
	}

	/**
	 * 日期相减，并按照要求日期格式输出 ,示例：20120305
	 * @param date 被操作日期
	 * @param days 被加天数
	 * @return 标准化操作后日期
	 */
	public static String diffDate(Date date, int days){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		calendar.setTimeInMillis(getMillis(date) - ((long)  days)  *  24  *  3600  *  1000);
		return simpleDateFormat.format(calendar.getTime());
	}
	/**
	 * 日期相减，并按照要求日期格式输出 ,示例：20120305
	 * @param date 被操作日期
	 * @param millis 被加天数
	 * @return 标准化操作后日期
	 */
	public static String diffDateTime(Date date, int millis){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		calendar.setTimeInMillis(getMillis(date) - millis);
		return simpleDateFormat.format(calendar.getTime());
	}
	/**
	 * 日期相减，并按照要求日期格式输出 ,示例：2012-03-05
	 * @param date 被操作日期
	 * @param days 被加天数
	 * @return 标准化操作后日期
	 */
	public static String diffDate1(Date date, int days){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		calendar.setTimeInMillis(getMillis(date) - ((long)  days)  *  24  *  3600  *  1000);
		return simpleDateFormat.format(calendar.getTime());
	}
	/**
	 * 规范化日期，规范成yyyyMMdd
	 * @param date
	 * @return
	 */
	public static String getFormatDateString(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(date);
	}
	/**
	 * 规范化日期，规范成yyyy-MM-dd HH:mm:ss
	 * @param timestamp
	 * @return
	 */
	public static String timestamp2Datetime(long timestamp){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date(timestamp * 1000));
	}
	/**
	 * 规范化日期，规范成yyyy-MM-dd
	 * @param timestamp
	 * @return
	 */
	public static String timestamp2Date(long timestamp){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new Date(timestamp * 1000));
	}
	/**
	 * 规范化日期，规范成yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String Date2DateString(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	/**
	 * 规范化日期，规范成yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static String Date2String(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}
	
	/**
	 * 规范化日期，规范成yyyy-MM
	 * @param date
	 * @return
	 */
	public static String Month2String(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		return dateFormat.format(date);
	}
	
	/**
	 * yyyy-MM-dd HH:mm:ss转成long型数据
	 * @param datestr
	 * @return
	 */
	public static long Date2Timestamp(String datestr){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = dateFormat.parse(datestr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timestamp = date.getTime()/1000;
		return timestamp;
	}
	/**
	 * yyyy-MM-dd HH:mm:ss转成long型数据
	 * @param datestr
	 * @return
	 */
	public static long Date3Timestamp(String datestr){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = dateFormat.parse(datestr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timestamp = date.getTime()/1000;
		return timestamp;
	}
	/**
	 *获得当前时间 
	 * @return
	 */
	public static String getCurrentDatetime(){
		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	/**
	 *获得当前时间
	 * @return
	 */
	public static String getCurrentDateMM(){
		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmm");
		return df.format(date);
	}



	/**
	 * 获得当前日期yyyy-MM-dd
	 * @return
	 */
	public static String getCurrentFormatDate2(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String convertMysqlTimestampToDatetime(Date date) {
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}
	/**
	 * 获得当前时间秒数
	 * @return
	 */
	public static int getCurrentTimeStamp(){
		String timeStamp = Long.toString(System.currentTimeMillis()/1000);
		return Integer.parseInt(timeStamp);
	}
	
	/**
	 * 剩余时间秒数值，依据设定规则换算，如大于24小时，换算成天；如小于24小时，换算成小时；如小于1小时，换算成分钟；如小于1分钟，换算成秒。
	 * @return
	 */
	public static String getRemainDayOrHour(int second) {
		int oneDay = 24 * 60 * 60;
		int oneHour = 60 * 60;
		int oneMin = 60;
		int oneSec = 1;
		String remainDayOrHour = "";
		if(second >= oneDay){
			remainDayOrHour = (second/oneDay + 1) + "天";
		}else{
			if(second >= oneHour){
				remainDayOrHour = (second/oneHour + 1) + "小时";
			}else if(second >= oneMin){
				remainDayOrHour = (second/oneMin) + "分钟";
			}else if(second >= oneSec){
				remainDayOrHour = (second) + "秒";
			}
		}
		return remainDayOrHour;
	}
	/**
	 * 时间对比
	 * DateUtil.compareDate()<BR>
	 * <P>Author : wangdi </P>  
	 * <P>Date : 2016年1月27日 </P>
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Integer compareDate(String date1, String date2){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 try {
	            Date dt1 = df.parse(date1);
	            Date dt2 = df.parse(date2);
	            if (dt1.getTime() > dt2.getTime()) {
	            	//dt1 在dt2前
	                return 1;
	            } else if (dt1.getTime() < dt2.getTime()) {
	               //dt1 在dt2后
	                return -1;
	            } else {
	            	//dt1 =dt2
	                return 0;
	            }
	        } catch (Exception exception) {
	            exception.printStackTrace();
	        }
		return 999;
	}
	/**
	 * 获取当前日期的前?天
	 * DateUtil.getBeforDate()<BR>
	 * <P>Author : wangdi </P>  
	 * <P>Date : 2016年2月2日 </P>
	 * @param day
	 * @return
	 */
	public static String getBeforDate(int day){
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Date beginDate = new Date();
		Calendar date = Calendar.getInstance();
		date.setTime(beginDate);
		date.set(Calendar.DATE, date.get(Calendar.DATE) - day);
		return dft.format(date.getTime());
	}
	/**
	 * yyyy-MM-dd 类型字符串日期变化为Date类型
	 * @param dates
	 * @return
	 */
	public static Date convertDateString2Date(String dates){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		   try {
		    date = dateFormat.parse(dates);
		   } catch (ParseException e) {
		    e.printStackTrace();
		   }
		   return date;
	}
	
	/**
     * 规范化日期，规范成yyyy-MM-dd HH:mm:ss
     * @param timestamp (精度为13位)
     * @return
     */
    public static String timestamp2Datetime2(long timestamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date(timestamp));
    }
	/**
	 * 规范化日期，规范成yyyy-MM-dd HH:mm:ss
	 * @param timestamp (精度为11位)
	 * @return
	 */
	public static String timestamp2Datetime2(String timestamp){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		if(StringUtils.isBlank(timestamp)){
			return dateFormat.format(new Date());
		}
		if(timestamp.length()<10){
			return dateFormat.format(new Date());
		}
		if(timestamp.length()==13){
			return dateFormat.format(new Date(Long.valueOf(timestamp)));
		}
		return dateFormat.format(new Date(Long.valueOf(timestamp)*1000));
	}



	/**
	 * 规范化日期，规范成yyyy-MM-dd HH:mm:ss
	 * @param timestamp (精度为11位)
	 * @return
	 */
	public static String timestamp2DateMinute(String timestamp){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

		if(StringUtils.isBlank(timestamp)){
			return dateFormat.format(new Date());
		}
		if(timestamp.length()<10){
			return dateFormat.format(new Date());
		}
		if(timestamp.length()==13){
			return dateFormat.format(new Date(Long.valueOf(timestamp)));
		}
		return dateFormat.format(new Date(Long.valueOf(timestamp)*1000));
	}



	/**
	 * 日期格式到小时
	 * @param timestamp
	 * @return
     */
	public  String timestamp2Hour(String timestamp){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
		if(StringUtils.isBlank(timestamp)||timestamp.length()<10){
			return null;
		}
		if(timestamp.length()==13){
			return dateFormat.format(new Date(Long.valueOf(timestamp)));
		}
		return dateFormat.format(new Date(Long.valueOf(timestamp)*1000));
	}

	/**
	 * 日期 到天
	 * @param timestamp
	 * @return
     */
	public static String timestamp2Day(String timestamp){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		if(StringUtils.isBlank(timestamp)||timestamp.length()<10){
			return null;
		}
		if(timestamp.length()==13){
			return dateFormat.format(new Date(Long.valueOf(timestamp)));
		}
		return dateFormat.format(new Date(Long.valueOf(timestamp)*1000));
	}
    /**
     * 规范化日期，规范成yyyy-MM-dd
     * @param timestamp  (精度为13位)
     * @return
     */
    public static String timestamp2Date2(long timestamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date(timestamp));
    }

	/**
	 * 特殊时间处理
	 * @param dateTime
	 * @return
     */
	public static String hyTimeFormat(String dateTime){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
		try {
			long time =  sdf.parse(dateTime).getTime();
			return String.valueOf(time);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return "0";

	}

	public static String dateTimeToString(Date date) {
		return dateTimeToString(date, "yyyy-MM-dd HH:mm");
	}

	public static String dateTimeToString(Date date, String format) {
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			return formatter.format(date);
		}
		return null;
	}

    public static void main(String[] args) {
        //System.out.println(timestamp2Date2(1456390734317L));
        //System.out.println(DateUtil.diffDate1(DateUtil.convertDateString2Date(DateUtil.timestamp2Date(DateUtil.Date2Timestamp("2016-01-06 20:32:59"))),1));

		DateUtil dateUtil  = new DateUtil();
		System.out.println(timestamp2Datetime2(1542731111782L));
		/*String time_stamp=timestamp2Date2(1355292000000l);
		String time_suffix=" 23:59:59";
		long time_end =  Date2Timestamp(time_stamp+time_suffix);
		System.out.println("--"+time_end);
		System.out.println(timestamp2Datetime2(time_end*1000l));


		System.out.println(dateUtil.timestamp2Hour("1588496322"));

		System.out.println(dateUtil.timestamp2Hour("1510227529551"));


		System.out.println("---:"+timestamp2Datetime2(1511265212855L));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = dateFormat.parse("2017-11-13 10:00:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timestamp = date.getTime();
		System.out.println(timestamp);

		System.out.println("201807231423".substring(0,8));
		System.out.println("201807231423".substring(8,10));

		System.out.println("201807231423".substring(10,12));*/








	}


}
