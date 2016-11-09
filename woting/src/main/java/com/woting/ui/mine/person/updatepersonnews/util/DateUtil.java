package com.woting.ui.mine.person.updatepersonnews.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/8 0008.
 */
public class DateUtil {

    public static List<String> getYearList(){
        List<String> yearList =new ArrayList<>();
        for(int i=1930;i<=2016;i++){
            yearList.add(i+"年");
        }
        return yearList;
    }

    public static List<String> getMonthList(){
        List<String> MonthList =new ArrayList<>();
        for(int i=1;i<10;i++){
            MonthList.add(" "+i+"月");
        }
        MonthList.add(10+"月");
        MonthList.add(11+"月");
        MonthList.add(12+"月");
        return MonthList;
    }

    public static List<String> getDayList31(){
        List<String> dayList =new ArrayList<>();
        for(int i=1;i<10;i++){
            dayList.add(" "+i+"日");
        }
        for(int i=10;i<32;i++){
            dayList.add(i+"日");
        }
        return dayList;
    }

    public static List<String> getDayList30(){
        List<String> dayList =new ArrayList<>();
        for(int i=1;i<10;i++){
            dayList.add(" "+i+"日");
        }
        for(int i=10;i<31;i++){
            dayList.add(i+"日");
        }
        return dayList;
    }

    public static List<String> getDayList29(){
        List<String> dayList =new ArrayList<>();
        for(int i=1;i<10;i++){
            dayList.add(" "+i+"日");
        }
        for(int i=10;i<30;i++){
            dayList.add(i+"日");
        }
        return dayList;
    }

    public static List<String> getDayList28(){
        List<String> dayList =new ArrayList<>();
        for(int i=1;i<10;i++){
            dayList.add(" "+i+"日");
        }
        for(int i=10;i<29;i++){
            dayList.add(i+"日");
        }
        return dayList;
    }

    /**
     *获取星座
     */
    public static String getConstellation(int m,int d){

        final String[] constellationArr = {"魔羯座" ,"水瓶座", "双鱼座", "牡羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座","天蝎座", "射手座", "魔羯座" };

        final int[] constellationEdgeDay = { 20,18,20,20,20,21,22,22,22,22,21,21};
        int month=m;
        int day =d;
        if (day <= constellationEdgeDay[month-1]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        //default to return 魔羯
        return constellationArr[11];

    }

}
