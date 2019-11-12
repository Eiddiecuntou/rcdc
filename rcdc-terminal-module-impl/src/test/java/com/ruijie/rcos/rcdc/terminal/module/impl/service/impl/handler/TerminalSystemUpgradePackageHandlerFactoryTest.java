package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.AndroidVDISystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.LinuxVDISystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.Injectable;
import mockit.Tested;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/15
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class TerminalSystemUpgradePackageHandlerFactoryTest {

    @Tested
    private TerminalSystemUpgradePackageHandlerFactory handlerFactory;

    @Injectable
    private LinuxVDISystemUpgradePackageHandler linuxVDISystemUpgradeHandler;

    @Injectable
    private AndroidVDISystemUpgradePackageHandler androidVDISystemUpgradeHandler;

    /**
     * 获取版本升级处理器参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetHandlerArgIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> handlerFactory.getHandler(null), "terminal type can not be null");
        Assert.assertTrue(true);
    }

    /**
     * 测试获取版本升级处理器
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetHandler() throws BusinessException {

        Map<CbbTerminalTypeEnums, TerminalSystemUpgradePackageHandler> systemUpgradeHandlerHolder = Maps.newHashMap();
        handlerFactory.safeInit();
        TerminalSystemUpgradePackageHandler handler = handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_LINUX);
        Assert.assertEquals(handler, linuxVDISystemUpgradeHandler);
    }

    /**
     * 测试获取版本请求-终端类型不支持
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetHandlerWhileTerminalTypeNotSupport() {

        try {
            handlerFactory.getHandler(CbbTerminalTypeEnums.APP_ANDROID);
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_HANDLER_NOT_EXIST, e.getKey());
        }
    }

}
