package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/7/27 17:14
 *
 * @author conghaifeng
 */
@RunWith(SkyEngineRunner.class)
public class UosAppComponentUpgradeHandlerTest {

    @Tested
    private UosAppComponentUpgradeHandler handler;

    /**
     *测试getTerminalType
     */
    @Test
    public void testGetTerminalType() {
        CbbTerminalTypeEnums terminalType = handler.getTerminalType();
        Assert.assertEquals(CbbTerminalTypeEnums.APP_UOS, terminalType);
    }

}
