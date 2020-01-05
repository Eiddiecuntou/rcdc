package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
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
public class AbstractTerminalComponentUpgradeHandlerTest {


    /**
     * 测试比较版本
     */
    @Test
    public void testCompareVersion() {
        TestedTerminalComponentUpgradeHandler handler = new TestedTerminalComponentUpgradeHandler();

        String versionStr1 = "2.0.3.1";
        String versionStr2 = "1.9.4.1";
        boolean isVersionBigger = handler.isVersionBigger(versionStr1, versionStr2);
        Assert.assertEquals(true, isVersionBigger);
    }

    /**
     * 测试比较版本-参数为null
     * 
     * @throws Exception exception
     */
    @Test
    public void testCompareVersionArgIsNull() throws Exception {
        TestedTerminalComponentUpgradeHandler handler = new TestedTerminalComponentUpgradeHandler();
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.isVersionBigger(null, "sss"), "firstVersion can not be blank");
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.isVersionBigger("sss", null), "secondVersion can not be blank");
        Assert.assertTrue(true);
    }

    /**
     * Description: Function Description
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019/8/12
     *
     * @author nt
     */
    private class TestedTerminalComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

        @Override
        public TerminalVersionResultDTO getVersion(GetVersionDTO request) {
            // 测试类
            return null;
        }
    }


}
