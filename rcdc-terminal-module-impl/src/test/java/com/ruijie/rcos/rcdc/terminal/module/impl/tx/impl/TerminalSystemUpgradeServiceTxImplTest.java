package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbAddSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

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

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Injectable
    private TerminalSystemUpgradeTerminalGroupDAO systemUpgradeTerminalGroupDAO;

    /**
     * 测试addSystemUpgradeTask，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.addSystemUpgradeTask(null, null), "upgradePackage can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.addSystemUpgradeTask(new TerminalSystemUpgradePackageEntity(), null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试addSystemUpgradeTask
     */
    @Test
    public void testAddSystemUpgradeTask() {
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setPackageType(CbbTerminalTypeEnums.APP_LINUX);
        String[] terminalIdArr = new String[1];
        terminalIdArr[0] = "1";
        CbbAddSystemUpgradeTaskDTO request = new CbbAddSystemUpgradeTaskDTO();
        request.setTerminalIdArr(terminalIdArr);
        request.setPackageId(UUID.randomUUID());
        request.setTerminalGroupIdArr(new UUID[]{UUID.randomUUID()});
        serviceTxImpl.addSystemUpgradeTask(upgradePackage, request);

        new Verifications() {
            {
                systemUpgradeDAO.save((TerminalSystemUpgradeEntity) any);
                times = 1;
            }
        };
    }

    /**
     * 测试addSystemUpgradeTask
     */
    @Test
    public void testAddSystemUpgradeTask2() {
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setPackageType(CbbTerminalTypeEnums.APP_LINUX);
        String[] terminalIdArr = new String[1];
        terminalIdArr[0] = "1";
        CbbAddSystemUpgradeTaskDTO request = new CbbAddSystemUpgradeTaskDTO();
        request.setTerminalIdArr(terminalIdArr);
        request.setPackageId(UUID.randomUUID());
        request.setTerminalGroupIdArr(new UUID[]{UUID.randomUUID()});
        List<TerminalEntity> terminalEntityList = new ArrayList<>();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123");
        terminalEntityList.add(entity);
        new Expectations() {
            {
                basicInfoDAO.findByGroupIdAndAuthModeAndTerminalOsType((UUID) any, (CbbTerminalPlatformEnums) any, anyString);
                result = terminalEntityList;

            }
        };
        serviceTxImpl.addSystemUpgradeTask(upgradePackage, request);

        new Verifications() {
            {
                systemUpgradeDAO.save((TerminalSystemUpgradeEntity) any);
                times = 1;
            }
        };
    }

    /**
     * 测试closeSystemUpgradeTask，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testCloseSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.closeSystemUpgradeTask(null), "upgradeTaskId can not be null");
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
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeTaskOffNoStartterminal() throws BusinessException {
        UUID upgradeTaskId = UUID.randomUUID();
        TerminalSystemUpgradeEntity systemUpgradeTask = new TerminalSystemUpgradeEntity();
        systemUpgradeTask.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        systemUpgradeTask.setId(upgradeTaskId);
        systemUpgradeTask.setUpgradePackageId(upgradeTaskId);
        systemUpgradeTask.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        List<TerminalSystemUpgradeTerminalEntity> waitUpgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setSysUpgradeId(upgradeTaskId);
        upgradeTerminal.setId(upgradeTaskId);
        upgradeTerminal.setTerminalId("1");
        waitUpgradeTerminalList.add(upgradeTerminal);
        new Expectations() {
            {
                systemUpgradeDAO.findById((UUID) any);
                result = Optional.of(systemUpgradeTask);
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
                result = waitUpgradeTerminalList;
            }
        };
        new MockUp<TerminalSystemUpgradeServiceTxImpl>() {
            @Mock
            public void modifySystemUpgradeTerminalState(UUID upgradeTaskId, String terminalId, CbbSystemUpgradeStateEnums state) {

            }
        };
        serviceTxImpl.closeSystemUpgradeTask(upgradeTaskId);

        new Verifications() {
            {
                systemUpgradeDAO.findById((UUID) any);
                times = 1;
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
                times = 1;
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeDAO.save(systemUpgradeTask);
                times = 1;
            }
        };
    }

    /**
     * 测试closeSystemUpgradeTask，将进行中的刷机任务设置为关闭中状态
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeTaskOffRunningTerminal() throws BusinessException {
        UUID upgradeTaskId = UUID.randomUUID();
        TerminalSystemUpgradeEntity systemUpgradeTask = new TerminalSystemUpgradeEntity();
        systemUpgradeTask.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        systemUpgradeTask.setUpgradePackageId(upgradeTaskId);
        systemUpgradeTask.setId(upgradeTaskId);
        systemUpgradeTask.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);

        List<TerminalSystemUpgradeTerminalEntity> waitUpgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setSysUpgradeId(upgradeTaskId);
        upgradeTerminal.setId(upgradeTaskId);
        upgradeTerminal.setTerminalId("1");
        waitUpgradeTerminalList.add(upgradeTerminal);
        new Expectations() {
            {
                systemUpgradeDAO.findById((UUID) any);
                result = Optional.of(systemUpgradeTask);
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
                result = waitUpgradeTerminalList;
            }
        };
        new MockUp<TerminalSystemUpgradeServiceTxImpl>() {
            @Mock
            public void modifySystemUpgradeTerminalState(UUID upgradeTaskId, String terminalId, CbbSystemUpgradeStateEnums state) {

            }
        };
        serviceTxImpl.closeSystemUpgradeTask(upgradeTaskId);

        new Verifications() {
            {
                systemUpgradeDAO.findById((UUID) any);
                times = 1;
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
                times = 1;
                systemUpgradeDAO.save(systemUpgradeTask);
                times = 1;
            }
        };
    }

    /**
     * 测试modifySystemUpgradeTerminalState，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeTerminalStateArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.modifySystemUpgradeTerminalState(null),
                "upgradeTerminal can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(
                () -> serviceTxImpl.modifySystemUpgradeTerminalState(new TerminalSystemUpgradeTerminalEntity()), "upgradeTaskId can not be null");

        TerminalSystemUpgradeTerminalEntity entity = new TerminalSystemUpgradeTerminalEntity();

        entity.setSysUpgradeId(UUID.randomUUID());
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.modifySystemUpgradeTerminalState(entity),
                "terminalId can not be blank");

        entity.setTerminalId("1");
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.modifySystemUpgradeTerminalState(entity), "state can not be null");
        assertTrue(true);
    }

    /**
     * 测试modifySystemUpgradeTerminalState，getUpgradeTerminalEntity有BusinessException
     */
    @Test
    public void testModifySystemUpgradeTerminalStateGetUpgradeTerminalEntityHasBusinessException() {
        UUID upgradeTaskId = UUID.randomUUID();
        String terminalId = "1";

        TerminalSystemUpgradeTerminalEntity entity = new TerminalSystemUpgradeTerminalEntity();
        entity.setSysUpgradeId(upgradeTaskId);
        entity.setTerminalId(terminalId);
        entity.setState(CbbSystemUpgradeStateEnums.UNDO);
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                result = null;
            }
        };
        try {
            serviceTxImpl.modifySystemUpgradeTerminalState(entity);
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
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testModifySystemUpgradeTerminalState() throws BusinessException {
        UUID upgradeTaskId = UUID.randomUUID();
        String terminalId = "1";

        TerminalSystemUpgradeTerminalEntity entity = new TerminalSystemUpgradeTerminalEntity();
        entity.setSysUpgradeId(upgradeTaskId);
        entity.setTerminalId(terminalId);
        entity.setState(CbbSystemUpgradeStateEnums.UNDO);
        entity.setStartTime(new Date());
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                result = upgradeTerminal;
            }
        };
        serviceTxImpl.modifySystemUpgradeTerminalState(entity);

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                times = 1;
            }
        };
    }

    /**
     * 测试startTerminalUpgrade，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testStartTerminalUpgradeArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceTxImpl.startTerminalUpgrade(null), "upgradeTerminal can not be null");
        assertTrue(true);
    }

    /**
     * 测试startTerminalUpgrade，
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testStartTerminalUpgrade() throws BusinessException {
        UUID upgradeTaskId = UUID.randomUUID();
        String terminalId = "1";

        TerminalSystemUpgradeTerminalEntity entity = new TerminalSystemUpgradeTerminalEntity();
        entity.setSysUpgradeId(upgradeTaskId);
        entity.setTerminalId(terminalId);
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                result = upgradeTerminal;
            }
        };
        serviceTxImpl.startTerminalUpgrade(entity);

        new Verifications() {
            {
                systemUpgradeTerminalDAO.save(upgradeTerminal);
                times = 1;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);
                times = 1;
            }
        };
    }

    /**
     * 测试editUpgradeGroup
     */
    @Test
    public void testEditUpgradeGroup() {
        TerminalSystemUpgradeEntity systemUpgradeEntity = new TerminalSystemUpgradeEntity();
        systemUpgradeEntity.setId(UUID.randomUUID());
        UUID[] terminalGroupIdArr = new UUID[]{UUID.randomUUID()};
        new Expectations() {
            {
                systemUpgradeTerminalGroupDAO.deleteBySysUpgradeId(systemUpgradeEntity.getId());
            }
        };
        serviceTxImpl.editUpgradeGroup(systemUpgradeEntity, terminalGroupIdArr);
        new Verifications() {
            {
                systemUpgradeTerminalGroupDAO.deleteBySysUpgradeId(systemUpgradeEntity.getId());
                times = 1;
            }
        };


    }
}
