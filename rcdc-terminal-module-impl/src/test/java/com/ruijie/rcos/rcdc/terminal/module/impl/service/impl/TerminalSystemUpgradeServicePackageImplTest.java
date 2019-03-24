package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
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
 * Create Time: 2019年2月28日
 * 
 * @author ls
 */
public class TerminalSystemUpgradeServicePackageImplTest {

    @Tested
    private TerminalSystemUpgradeServicePackageImpl servicePackageImpl;
    
    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;
    
    /**
     * 测试saveTerminalUpgradePackage，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testSaveTerminalUpgradePackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> servicePackageImpl.saveTerminalUpgradePackage(null),
                "terminalUpgradeVersionFileInfo 不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试saveTerminalUpgradePackage，upgradePackage为空
     * @throws BusinessException 异常
     */
    @Test
    public void testSaveTerminalUpgradePackageUpgradePackageIsNull() throws BusinessException {
        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType(versionInfo.getPackageType());
                result = null;
            }
        };
        servicePackageImpl.saveTerminalUpgradePackage(versionInfo);
        
        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType(versionInfo.getPackageType());
                times = 1;
                termianlSystemUpgradePackageDAO.save((TerminalSystemUpgradePackageEntity)any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试saveTerminalUpgradePackage
     * @throws BusinessException 异常
     */
    @Test
    public void testSaveTerminalUpgradePackage() throws BusinessException {
        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType(versionInfo.getPackageType());
                result = upgradePackage;
            }
        };
        servicePackageImpl.saveTerminalUpgradePackage(versionInfo);
        assertEquals(versionInfo.getPackageName(), upgradePackage.getPackageName());
        assertEquals(versionInfo.getImgName(), upgradePackage.getImgName());
        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType(versionInfo.getPackageType());
                times = 1;
                termianlSystemUpgradePackageDAO.save(upgradePackage);
                times = 1;
            }
        };
    }
    
    /**
     * 测试readSystemUpgradeSuccessStateFromFile，目录不存在
     */
    @Test
    public void testReadSystemUpgradeSuccessStateFromFileDirectoryNotExist() {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }
        };
        try {
            servicePackageImpl.readSystemUpgradeSuccessStateFromFile();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_SUCCESS_STATUS_DIRECTORY_NOT_EXIST, e.getKey());
        }
    }
    
    /**
     * 测试readSystemUpgradeSuccessStateFromFile，
     * @throws BusinessException 异常
     */
    @Test
    public void testReadSystemUpgradeSuccessStateFromFile() throws BusinessException {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                String[] stringArr = new String[2];
                stringArr[0] = "dsds";
                stringArr[1] = "gggg.ss";
                return stringArr;
            }
        };
        List<TerminalSystemUpgradeInfo> upgradeInfoList = servicePackageImpl.readSystemUpgradeSuccessStateFromFile();
        assertEquals("dsds", upgradeInfoList.get(0).getTerminalId());
        assertEquals(CbbSystemUpgradeStateEnums.SUCCESS, upgradeInfoList.get(0).getState());
        assertEquals("gggg", upgradeInfoList.get(1).getTerminalId());
        assertEquals(CbbSystemUpgradeStateEnums.SUCCESS, upgradeInfoList.get(1).getState());
    }
    
    /**
     * 测试readSystemUpgradeStartStateFromFile，目录不存在
     */
    @Test
    public void testReadSystemUpgradeStartStateFromFileDirectoryNotExist() {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }
        };
        try {
            servicePackageImpl.readSystemUpgradeStartStateFromFile();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_SUCCESS_STATUS_DIRECTORY_NOT_EXIST, e.getKey());
        }
    }
    
    /**
     * 测试readSystemUpgradeStartStateFromFile，
     * @throws BusinessException 异常
     */
    @Test
    public void testReadSystemUpgradeStartStateFromFile() throws BusinessException {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
            
            @Mock
            public String[] list() {
                String[] stringArr = new String[2];
                stringArr[0] = "dsds";
                stringArr[1] = "gggg.ss";
                return stringArr;
            }
        };
        List<TerminalSystemUpgradeInfo> upgradeInfoList = servicePackageImpl.readSystemUpgradeStartStateFromFile();
        assertEquals("dsds", upgradeInfoList.get(0).getTerminalId());
        assertEquals(CbbSystemUpgradeStateEnums.UPGRADING, upgradeInfoList.get(0).getState());
        assertEquals("gggg", upgradeInfoList.get(1).getTerminalId());
        assertEquals(CbbSystemUpgradeStateEnums.UPGRADING, upgradeInfoList.get(1).getState());
    }

    /**
     * 测试getSystemUpgradePackage，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testGetSystemUpgradePackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> servicePackageImpl.getSystemUpgradePackage(null),
                "upgradePackage");
        assertTrue(true);
    }
    
    /**
     * 测试getSystemUpgradePackage，upgradePackageOpt为空
     */
    @Test
    public void testGetSystemUpgradePackageUpgradePackageOptIsNull() {
        UUID upgradePackageId = UUID.randomUUID();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findById(upgradePackageId);
                result = Optional.empty();
            }
        };
        try {
            servicePackageImpl.getSystemUpgradePackage(upgradePackageId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST, e.getKey());
        }
    }
    
    /**
     * 测试getSystemUpgradePackage，
     * @throws BusinessException 异常
     */
    @Test
    public void testGetSystemUpgradePackage() throws BusinessException {
        UUID upgradePackageId = UUID.randomUUID();
        Optional<TerminalSystemUpgradePackageEntity> upgradePackageOpt = Optional.of(new TerminalSystemUpgradePackageEntity());
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findById(upgradePackageId);
                result = upgradePackageOpt;
            }
        };
        assertEquals(upgradePackageOpt.get(), servicePackageImpl.getSystemUpgradePackage(upgradePackageId));
        
        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findById(upgradePackageId);
                times = 1;
            }
        };
    }
}
