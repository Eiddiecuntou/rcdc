package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.WinAppTerminalUpdatelistCacheInit;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

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
public class WinAppTerminalUpgradeCacheInitTest {

    @Tested
    private WinAppTerminalUpgradeCacheInit init;

    @Injectable
    private WinAppTerminalUpdatelistCacheInit windowsAppTerminalUpdatelistCacheInit;

    /**
     * testSafeInit
     */
    @Test
    public void testSafeInit() {
        new Expectations() {
            {
                windowsAppTerminalUpdatelistCacheInit.init();
            }
        };

        init.safeInit();

        new Verifications() {
            {
                windowsAppTerminalUpdatelistCacheInit.init();
                times = 1;
            }
        };
    }

}
