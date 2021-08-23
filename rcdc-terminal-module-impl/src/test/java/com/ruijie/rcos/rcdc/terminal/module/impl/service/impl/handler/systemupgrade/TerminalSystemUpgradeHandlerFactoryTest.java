package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/15
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class TerminalSystemUpgradeHandlerFactoryTest {

    @Tested
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Injectable
    private LinuxVDISystemUpgradeHandler linuxVDISystemUpgradeHandler;

    @Injectable
    private SystemOtaUpgradeHandler systemOtaUpgradeHandler;

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
        TerminalSystemUpgradeHandler handler = handlerFactory.getHandler(TerminalTypeArchType.LINUX_VDI_X86);
        Assert.assertTrue(handler instanceof LinuxVDISystemUpgradeHandler);
    }

    /**
     * 测试获取版本请求-终端类型不支持
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetHandlerWhileTerminalTypeNotSupport() {

        try {
            handlerFactory.getHandler(TerminalTypeArchType.LINUX_IDV_ARM);
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_HANDLER_NOT_EXIST, e.getKey());
        }
    }

}
