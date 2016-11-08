package com.woting.ui.mine.updatepersonnews.util;

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

}
