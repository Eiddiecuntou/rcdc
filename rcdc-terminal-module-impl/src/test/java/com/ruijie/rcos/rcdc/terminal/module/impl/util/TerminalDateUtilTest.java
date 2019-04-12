package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalDateUtilTest {

    /**
     * 测试getDayStart,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetDayStartArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> TerminalDateUtil.getDayStart(null), "date can not be null");
        assertTrue(true);
    }

    /**
     * 测试getDayStart,
     */
    @Test
    public void testGetDayStart() {
        Date date = new Date();
        Date date1 = TerminalDateUtil.getDayStart(date);
        assertNotSame(date, date1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    /**
     * 测试getDayEnd,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetDayEndArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> TerminalDateUtil.getDayEnd(null), "date can not be null");
        assertTrue(true);
    }

    /**
     * 测试getDayEnd,
     */
    @Test
    public void testGetDayEnd() {
        Date date = new Date();
        Date date1 = TerminalDateUtil.getDayEnd(date);
        assertNotSame(date, date1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, calendar.get(Calendar.MINUTE));
        assertEquals(59, calendar.get(Calendar.SECOND));
        assertEquals(999, calendar.get(Calendar.MILLISECOND));
    }

    /**
     * 测试addDay,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddDayArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> TerminalDateUtil.addDay(null, 1), "date can not be null");
        assertTrue(true);
    }

    /**
     * 测试addDay,
     */
    @Test
    public void testAddDay() {
        Date date = new Date();
        Date date1 = TerminalDateUtil.addDay(date, 2);
        assertNotSame(date, date1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        assertEquals(calendar.getTime(), date1);
    }

    /**
     * 测试addSecond,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddSecondArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> TerminalDateUtil.addSecond(null, 30), "date can not be null");
        assertTrue(true);
    }

    /**
     * 测试addSecond,
     */
    @Test
    public void testAddSecond() {
        Date date = new Date();
        Date date1 = TerminalDateUtil.addSecond(date, 30);
        assertNotSame(date, date1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, 30);
        assertEquals(calendar.getTime(), date1);
    }


    /**
     * 测试isTimeout,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testIsTimeoutArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> TerminalDateUtil.isTimeout(null, 30), "date can not be null");
        assertTrue(true);
    }

    /**
     * 测试isTimeout,
     */
    @Test
    public void testIsTimeout() {
        Date date = TerminalDateUtil.addSecond(new Date(), -31);
        assertTrue(TerminalDateUtil.isTimeout(date, 30));
    }
}
