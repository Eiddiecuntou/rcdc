package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Title: KylinAppComponentUpgradeHandlerTest
 * Description: Function Description
 * Copyright: Ruijie Co., Ltd. (c) 2021
 *
 * @Author: juan_chen
 * @Date: 2021/4/20 13:25
 */
@RunWith(SkyEngineRunner.class)
public class KylinAppComponentUpgradeHandlerTest {
    @Tested
    private KylinAppComponentUpgradeHandler handler;

    /**
     *测试getTerminalType
     */
    @Test
    public void testGetTerminalType() {
        TerminalOsArchType osType = handler.getTerminalOsArchType();
        Assert.assertEquals(TerminalOsArchType.KYLIN_X86, osType);
    }
}
