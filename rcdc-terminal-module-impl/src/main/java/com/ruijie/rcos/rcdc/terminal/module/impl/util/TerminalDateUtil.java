package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import java.util.Calendar;
import java.util.Date;
import org.springframework.util.Assert;

/**
 * 
 * Description: 日期工具类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class TerminalDateUtil {

    /**
     * 得到日期的起始时间点。
     * 
     * @param date 日期
     * @return 起始时间
     */
    public static Date getDayStart(Date date) {
        Assert.notNull(date, "date can not be null");
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 得到day的终止时间点.
     * 
     * @param date 日期
     * @return 终止时间
     */
    public static Date getDayEnd(Date date) {
        Assert.notNull(date, "date can not be null");
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 获取指定天数后的时间
     * 
     * @param date 日期
     * @param day 天数
     * @return 日期
     */
    public static Date addDay(Date date, int day) {
        Assert.notNull(date, "date can not be null");
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

}
