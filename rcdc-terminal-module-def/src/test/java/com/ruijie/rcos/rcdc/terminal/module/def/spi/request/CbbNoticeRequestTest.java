package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(SkyEngineRunner.class)
public class CbbNoticeRequestTest {
    /**
     * 测试getAndSet方法
     */
    @Test
    public void testSetAndGet() {
        GetSetTester getSetTester = new GetSetTester(CbbNoticeRequest.class);
        getSetTester.runTest();
        Assert.assertTrue(true);
    }

}