package com.smart.mirrorer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

public class IDateUtil {

	
	
	
    /**
     * 时间格式化
     * 事例:"yyyy-MM-dd HH:mm"    "yyyy-MM-dd HH:mm:ss"
     * @param time
     * @param param
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatTime(long time, String param) {
        SimpleDateFormat format = new SimpleDateFormat(param);
        return format.format(new Date(time));
    }
   
    /**
     * 从"yyyy-MM-dd HH:mm:ss"直接转成 刚刚  x分钟前 。。。等等
     * 结果和formatDateToString一样
     * */
    public static String formatDateToString2(String strTime) {
    	long stringToLong2 = stringToLong(strTime);
    	String result = formatDateToString(stringToLong2);
    	return result;
    }
	public static String formatDateToString(long time) {
		String interval = "";
		long millis = new Date().getTime() - time;// 得出的时间间隔是毫秒
		/*if (millis / 1000 < 10 && millis / 1000 >= 0) {
			// 如果时间间隔小于10秒则显示“刚刚”millis/10得出的时间间隔的单位是秒
			interval = "刚刚";

		} else */if (millis / 1000 < 60 ) {
			// 如果时间间隔小于60秒则显示多少秒前
//
			int se = (int) ((millis % 60000) / 1000);
//			interval = se + "秒前";

			//显示1分钟前
			interval = "1分钟前";
		} else if (millis / 60000 < 60 && millis / 60000 > 0) {
			// 如果时间间隔小于60分钟则显示多少分钟前
			int m = (int) ((millis % 3600000) / 60000);// 得出的时间间隔的单位是分钟
			interval = m + "分钟前";

		} else if (millis / 3600000 < 24 && millis / 3600000 > 0) {
			// 如果时间间隔小于24小时则显示多少小时前
			int h = (int) (millis / 3600000);// 得出的时间间隔的单位是小时
			interval = h + "小时前";

		}
//		else if (isTimeInThisYear(time)){
//			// 大于24小时，在今年内，则显示正常的时间，但是不显示秒
//			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
//			interval = sdf.format(new Date(time));
//		}
		else{
			// 不在今年内，则显示正常的时间，但是不显示秒
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			interval = sdf.format(new Date(time));
		}
		return interval;
	}
	private static boolean isTimeInThisYear(long millis){

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		int year = c.get(Calendar.YEAR);
		if (year-getCurrentYear()==0){//在同一年
			return true;
		}else{
			return false;
		}
	}
	private static int getCurrentYear(){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		return c.get(Calendar.YEAR);
	}
	
	/**
	 * 新添方法
	 * */
	 public static Date stringToDate(String strTime, String formatType) {
			SimpleDateFormat formatter = new SimpleDateFormat(formatType);
			Date date = null;
			try {
				date = formatter.parse(strTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		}
	    public static long stringToLong(String strTime) {
			Date date = stringToDate(strTime, "yyyy-MM-dd HH:mm:ss"); // String类型转成date类型
			if (date == null) {
				return 0;
			} else {
				long currentTime = date.getTime(); // date类型转成long类型
				return currentTime;
			}
		}
}
