package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
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
public class SystemUpgradeStateSynctHandlerTest {

    @Tested
    private SystemUpgradeStateSynctHandler handler;
    
    @Injectable
    private TerminalSystemUpgradePackageService systemUpgradePackageService;

    @Injectable
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;
    
    /**
     * 测试execute，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testExecuteArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.execute(null),
                "upgradeTerminalList can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试execute，readSystemUpgradeSuccessStateFromFile，BusinessException
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteReadSystemUpgradeSuccessStateFromFileHasBusinessException() throws BusinessException {
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                result = new BusinessException("key");
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试execute，systemUpgradeInfoList为null
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteSystemUpgradeInfoListIsNull() throws BusinessException {
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                result = null;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试execute，upgradeInfoList为空
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteUpgradeInfoListIsEmpty() throws BusinessException {
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = new ArrayList<>();
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                result = systemUpgradeInfoList;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试execute，systemUpgradeInfoList为空,无匹配终端升级状态信息
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteSystemUpgradeInfoListIsEmpty() throws BusinessException {
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = new ArrayList<>();
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                result = systemUpgradeInfoList;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试execute，systemUpgradeInfoList不为空,无匹配终端升级状态信息
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteNoMatchTerminal() throws BusinessException {
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = new ArrayList<>();
        TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
        upgradeInfo.setTerminalId("1");
        systemUpgradeInfoList.add(upgradeInfo);
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                result = systemUpgradeInfoList;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setTerminalId("2");
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试execute，systemUpgradeInfoList不为空,有匹配终端升级状态信息
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteMatchTerminal() throws BusinessException {
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = new ArrayList<>();
        TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
        upgradeInfo.setTerminalId("1");
        systemUpgradeInfoList.add(upgradeInfo);
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                result = systemUpgradeInfoList;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setTerminalId("1");
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeSuccessStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 1;
            }
        };
    }

}
