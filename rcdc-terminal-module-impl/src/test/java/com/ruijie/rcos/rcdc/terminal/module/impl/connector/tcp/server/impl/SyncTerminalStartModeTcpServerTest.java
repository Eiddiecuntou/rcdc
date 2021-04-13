package com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.server.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalStartMode;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.fail;

/**
 * Description: ShineRequestPartTypeSPIImpl
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/2/6
 *
 * @author nting
 */
@RunWith(SkyEngineRunner.class)
public class SyncTerminalStartModeTcpServerTest {

    @Tested
    private SyncTerminalStartModeTcpServerImpl impl;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    /**
     * testHandle
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testHandle() throws BusinessException {
        String terminalId = "123";
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setIp("aaa");
        terminalEntity.setStartMode(CbbTerminalStartMode.AUTO);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = terminalEntity;
            }
        };

        String result = impl.handle(terminalId);
        Assert.assertEquals(CbbTerminalStartMode.AUTO.getMode(), result);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 1;
            }
        };
    }

    /**
     * testHandle
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testHandleTerminalEntityNotExist() throws BusinessException {
        String terminalId = "123";
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setIp("aaa");
        terminalEntity.setStartMode(CbbTerminalStartMode.AUTO);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = null;
            }
        };

        try {
            impl.handle(terminalId);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL, e.getKey());
        }

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 1;
            }
        };
    }

}
