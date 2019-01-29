package pubUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类
 * 
 * @author cdq
 * 
 */
public class DateUtil {
    public final static String YYYYMMDD          = "yyyyMMdd";
    public final static String YYYYMMDDHH24MISS  = "yyyyMMddHHmmss";
    public final static String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    public final static String HH24MISS          = "HHmmss";
    public final static Date   NULL              = str2Date("18000101000000");//空日期的时间值
    public final static String YDDATE          = "yyyy年MM月dd日";

    
    /**
     * 判断时间是否为NULL值
     * @param dt 时间
     * @return null或NULL返会true
     */
    public static boolean isNull(Date dt) {
        if (dt == null || NULL.equals(dt)) {
            return true;
        }
        return false;
    }
    
    /**
     * 获取当前时间
     * 
     * @return 返回时间格式为yyyyMMddHHmmss的字符串
     */
    public static String getCurDate() {
        return date2Str(new Date(), YYYYMMDDHH24MISS);
    }
    
    /**
     * 获取当前时间
     * 
     * @param dateFormat
     *            时间格式
     * @return 返回指定时间格式的字符串
     */
    public static String getCurDate(String dateFormat) {
        return date2Str(new Date(), dateFormat);
    }
    
    /**
     * 将时间格式为YYYYMMDDHH24MISS的字符串转化为Date
     * 
     * @param dateStr
     *            时间格式为YYYYMMDDHH24MISS的字符串
     * @return Date
     */
    public static Date str2Date(String dateStr) {
        return str2Date(dateStr, YYYYMMDDHH24MISS);
    }
    
    /**
     * 时间串转化为Date
     * 
     * @param dateStr
     *            dateFormat时间格式的字符串
     * @param dateFormat
     *            时间格式
     * @return Date
     */
    public static Date str2Date(String dateStr, String dateFormat) {
        if (StringHelper.isEmpty(dateStr)) {
            return null;
        }
        
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        try {
            return df.parse(dateStr);
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Date转化为YYYYMMDDHH24MISS格式的字符串
     * 
     * @param date
     *            Date
     * @return YYYYMMDDHH24MISS格式的字符串
     */
    public static String date2Str(Date date) {
        return date2Str(date, YYYYMMDDHH24MISS);
    }
    
    /**
     * Date转化为dateFormat时间格式的字符串
     * 
     * @param date
     *            Date
     * @param dateFormat
     *            时间格式
     * @return dateFormat时间格式的字符串
     */
    public static String date2Str(Date date, String dateFormat) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }
    
    public static void main(String[] args) {
        System.out.println(DateUtil.getCurDate(DateUtil.YYYYMMDDHHMMSSSSS));
    }

    /**
     * 获得指定时间的零点
     * @return
     */
    public static Date getCurrentZero(long current) {
        String dates = DateUtil.date2Str(new Date(current), DateUtil.YYYYMMDD)+"000000";
        return DateUtil.str2Date(dates, DateUtil.YYYYMMDDHH24MISS);
    }

    /**
     * 获得指定时间的24点
     * @return
     */
    public static Date getCurrentTwelve(long current) {
        String dates = DateUtil.date2Str(new Date(current), DateUtil.YYYYMMDD)+"235959";
        return  DateUtil.str2Date(dates, DateUtil.YYYYMMDDHH24MISS);
    }


    /**
     * 获得输入时间是当前时间几天几小时前
     * @return
     */
    public static String getOutTime(long second ){
    	try {
    		second = second/1000;
    		long days = second / 86400;            //转换天数
    		second = second % 86400;            //剩余秒数
    		long hours = second / 3600;            //转换小时
    		second = second % 3600;                //剩余秒数
    		long minutes = second /60;            //转换分钟
    		second = second % 60;                //剩余秒数
    		if(days > 0){
    			if(days > 30){
    				Date origin = new Date(System.currentTimeMillis()-second);
    				return DateUtil.date2Str(origin, DateUtil.YDDATE);
    			}
    			return days + "天前";
    		}else{
    			if(hours == 0){
    				if(minutes == 0){
    					return "刚刚";
    				}
    				return minutes+"分钟前";
    			}
    			return hours + "小时前";
    		}
		} catch (Exception e) {
			return DateUtil.getCurDate(YDDATE);
		}
    
    }
}
