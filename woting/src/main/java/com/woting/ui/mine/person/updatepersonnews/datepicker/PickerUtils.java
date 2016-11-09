package com.woting.ui.mine.person.updatepersonnews.datepicker;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/11/7 0007.
 */
public class PickerUtils {
    public static Calendar calendar;
    public static Context context;
    public static final String startTime="1980年1月15日";
    private String dateTime;


    public PickerUtils(){


    }
    public  Calendar getCalender(Context context){
        this.context=context;
        calendar=Calendar.getInstance();
        calendar=this.getCalendarByInintData(startTime);

        return calendar;
    };

    /**
     * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
     *
     * @param initDateTime
     *            初始日期时间值 字符串型
     * @return Calendar
     */
    private Calendar getCalendarByInintData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        // 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
        String date = splitString(initDateTime, "日", "index", "front"); // 日期
        String yearStr = splitString(date, "年", "index", "front"); // 年份
        String monthAndDay = splitString(date, "年", "index", "back"); // 月日
        String monthStr = splitString(monthAndDay, "月", "index", "front"); // 月
        String dayStr = splitString(monthAndDay, "月", "index", "back"); // 日
        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();
        calendar.set(currentYear, currentMonth, currentDay);
        return calendar;
    }

    /**
     * 截取子串
     *
     * @param srcStr
     *            源串
     * @param pattern
     *            匹配模式
     * @param indexOrLast
     * @param frontOrBack
     * @return
     */
    public static String splitString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }
}
