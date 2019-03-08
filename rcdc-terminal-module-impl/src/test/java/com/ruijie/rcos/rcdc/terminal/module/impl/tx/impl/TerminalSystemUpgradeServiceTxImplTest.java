package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
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
public class TerminalSystemUpgradeServiceTxImplTest {

    @Tested
    private TerminalSystemUpgradeServiceTxImpl serviceTxImpl;
    
    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private TerminalBasicInfoService basicInfoService;
    
    /**
     * 测试addSystemUpgradeTask，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testAddSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.addSystemUpgradeTask(null, new String[5]),
                "upgradePackage can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.addSystemUpgradeTask
                (new TerminalSystemUpgradePackageEntity(), null), "terminalIdArr can not be empty");
        assertTrue(true);
    }
    
    /**
     * 测试addSystemUpgradeTask
     */
    @Test
    public void testAddSystemUpgradeTask() {
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        String[] terminalIdArr = new String[1];
        terminalIdArr[0] = "1";
        serviceTxImpl.addSystemUpgradeTask(upgradePackage, terminalIdArr);
        
        new Verifications() {
            {
                systemUpgradeDAO.save((TerminalSystemUpgradeEntity)any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试closeSystemUpgradeTask，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testCloseSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.closeSystemUpgradeTask(null),
                "upgradeTaskId can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试closeSystemUpgradeTask，BusinessException
     */
    @Test
    public void testCloseSystemUpgradeTaskHasBusinessException() {
        UUID upgradeTaskId = UUID.randomUUID();
        new Expectations() {
            {
                systemUpgradeDAO.findById((UUID) any);
                result = Optional.empty();
            }
        };
        try {
            serviceTxImpl.closeSystemUpgradeTask(upgradeTaskId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST, e.getKey());
        }
    }
    
    /**
     * 测试closeSystemUpgradeTask，关闭未开始的刷机终端
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeTaskOffNoStartterminal() throws BusinessException {
        UUID upgradeTaskId = UUID.randomUUID();
        TerminalSystemUpgradeEntity systemUpgradeTask = new TerminalSystemUpgradeEntity();
        List<TerminalSystemUpgradeTerminalEntity> waitUpgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        waitUpgradeTerminalList.add(upgradeTerminal);
        new Expectations() {
            {
                systemUpgradeDAO.findById((UUID) any);
                result = Optional.of(systemUpgradeTask);
                systemUpgradeTerminalDAO
                .findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
                result = waitUpgradeTerminalList;
            }
        };
        new MockUp<TerminalSystemUpgradeServiceTxImpl>() {
            @Mock
            public void modifySystemUpgradeTerminalState(UUID upgradeTaskId, String terminalId,
                    CbbSystemUpgradeStateEnums state) {
                
            }
        };
        serviceTxImpl.closeSystemUpgradeTask(upgradeTaskId);
        
        new Verifications() {
            {
                systemUpgradeDAO.findById((UUID) any);
                times = 1;
                systemUpgradeTerminalDAO
                .findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
                times = 1;
                systemUpgradeDAO.save(systemUpgradeTask);
                times = 0;
            }
        };
    }
    
    /**
     * 测试closeSystemUpgradeTask，将进行中的刷机任务设置为关闭中状态
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeTaskOffRunningTerminal() throws BusinessException {
        UUID upgradeTaskId = UUID.randomUUID();
        TerminalSystemUpgradeEntity systemUpgradeTask = new TerminalSystemUpgradeEntity();
        systemUpgradeTask.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        
        List<TerminalSystemUpgradeTerminalEntity> waitUpgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        waitUpgradeTerminalList.add(upgradeTerminal);
        new Expectations() {
            {
                systemUpgradeDAO.findById((UUID) any);
                result = Optional.of(systemUpgradeTask);
                systemUpgradeTerminalDAO
                .findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
                result = waitUpgradeTerminalList;
            }
        };
        new MockUp<TerminalSystemUpgradeServiceTxImpl>() {
            @Mock
            public void modifySystemUpgradeTerminalState(UUID upgradeTaskId, String terminalId,
                    CbbSystemUpgradeStateEnums state) {
                
            }
        };
        serviceTxImpl.closeSystemUpgradeTask(upgradeTaskId);
        
        new Verifications() {
            {
                systemUpgradeDAO.findById((UUID) any);
                times = 1;
                systemUpgradeTerminalDAO
                .findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
                times = 1;
                systemUpgradeDAO.save(systemUpgradeTask);
                times = 1;
            }
        };
    }

    /**
     * 测试modifySystemUpgradeTerminalState，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeTerminalStateArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.modifySystemUpgradeTerminalState
                (null, "1", CbbSystemUpgradeStateEnums.UPGRADING), "upgradeTaskId can not be blank");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.modifySystemUpgradeTerminalState
                (UUID.randomUUID(), "", CbbSystemUpgradeStateEnums.UPGRADING), "terminalId can not be blank");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.modifySystemUpgradeTerminalState
                (UUID.randomUUID(), "1", null), "state can not be blank");
        assertTrue(true);
    }
    
    /**
     * 测试modifySystemUpgradeTerminalState，getUpgradeTerminalEntity有BusinessException
     */
    @Test
    public void testModifySystemUpgradeTerminalStateGetUpgradeTerminalEntityHasBusinessException() {
        UUID upgradeTaskId = UUID.randomUUID();
        String terminalId = "1";
        
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                result = null;
            }
        };
        try {
            serviceTxImpl.modifySystemUpgradeTerminalState(upgradeTaskId, terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NOT_EXIST, e.getKey());
        }
        
        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                times = 1;
            }
        };
    }
    
    /**
     * 测试modifySystemUpgradeTerminalState，
     * @throws BusinessException 异常
     */
    @Test
    public void testModifySystemUpgradeTerminalState() throws BusinessException {
        UUID upgradeTaskId = UUID.randomUUID();
        String terminalId = "1";
        
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                result = upgradeTerminal;
            }
        };
        serviceTxImpl.modifySystemUpgradeTerminalState(upgradeTaskId, terminalId, CbbSystemUpgradeStateEnums.UNDO);
        
        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                times = 1;
            }
        };
    }

    /**
     * 测试startTerminalUpgrade，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testStartTerminalUpgradeArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.startTerminalUpgrade
                (null, "1"), "upgradeTaskId can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.startTerminalUpgrade
                (UUID.randomUUID(), ""), "terminalId can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试startTerminalUpgrade，
     * @throws BusinessException 异常
     */
    @Test
    public void testStartTerminalUpgrade() throws BusinessException {
        UUID upgradeTaskId = UUID.randomUUID();
        String terminalId = "1";
        
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                result = upgradeTerminal;
            }
        };
        serviceTxImpl.startTerminalUpgrade(upgradeTaskId, terminalId);
        
        new Verifications() {
            {
                systemUpgradeTerminalDAO.save(upgradeTerminal);
                times = 1;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                times = 1;
            }
        };
    }
}
