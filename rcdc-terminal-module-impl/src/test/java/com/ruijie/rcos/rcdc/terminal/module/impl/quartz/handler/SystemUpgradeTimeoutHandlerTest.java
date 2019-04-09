package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
public class SystemUpgradeTimeoutHandlerTest {

    @Tested
    private SystemUpgradeTimeoutHandler handler;

    @Injectable
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    /**
     * 测试execute，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testExecuteArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.execute(null), "upgradeTerminalList can not be null");
        assertTrue(true);
    }

    /**
     * 测试execute，upgradeTerminalList为空
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteUpgradeTerminalListIsEmpty() throws BusinessException {
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        handler.execute(upgradeTerminalList);
        new Verifications() {
            {
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试execute，状态不是upgrading
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStateIsNotUpgrading() throws BusinessException {
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        upgradeTerminalList.add(upgradeTerminal);

        handler.execute(upgradeTerminalList);
        new Verifications() {
            {
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试execute，超时
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteTimeout() throws BusinessException {
        new MockUp<TerminalDateUtil>() {
            @Mock
            public boolean isTimeout(Date date, int timeoutSecond) {
                return true;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeTerminalList.add(upgradeTerminal);

        handler.execute(upgradeTerminalList);
        new Verifications() {
            {
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 1;
            }
        };
    }

    /**
     * 测试execute，超时,BusinessException
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteTimeoutHasBusinessException() throws BusinessException {
        new MockUp<TerminalDateUtil>() {
            @Mock
            public boolean isTimeout(Date date, int timeoutSecond) {
                return true;
            }
        };

        new Expectations() {
            {
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                result = new BusinessException("key");
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeTerminalList.add(upgradeTerminal);

        handler.execute(upgradeTerminalList);
        new Verifications() {
            {
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 1;
            }
        };
    }

    /**
     * 测试execute，未超时
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteNotTimeout() throws BusinessException {
        new MockUp<TerminalDateUtil>() {
            @Mock
            public boolean isTimeout(Date date, int timeoutSecond) {
                return false;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeTerminalList.add(upgradeTerminal);

        handler.execute(upgradeTerminalList);
        new Verifications() {
            {
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }

}
