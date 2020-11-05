package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOtaInitService;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
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
public class CbbTerminalUpgradeInitAPIImplTest {

    @Tested
    CbbTerminalUpgradeInitAPIImpl terminalUpgradeInitAPI;

    @Injectable
    private TerminalOtaInitService terminalOtaInitService;

    /**
     * 测试listTerminalModel参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitAndroidOTA() throws Exception {

        new Expectations() {
            {
                terminalOtaInitService.initAndroidOta();
            }
        };

        terminalUpgradeInitAPI.initAndroidOTA();

        new Verifications() {
            {
                terminalOtaInitService.initAndroidOta();
                times = 1;
            }
        };
    }

}
