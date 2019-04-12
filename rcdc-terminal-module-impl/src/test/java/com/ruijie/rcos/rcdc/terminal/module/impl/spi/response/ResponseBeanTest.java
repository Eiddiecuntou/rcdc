package com.ruijie.rcos.rcdc.terminal.module.impl.spi.response;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class ResponseBeanTest {

    /**
     * 测试Localelanguage
     */
    @Test
    public void testLocalelanguage() {
        GetSetTester tester = new GetSetTester(Localelanguage.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalLogName
     */
    @Test
    public void testTerminalLogName() {
        GetSetTester tester = new GetSetTester(TerminalLogName.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalPassword
     */
    @Test
    public void testTerminalPassword() {
        GetSetTester tester = new GetSetTester(TerminalPassword.class);
        tester.runTest();
        assertTrue(true);
    }
}
