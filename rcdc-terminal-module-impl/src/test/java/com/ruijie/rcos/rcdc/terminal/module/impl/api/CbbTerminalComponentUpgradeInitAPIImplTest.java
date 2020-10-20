package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentInitService;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/20
 *
 * @author nting
 */
@RunWith(SkyEngineRunner.class)
public class CbbTerminalComponentUpgradeInitAPIImplTest {

    @Tested
    private CbbTerminalComponentUpgradeInitAPIImpl terminalComponentUpgradeInitAPI;

    @Injectable
    private TerminalComponentInitService terminalComponentInitService;

    /**
     * testInitAndroidOTA
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitAndroidOTA() throws Exception {

        new Expectations() {
            {
                terminalComponentInitService.initAndroidVDI();
            }
        };

        terminalComponentUpgradeInitAPI.initAndroidVDI();

        new Verifications() {
            {
                terminalComponentInitService.initAndroidVDI();
                times = 1;
            }
        };
    }

    /**
     * testInitAndroidVDI
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitAndroidVDI() throws Exception {

        new Expectations() {
            {
                terminalComponentInitService.initAndroidVDI();
            }
        };

        terminalComponentUpgradeInitAPI.initAndroidVDI();

        new Verifications() {
            {
                terminalComponentInitService.initAndroidVDI();
                times = 1;
            }
        };
    }

    /**
     * testInitLinuxVDI
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitLinuxVDI() throws Exception {

        new Expectations() {
            {
                terminalComponentInitService.initLinuxVDI();
            }
        };

        terminalComponentUpgradeInitAPI.initLinuxVDI();

        new Verifications() {
            {
                terminalComponentInitService.initLinuxVDI();
                times = 1;
            }
        };
    }

    /**
     * testInitLinuxVDI
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitLinuxIDV() throws Exception {

        new Expectations() {
            {
                terminalComponentInitService.initLinuxIDV();
            }
        };

        terminalComponentUpgradeInitAPI.initLinuxIDV();

        new Verifications() {
            {
                terminalComponentInitService.initLinuxIDV();
                times = 1;
            }
        };
    }

}
