package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
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
 * Create Time: 2019年2月25日
 * 
 * @author ls
 */
public class SystemUpgradeStartConfirmHandlerTest {

    @Tested
    private SystemUpgradeStartConfirmHandler handler;
    
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
     * 测试execute，systemUpgradeInfoList为null,存在非进行中的刷机终端
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteSystemUpgradeInfoListIsNull() throws BusinessException {
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = null;
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                result = systemUpgradeInfoList;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                times = 1;
            }
        };
    }
    
    /**
     * 测试execute，获取systemUpgradeInfoList时抛出BusinessException
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteGetSystemUpgradeInfoListHasBusinessException() throws BusinessException {
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                result = new BusinessException("key");
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试execute，systemUpgradeInfoList不为null,存在匹配的升级中的终端
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteHasUpgradeTerminal() throws BusinessException {
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = new ArrayList<>();
        TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
        upgradeInfo.setTerminalId("1");
        systemUpgradeInfoList.add(upgradeInfo);
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                result = systemUpgradeInfoList;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setTerminalId("1");
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试execute，systemUpgradeInfoList不为null,存在匹配的升级中的终端,no timeout
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteHasUpgradeTerminalAndNotTimeout() throws BusinessException {
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = new ArrayList<>();
        TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
        upgradeInfo.setTerminalId("2");
        systemUpgradeInfoList.add(upgradeInfo);
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                result = systemUpgradeInfoList;
            }
        };
        new MockUp<TerminalDateUtil>() {
            @Mock
            public boolean isTimeout(Date date, int timeoutSecond) {
                return false;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setTerminalId("1");
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试execute，systemUpgradeInfoList不为null,不存在匹配的升级中的终端,timeout
     * @throws BusinessException 异常
     */
    @Test
    public void testExecuteNoUpgradeTerminalAndTimeout() throws BusinessException {
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = new ArrayList<>();
        TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
        upgradeInfo.setTerminalId("2");
        systemUpgradeInfoList.add(upgradeInfo);
        new Expectations() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                result = systemUpgradeInfoList;
            }
        };
        new MockUp<TerminalDateUtil>() {
            @Mock
            public boolean isTimeout(Date date, int timeoutSecond) {
                return true;
            }
        };
        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setTerminalId("1");
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeTerminalList.add(upgradeTerminal);
        handler.execute(upgradeTerminalList);
        
        new Verifications() {
            {
                systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
                times = 1;
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 1;
            }
        };
    }

}
