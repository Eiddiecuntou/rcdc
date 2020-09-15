package com.ruijie.rcos.rcdc.terminal.module.impl.entity;


import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/15 1:43 下午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class TerminalEntityTest {

    @Test
    public void testGetAndSet() {
        GetSetTester tester = new GetSetTester(TerminalEntity.class);
        tester.addIgnoreProperty("networkInfoArr");
        tester.runTest();
        Assert.assertTrue(true);
    }
}