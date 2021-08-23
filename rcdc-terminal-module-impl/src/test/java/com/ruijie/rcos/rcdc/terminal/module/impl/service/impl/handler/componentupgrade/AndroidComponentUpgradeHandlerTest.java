package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
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
public class AndroidComponentUpgradeHandlerTest {

    @Tested
    private AndroidComponentUpgradeHandler handler;

    @Test
    public void testGetTerminalType() {
        TerminalOsArchType osArchType = handler.getTerminalOsArchType();
        Assert.assertEquals(TerminalOsArchType.ANDROID_ARM, osArchType);
    }
}
