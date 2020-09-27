package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.AppTerminalUpdateListCacheInit;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Mocked;
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
public class AppTerminalUpgradeCacheInitTest {

    @Tested
    private AppTerminalUpgradeCacheInit init;

    @Mocked
    private AppTerminalUpdateListCacheInit windowsAppTerminalUpdatelistCacheInit;

    /**
     * testSafeInit
     */
    @Test
    public void testSafeInit() {

        init.safeInit();

        new Verifications() {
            {
                windowsAppTerminalUpdatelistCacheInit.init();
                times = 3;
            }
        };
    }

}
