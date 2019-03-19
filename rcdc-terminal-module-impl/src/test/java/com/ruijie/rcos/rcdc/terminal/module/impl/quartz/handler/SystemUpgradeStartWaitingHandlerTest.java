package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    
    /**
     * 测试execute，参数为空
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
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingUpgradeTerminalListIsEmpty() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                result = 1;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        handler.execute(upgradeTerminalList, upgradePackageId);
        
        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 0;
                systemUpgradeServiceTx.startTerminalUpgrade((UUID) any, anyString);
                times = 0;
                
            }
        };
    }
    
    
    /**
     * 测试execute，开始等待中的刷机终端,getSystemUpgradePackage,BusinessException
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
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                times = 0;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 0;
                systemUpgradeServiceTx.startTerminalUpgrade((UUID) any, anyString);
                times = 0;
                
            }
        };
    }
    
    /**
     * 测试execute，开始等待中的刷机终端,超过最大同时刷机数
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingGreaterThanUpgradeMaxNum() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                result = 51;
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
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 0;
                systemUpgradeServiceTx.startTerminalUpgrade((UUID) any, anyString);
                times = 0;
                
            }
        };
    }
    
    /**
     * 测试execute，开始等待中的刷机终端,不是wait状态的终端
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingUpgradeStateIsNotWait() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                result = 1;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList, upgradePackageId);
        
        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 0;
                systemUpgradeServiceTx.startTerminalUpgrade((UUID) any, anyString);
                times = 0;
                
            }
        };
    }
    
    /**
     * 测试execute，开始等待中的刷机终端,系统刷机失败
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingSystemUpgradeFail() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                result = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                result = new BusinessException("key");
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList, upgradePackageId);
        
        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 1;
                systemUpgradeServiceTx.startTerminalUpgrade((UUID) any, anyString);
                times = 0;
                
            }
        };
    }
    
    /**
     * 测试execute，开始等待中的刷机终端,系统刷机
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteStartWaitingSystemUpgrade() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                result = upgradePackage;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                result = 1;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList, upgradePackageId);
        
        new Verifications() {
            {
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);
                times = 1;
                systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);
                times = 1;
                systemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 1;
                systemUpgradeServiceTx.startTerminalUpgrade((UUID) any, anyString);
                times = 1;
                
            }
        };
    }
}
