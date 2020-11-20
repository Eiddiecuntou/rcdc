package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Tested;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author XiaoJiaXin
 */
@RunWith(SkyEngineRunner.class)
public class AndroidVDIComponentUpgradeHandlerTest {

    @Tested
    private AndroidComponentUpgradeHandler handler;

    @Test
    public void testGetTerminalType() {
        CbbTerminalTypeEnums terminalType = handler.getTerminalOsType();
        Assert.assertEquals(CbbTerminalTypeEnums.VDI_ANDROID, terminalType);
    }
}
