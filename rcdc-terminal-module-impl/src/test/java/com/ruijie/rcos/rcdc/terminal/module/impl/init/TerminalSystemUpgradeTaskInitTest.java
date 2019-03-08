package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeSupportService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 * 
 * @author ls
 */
public class TerminalSystemUpgradeTaskInitTest {

    @Tested
    private TerminalSystemUpgradeTaskInit init;
    
    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Injectable
    private TerminalSystemUpgradePackageService systemUpgradePackageService;

    @Injectable
    private TerminalSystemUpgradeSupportService systemUpgradeSupportService;
    
    /**
     * 测试safeInit，无进行中的刷机任务
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitNoUpgradingTask() throws BusinessException {
        
        new Expectations() {
            {
                systemUpgradeDAO.findByStateInOrderByCreateTimeAsc((List<CbbSystemUpgradeTaskStateEnums>) any);
                result = new ArrayList<>();
            }
        };
        init.safeInit();
        
        new Verifications() {
            {
                systemUpgradeDAO.findByStateInOrderByCreateTimeAsc((List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                systemUpgradePackageService.getSystemUpgradePackage((UUID) any);
                times = 0;
                systemUpgradeSupportService.openSystemUpgradeService((TerminalSystemUpgradePackageEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试safeInit，有进行中的刷机任务,出现BusinessException
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitHasBusinessException() throws BusinessException {
        List<TerminalSystemUpgradeEntity> arrayList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        arrayList.add(upgradeEntity);
        
        new Expectations() {
            {
                systemUpgradeDAO.findByStateInOrderByCreateTimeAsc((List<CbbSystemUpgradeTaskStateEnums>) any);
                result = arrayList;
                systemUpgradePackageService.getSystemUpgradePackage(upgradeEntity.getUpgradePackageId());
                result = new BusinessException("key");
            }
        };
        init.safeInit();
        
        new Verifications() {
            {
                systemUpgradeDAO.findByStateInOrderByCreateTimeAsc((List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                systemUpgradePackageService.getSystemUpgradePackage(upgradeEntity.getUpgradePackageId());
                times = 1;
                systemUpgradeSupportService.openSystemUpgradeService((TerminalSystemUpgradePackageEntity) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试safeInit，有进行中的刷机任务
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInit() throws BusinessException {
        List<TerminalSystemUpgradeEntity> arrayList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        arrayList.add(upgradeEntity);
        
        TerminalSystemUpgradePackageEntity systemUpgradePackage = new TerminalSystemUpgradePackageEntity();
        new Expectations() {
            {
                systemUpgradeDAO.findByStateInOrderByCreateTimeAsc((List<CbbSystemUpgradeTaskStateEnums>) any);
                result = arrayList;
                systemUpgradePackageService.getSystemUpgradePackage(upgradeEntity.getUpgradePackageId());
                result = systemUpgradePackage;
            }
        };
        init.safeInit();
        
        new Verifications() {
            {
                systemUpgradeDAO.findByStateInOrderByCreateTimeAsc((List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                systemUpgradePackageService.getSystemUpgradePackage(upgradeEntity.getUpgradePackageId());
                times = 1;
                systemUpgradeSupportService.openSystemUpgradeService(systemUpgradePackage);
                times = 1;
            }
        };
    }

}
