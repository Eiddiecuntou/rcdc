package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.Tested;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/12
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class TerminalComponentUpgradeHandlerFactoryTest {

    @Tested
    private TerminalComponentUpgradeHandlerFactory handlerFactory;

    /**
     *  获取版本升级处理器参数为空
     * @throws Exception 异常
     */
    @Test
    public void testGetHandlerArgIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> handlerFactory.getHandler( null),
                "osArchType can not be null");
        Assert.assertTrue(true);
    }

    /**
     * 测试获取版本升级处理器
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetHandler() throws BusinessException {
        TerminalComponentUpgradeHandler handler = handlerFactory.getHandler(TerminalOsArchType.LINUX_X86);
        Assert.assertTrue(handler instanceof LinuxComponentUpgradeHandler);
    }

    /**
     * 测试获取版本请求-终端类型不支持
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetHandlerWhileTerminalTypeNotSupport() {

        try {
            handlerFactory.getHandler(TerminalOsArchType.WINDOWS_ARM);
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_COMPONENT_UPGRADE_HANDLER_NOT_EXIST, e.getKey());
        }
    }
}
