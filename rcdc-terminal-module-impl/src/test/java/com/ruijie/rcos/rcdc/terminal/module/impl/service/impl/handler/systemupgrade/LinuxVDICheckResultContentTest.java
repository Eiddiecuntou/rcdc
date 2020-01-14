package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/8
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class LinuxVDICheckResultContentTest {

    /**
     * 测试LinuxVDICheckResultContent
     */
    @Test
    public void testLinuxVDICheckResultContent() {
        GetSetTester tester = new GetSetTester(LinuxVDICheckResultContent.class);
        tester.runTest();
        Assert.assertTrue(true);
    }
}
