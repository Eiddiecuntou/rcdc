package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxVDIUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.WinAppTerminalUpdatelistCacheInit;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(SkyEngineRunner.class)
public class TerminalComponentUpgradeCacheInitTest {

    @Tested
    private TerminalComponentUpgradeCacheInit init;

    @Injectable
    private LinuxVDIUpdatelistCacheInit linuxVDIUpdatelistCacheInit;

    @Injectable
    private WinAppTerminalUpdatelistCacheInit windowsAppTerminalUpdatelistCacheInit;

    @Test
    public void testSafeInit() {
        new Expectations() {
            {
                linuxVDIUpdatelistCacheInit.init();

                windowsAppTerminalUpdatelistCacheInit.init();
            }
        };

        init.cachesInit();

        new Verifications() {
            {
                linuxVDIUpdatelistCacheInit.init();
                times = 1;

                windowsAppTerminalUpdatelistCacheInit.init();
                times = 1;
            }
        };
    }

}
