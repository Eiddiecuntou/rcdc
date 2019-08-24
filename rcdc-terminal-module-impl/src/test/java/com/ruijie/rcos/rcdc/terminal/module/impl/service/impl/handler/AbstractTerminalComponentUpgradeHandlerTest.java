package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

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
     * 测试从版本字符串获取数字版本
     */
    @Test
    public void testGetVersionFromVerStr() {
        TestedTerminalComponentUpgradeHandler handler = new TestedTerminalComponentUpgradeHandler();

        String versionStr = "2.0.3.1";
        Integer version = handler.getVersionFromVerStr(versionStr);
        Assert.assertEquals(203, version.intValue());
    }

    /**
     * 测试从版本字符串获取数字版本-字符串无点分隔
     */
    @Test
    public void testGetVersionFromVerStrWhileVesionStrHasNoDot() {
        TestedTerminalComponentUpgradeHandler handler = new TestedTerminalComponentUpgradeHandler();

        String versionStr = "2031";
        Integer version = handler.getVersionFromVerStr(versionStr);
        Assert.assertEquals(0, version.intValue());
    }

    /**
     * 测试从版本字符串获取数字版本-版本号为空
     */
    @Test
    public void testGetVersionFromVerStrArgIsNull() throws Exception {
        TestedTerminalComponentUpgradeHandler handler = new TestedTerminalComponentUpgradeHandler();
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.getVersionFromVerStr(null), "version can not be blank");
        Assert.assertTrue(true);
    }

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
     */
    @Test
    public void testCompareVersionArgIsNull() throws Exception {
        TestedTerminalComponentUpgradeHandler handler = new TestedTerminalComponentUpgradeHandler();
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.isVersionBigger(null, "sss"), "firstVersion can not be blank");
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.isVersionBigger("sss", null), "secondVersion can not be blank");
        Assert.assertTrue(true);
    }

    private class TestedTerminalComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

        @Override
        public TerminalVersionResultDTO getVersion(GetVersionRequest request) {
            // 测试类
            return null;
        }
    }


}
