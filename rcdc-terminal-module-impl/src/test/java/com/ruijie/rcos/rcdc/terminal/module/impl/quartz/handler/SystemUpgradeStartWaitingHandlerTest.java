package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.UpgradeTerminalLockManager;
import jdk.nashorn.internal.ir.Terminal;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
public class SystemUpgradeStartWaitingHandlerTest {

    @Tested
    private SystemUpgradeStartWaitingHandler handler;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private TerminalSystemUpgradeService systemUpgradeService;

    @Injectable
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    @Injectable
    private TerminalSystemUpgradePackageService systemUpgradePackageService;

    @Injectable
    private UpgradeTerminalLockManager lockManager;

    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    /**
     * 测试execute，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testExecuteArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.execute(null, UUID.randomUUID()),
                "upgradeTerminalList can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.execute(new ArrayList<>(), null),
                "upgradePackageId can not be null");
        assertTrue(true);
    }

    /**
     * 测试execute，开始等待中的刷机终端,upgradeTerminalList为空
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingUpgradeTerminalListIsEmpty() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        List<TerminalSystemUpgradeEntity> upgradingTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity systemUpgrade = new TerminalSystemUpgradeEntity();
        systemUpgrade.setId(UUID.randomUUID());
        upgradingTaskList.add(systemUpgrade);
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeDAO
                        .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                result = 1;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        handler.execute(upgradeTerminalList, upgradePackageId);

        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 0;
                systemUpgradeServiceTx.startTerminalUpgrade((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;

            }
        };
    }


    /**
     * 测试execute，开始等待中的刷机终端,getSystemUpgradePackage,BusinessException
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingGetSystemUpgradePackageHasBusinessException() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = new BusinessException("key");
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        handler.execute(upgradeTerminalList, upgradePackageId);

        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 0;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 0;
                systemUpgradeServiceTx.startTerminalUpgrade((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;

            }
        };
    }

    /**
     * 测试execute，开始等待中的刷机终端,超过最大同时刷机数
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingGreaterThanUpgradeMaxNum() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        List<TerminalSystemUpgradeEntity> upgradingTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity systemUpgrade = new TerminalSystemUpgradeEntity();
        systemUpgrade.setId(UUID.randomUUID());
        upgradingTaskList.add(systemUpgrade);
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeDAO
                        .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                result = 61;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList, upgradePackageId);

        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 0;
                systemUpgradeServiceTx.startTerminalUpgrade((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;

            }
        };
    }

    /**
     * 测试execute，开始等待中的刷机终端,不是wait状态的终端
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingUpgradeStateIsNotWait() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        upgradeTerminal.setTerminalId("123");
        upgradeTerminalList.add(upgradeTerminal);
        List<TerminalSystemUpgradeEntity> upgradingTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity systemUpgrade = new TerminalSystemUpgradeEntity();
        systemUpgrade.setId(UUID.randomUUID());
        upgradingTaskList.add(systemUpgrade);
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeDAO
                        .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId((UUID) any, anyString);
                result = upgradeTerminal;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                result = 1;
                lockManager.getAndCreateLock(anyString);
                result = new ReentrantLock();
            }
        };

        handler.execute(upgradeTerminalList, upgradePackageId);

        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 0;
                systemUpgradeServiceTx.startTerminalUpgrade((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;

            }
        };
    }

    /**
     * 测试execute，开始等待中的刷机终端,系统刷机失败
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingSystemUpgradeFail() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);
        upgradeTerminal.setTerminalId("123");
        upgradeTerminalList.add(upgradeTerminal);
        List<TerminalSystemUpgradeEntity> upgradingTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity systemUpgrade = new TerminalSystemUpgradeEntity();
        systemUpgrade.setId(UUID.randomUUID());
        upgradingTaskList.add(systemUpgrade);

        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeDAO
                        .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                result = 1;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId((UUID) any, anyString);
                result = upgradeTerminal;
                lockManager.getAndCreateLock(anyString);
                result = new ReentrantLock();
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                result = new BusinessException("key");
            }
        };

        handler.execute(upgradeTerminalList, upgradePackageId);

        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 1;
                systemUpgradeServiceTx.startTerminalUpgrade((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;

            }
        };
    }

    /**
     * 测试execute，开始等待中的刷机终端,系统刷机
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingSystemUpgrade() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);
        upgradeTerminal.setTerminalId("123");
        upgradeTerminalList.add(upgradeTerminal);
        List<TerminalSystemUpgradeEntity> upgradingTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity systemUpgrade = new TerminalSystemUpgradeEntity();
        systemUpgrade.setId(UUID.randomUUID());
        upgradingTaskList.add(systemUpgrade);

        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeDAO
                        .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                result = 1;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId((UUID) any, anyString);
                result = upgradeTerminal;
                lockManager.getAndCreateLock(anyString);
                result = new ReentrantLock();
            }
        };

        handler.execute(upgradeTerminalList, upgradePackageId);

        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countBySysUpgradeIdAndState((UUID) any, CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 1;
                systemUpgradeServiceTx.startTerminalUpgrade((TerminalSystemUpgradeTerminalEntity) any);
                times = 1;

            }
        };
    }
}
