package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
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
 * Create Time: 2019年2月25日
 * 
 * @author ls
 */
public class SystemUpgradeQuartzHandlerTest {

    @Tested
    private SystemUpgradeQuartzHandler handler;

    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private TerminalSystemUpgradeService systemUpgradeService;

    @Injectable
    private SystemUpgradeStartWaitingHandler startWaitingHandler;

    @Injectable
    private SystemUpgradeStateSynctHandler stateSyncHandler;

    @Injectable
    private SystemUpgradeStartConfirmHandler confirmHandler;

    /**
     * 测试run，无正在进行中的刷机任务
     */
    @Test
    public void testRunNoUpgradeTask() {
        new Expectations() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = null;
            }
        };
        handler.run();

        new Verifications() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                systemUpgradeTerminalDAO.findBySysUpgradeId((UUID) any);
                times = 0;
            }
        };
    }

    /**
     * 测试run，刷机任务无刷机终端
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testRunUpgradeTaskNoUpgradeTerminal() throws BusinessException {
        List<TerminalSystemUpgradeEntity> upgradeTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeTask = new TerminalSystemUpgradeEntity();
        upgradeTaskList.add(upgradeTask);
        new Expectations() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradeTaskList;
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
                result = null;
            }
        };
        handler.run();

        new Verifications() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
                times = 1;
                systemUpgradeService.modifySystemUpgradeState((TerminalSystemUpgradeEntity) any);
                times = 1;
                confirmHandler.execute((List<TerminalSystemUpgradeTerminalEntity>) any);
                times = 0;
            }
        };
    }

    /**
     * 测试run，刷机任务无刷机终端,BusinessException
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testRunUpgradeTaskNoUpgradeTerminalHasBusinessException() throws BusinessException {
        List<TerminalSystemUpgradeEntity> upgradeTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeTask = new TerminalSystemUpgradeEntity();
        upgradeTaskList.add(upgradeTask);
        new Expectations() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradeTaskList;
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
                result = null;
                systemUpgradeService.modifySystemUpgradeState((TerminalSystemUpgradeEntity) any);
                result = new BusinessException("key");
            }
        };
        handler.run();

        new Verifications() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
                times = 1;
                systemUpgradeService.modifySystemUpgradeState((TerminalSystemUpgradeEntity) any);
                times = 1;
                confirmHandler.execute((List<TerminalSystemUpgradeTerminalEntity>) any);
                times = 0;
            }
        };
    }

    /**
     * 测试run，刷机任务有刷机终端,存在非最终态的刷机终端
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testRunUpgradeTaskHasUpgradeTerminalAndNoFinalState() throws BusinessException {
        List<TerminalSystemUpgradeEntity> upgradeTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeTask = new TerminalSystemUpgradeEntity();
        upgradeTaskList.add(upgradeTask);

        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);
        upgradeTerminalList.add(upgradeTerminal);
        TerminalSystemUpgradeTerminalEntity upgradeTerminal1 = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal1.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeTerminalList.add(upgradeTerminal1);
        new Expectations() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradeTaskList;
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
                result = upgradeTerminalList;
            }
        };
        handler.run();

        new Verifications() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
                times = 2;
                systemUpgradeService.modifySystemUpgradeState((TerminalSystemUpgradeEntity) any);
                times = 0;
                confirmHandler.execute((List<TerminalSystemUpgradeTerminalEntity>) any);
                times = 1;
                stateSyncHandler.execute(upgradeTerminalList);
                times = 1;
                startWaitingHandler.execute(upgradeTerminalList, upgradeTask.getUpgradePackageId());
                times = 1;
            }
        };
    }

    /**
     * 测试run，刷机任务有刷机终端,不存在非最终态的刷机终端
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testRunUpgradeTaskHasUpgradeTerminalAndFinalState() throws BusinessException {
        List<TerminalSystemUpgradeEntity> upgradeTaskList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeTask = new TerminalSystemUpgradeEntity();
        upgradeTaskList.add(upgradeTask);

        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        upgradeTerminalList.add(upgradeTerminal);
        new Expectations() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradeTaskList;
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
                result = upgradeTerminalList;
            }
        };
        handler.run();

        new Verifications() {
            {
                systemUpgradeDAO
                        .findByPackageTypeAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX, (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                systemUpgradeTerminalDAO.findBySysUpgradeId(upgradeTask.getId());
                times = 2;
                systemUpgradeService.modifySystemUpgradeState((TerminalSystemUpgradeEntity) any);
                times = 1;
                confirmHandler.execute((List<TerminalSystemUpgradeTerminalEntity>) any);
                times = 1;
                stateSyncHandler.execute(upgradeTerminalList);
                times = 1;
                startWaitingHandler.execute(upgradeTerminalList, upgradeTask.getUpgradePackageId());
                times = 1;
            }
        };
    }
}
