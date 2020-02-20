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
 * Create Time: 2019/8/12
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class OtaCheckResultContentTest {

    /**
     * 测试GetVersionRequest请求
     */
    @Test
    public void testAndroidVDICheckResultContent() {
        GetSetTester tester = new GetSetTester(OtaCheckResultContent.class);
        tester.runTest();
        Assert.assertTrue(true);
    }
}
