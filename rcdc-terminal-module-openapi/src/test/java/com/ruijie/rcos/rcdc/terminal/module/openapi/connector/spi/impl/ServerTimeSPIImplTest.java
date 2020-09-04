package com.ruijie.rcos.rcdc.terminal.module.openapi.connector.spi.impl;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/4
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class ServerTimeSPIImplTest {

    @Tested
    private ServerTimeSPIImpl serverTimeSPI;

    @Test
    public void testSyncServerTime() {
        new Expectations(System.class) {
            {
                System.currentTimeMillis();
                result = 1L;
            }
        };
        serverTimeSPI.syncServerTime();
        new Verifications() {
            {
                System.currentTimeMillis();
                times = 1;

            }
        };
    }
}
