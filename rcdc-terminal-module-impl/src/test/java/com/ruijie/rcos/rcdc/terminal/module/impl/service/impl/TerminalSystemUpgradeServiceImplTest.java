package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler.TerminalOffLineException;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.SystemUpgradeFileClearHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeResponseMsgHandler;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: 终端系统升级服务测试
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月29日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class TerminalSystemUpgradeServiceImplTest {

    @Tested
    private TerminalSystemUpgradeServiceImpl terminalSystemUpgradeService;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Injectable
    private TerminalSystemUpgradeResponseMsgHandler upgradeResponseMsgHandler;

    @Injectable
    private SystemUpgradeFileClearHandler upgradeFileClearHandler;


    /**
     * 测试发送系统升级指令
     * 
     * 
     * @param sender 模拟的消息发送对象
     * @throws BusinessException 业务异常
     * @throws IOException io异常
     * @throws InterruptedException 异常
     */
    @Test
    public void testSystemUpgrade(@Mocked DefaultRequestMessageSender sender) throws BusinessException, InterruptedException, IOException {
        String terminalId = "terminalId";
        TerminalSystemUpgradeMsg upgradeMsg = new TerminalSystemUpgradeMsg();

        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;

                sender.syncRequest((Message) any);

            }
        };

        terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);

        new Verifications() {
            {
                sessionManager.getRequestMessageSender(anyString);
                times = 1;

                sender.syncRequest((Message) any);
                times = 1;

            }
        };
    }

    /**
     * 测试发送系统升级指令当终端id为空时
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSystemUpgradeTerminalIdIsNull() throws BusinessException {
        String terminalId = null;
        TerminalSystemUpgradeMsg upgradeMsg = new TerminalSystemUpgradeMsg();

        try {
            terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);
            fail();
        } catch (Exception e) {
            Assert.assertEquals("terminalId 不能为空", e.getMessage());
        }
    }

    /**
     * 测试发送系统升级指令当发送消息为空时
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSystemUpgradeUpgradeMsgIsNull() throws BusinessException {
        String terminalId = "1111";
        TerminalSystemUpgradeMsg upgradeMsg = null;

        try {
            terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);
            fail();
        } catch (Exception e) {
            Assert.assertEquals("systemUpgradeMsg 不能为空", e.getMessage());
        }
    }


    /**
     * 测试发送系统升级指令当获取到的消息发送器为空时
     * 
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSystemUpgrade() throws BusinessException {
        String terminalId = "terminalId";
        TerminalSystemUpgradeMsg upgradeMsg = new TerminalSystemUpgradeMsg();

        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = new Exception();
            }
        };

        try {
            terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);
            fail();
        } catch (TerminalOffLineException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_OFFLINE, e.getKey());
        }

        new Verifications() {
            {
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试hasSystemUpgradeInProgress0，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testHasSystemUpgradeInProgress0ArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalSystemUpgradeService.hasSystemUpgradeInProgress(null),
                "upgradePackageId can not be null");
        assertTrue(true);
    }

    /**
     * 测试hasSystemUpgradeInProgress0，返回false
     */
    @Test
    public void testHasSystemUpgradeInProgress0IsFalse() {
        UUID upgradePackageId = UUID.randomUUID();
        new Expectations() {
            {
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = new ArrayList<>();
            }
        };
        assertFalse(terminalSystemUpgradeService.hasSystemUpgradeInProgress(upgradePackageId));

        new Verifications() {
            {
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
            }
        };
    }

    /**
     * 测试hasSystemUpgradeInProgress0，返回true
     */
    @Test
    public void testHasSystemUpgradeInProgress0IsTrue() {
        UUID upgradePackageId = UUID.randomUUID();
        List<TerminalSystemUpgradeEntity> upgradingTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(upgradeEntity);
        new Expectations() {
            {
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };
        assertTrue(terminalSystemUpgradeService.hasSystemUpgradeInProgress(upgradePackageId));

        new Verifications() {
            {
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackageId,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
            }
        };
    }

    /**
     * 测试modifySystemUpgradeState，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeStateArgumentIsNull() throws Exception {
        TerminalSystemUpgradeEntity idNullEntity = new TerminalSystemUpgradeEntity();
        TerminalSystemUpgradeEntity idNotNullEntity = new TerminalSystemUpgradeEntity();
        idNotNullEntity.setId(UUID.randomUUID());
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalSystemUpgradeService.modifySystemUpgradeState(idNullEntity),
                "upgradeTaskId 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalSystemUpgradeService.modifySystemUpgradeState(idNotNullEntity),
                "state 不能为空");
        assertTrue(true);
    }

    /**
     * 测试modifySystemUpgradeState，状态为非finish状态
     * 
     * @throws BusinessException 异常
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeStateIsNotFinish() throws BusinessException {
        UUID systemUpgradeId = UUID.randomUUID();
        TerminalSystemUpgradeEntity systemUpgradeEntity = new TerminalSystemUpgradeEntity();
        systemUpgradeEntity.setId(systemUpgradeId);
        systemUpgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);

        new MockUp<TerminalSystemUpgradeServiceImpl>() {
            @Mock
            public TerminalSystemUpgradeEntity getSystemUpgradeTask(UUID systemUpgradeId) {
                return systemUpgradeEntity;
            }
        };
        terminalSystemUpgradeService.modifySystemUpgradeState(systemUpgradeEntity);

        new Verifications() {
            {
                terminalSystemUpgradeDAO.save(systemUpgradeEntity);
                times = 1;
                upgradeFileClearHandler.clear(systemUpgradeEntity.getUpgradePackageId());
                times = 0;
            }
        };
    }

    /**
     * 测试modifySystemUpgradeState，状态为finish状态
     * 
     * @throws BusinessException 异常
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeStateIsCLOSING() throws BusinessException {
        UUID systemUpgradeId = UUID.randomUUID();
        TerminalSystemUpgradeEntity systemUpgradeEntity = new TerminalSystemUpgradeEntity();
        systemUpgradeEntity.setId(systemUpgradeId);
        systemUpgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.CLOSING);

        new MockUp<TerminalSystemUpgradeServiceImpl>() {
            @Mock
            public TerminalSystemUpgradeEntity getSystemUpgradeTask(UUID systemUpgradeId) {
                return systemUpgradeEntity;
            }
        };
        terminalSystemUpgradeService.modifySystemUpgradeState(systemUpgradeEntity);

        new Verifications() {
            {
                terminalSystemUpgradeDAO.save(systemUpgradeEntity);
                times = 1;
                upgradeFileClearHandler.clear(systemUpgradeEntity.getUpgradePackageId());
                times = 1;
            }
        };
    }

    /**
     * 测试getSystemUpgradeTask，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalSystemUpgradeService.getSystemUpgradeTask(null),
                "systemUpgradeId can not be null");
        assertTrue(true);
    }

    /**
     * 测试getSystemUpgradeTask，BusinessException
     */
    @Test
    public void testGetSystemUpgradeTaskHasBusinessException() {
        UUID systemUpgradeId = UUID.randomUUID();

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findById(systemUpgradeId);
                result = Optional.empty();
            }
        };
        try {
            terminalSystemUpgradeService.getSystemUpgradeTask(systemUpgradeId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试getSystemUpgradeTask，
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testGetSystemUpgradeTask() throws BusinessException {
        UUID systemUpgradeId = UUID.randomUUID();
        Optional<TerminalSystemUpgradeEntity> systemUpgradeOpt = Optional.of(new TerminalSystemUpgradeEntity());
        new Expectations() {
            {
                terminalSystemUpgradeDAO.findById(systemUpgradeId);
                result = systemUpgradeOpt;
            }
        };
        assertEquals(systemUpgradeOpt.get(), terminalSystemUpgradeService.getSystemUpgradeTask(systemUpgradeId));
        new Verifications() {
            {
                terminalSystemUpgradeDAO.findById(systemUpgradeId);
                times = 1;
            }
        };
    }
}
