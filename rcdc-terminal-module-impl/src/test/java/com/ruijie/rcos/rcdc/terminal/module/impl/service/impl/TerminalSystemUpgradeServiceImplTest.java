package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.UUID;

import org.junit.Test;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.LinuxVDISystemUpgradeFileClearHandler;
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
 * Create Time: 2020年1月8日
 * 
 * @author nt
 */
public class TerminalSystemUpgradeServiceImplTest {

    @Tested
    private TerminalSystemUpgradeServiceImpl upgradeService;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Injectable
    private LinuxVDISystemUpgradeFileClearHandler upgradeFileClearHandler;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private TerminalSystemUpgradeTerminalGroupDAO systemUpgradeTerminalGroupDAO;

    /**
     * 测试编辑升级任务状态 - 任务记录为null
     *
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeStateUpgradeTaskIsNull() throws Exception {
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                result = Optional.empty();
            }
        };

        try {
            upgradeService.modifySystemUpgradeState(upgradeEntity);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST, e.getKey());
        }

        new Verifications() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                times = 1;

                terminalSystemUpgradeDAO.save((TerminalSystemUpgradeEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试编辑升级任务状态 - 任务记录为null
     *
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeState() throws Exception {
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                result = Optional.of(upgradeEntity);
            }
        };

        upgradeService.modifySystemUpgradeState(upgradeEntity);

        new Verifications() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                times = 1;

                terminalSystemUpgradeDAO.save(upgradeEntity);
                times = 1;

                upgradeFileClearHandler.clear();
                times = 0;
            }
        };
    }

    /**
     * 测试编辑升级任务状态 - linux vdi任务关闭
     *
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeStateIsLinuxVDIAndStateIsFinish() throws Exception {
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();
        upgradeEntity.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.FINISH);

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                result = Optional.of(upgradeEntity);
            }
        };

        upgradeService.modifySystemUpgradeState(upgradeEntity);

        new Verifications() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                times = 1;

                terminalSystemUpgradeDAO.save(upgradeEntity);
                times = 1;

                upgradeFileClearHandler.clear();
                times = 1;
            }
        };
    }

    private TerminalSystemUpgradeEntity buildUpgradeEntity() {
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        upgradeEntity.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        upgradeEntity.setId(UUID.randomUUID());

        return upgradeEntity;
    }

}
