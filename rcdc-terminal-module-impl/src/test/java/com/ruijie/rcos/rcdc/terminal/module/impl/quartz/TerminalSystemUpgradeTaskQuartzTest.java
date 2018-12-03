package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TermianlSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TermianlSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: 测试终端系统升级任务定时任务
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月28日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class TerminalSystemUpgradeTaskQuartzTest {

    @Tested
    private TerminalSystemUpgradeTaskQuartz terminalSystemUpgradeTaskQuartz;

    @Injectable
    private SystemUpgradeTaskManager taskManager;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private TermianlSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;


    /**
     * 测试终端升级任务状态同步
     */
    @Test
    public void testStateSynchronize() {
        List<TerminalSystemUpgradeInfo> upgradeInfoList = getMockedUpgradeInfoList();
        List<SystemUpgradeTask> upgradingTaskList = getMockedUpgradingTask();
        new Expectations() {
            {
                taskManager.getUpgradingTask();
                result = upgradingTaskList;

                terminalSystemUpgradeService.readSystemUpgradeStateFromFile();
                result = upgradeInfoList;

            }

        };

        terminalSystemUpgradeTaskQuartz.stateSynchronize();

        int count = 0;
        for (int i = 0; i < upgradingTaskList.size(); i++) {
            if (upgradingTaskList.get(i).getState() == CbbSystemUpgradeStateEnums.SUCCESS) {
                count++;
            }
        }
        Assert.assertEquals(5, count);

    }

    /**
     * 测试终端升级任务状态同步合并时添加进队列失败
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStateSynchronizeAddCacheFail() throws BusinessException {
        List<TerminalSystemUpgradeInfo> upgradeInfoList = getMockedUpgradeInfoList();
        List<SystemUpgradeTask> upgradingTaskList = getMockedUpgradingTask();
        new Expectations() {
            {
                taskManager.getUpgradingTask();
                result = upgradingTaskList;

                terminalSystemUpgradeService.readSystemUpgradeStateFromFile();
                result = upgradeInfoList;

//                taskManager.addTask(anyString, (CbbTerminalTypeEnums) any);
//                result = new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_NUM_EXCEED_LIMIT);
            }

        };

        terminalSystemUpgradeTaskQuartz.stateSynchronize();

        int count = 0;
        for (int i = 0; i < upgradingTaskList.size(); i++) {
            if (upgradingTaskList.get(i).getState() == CbbSystemUpgradeStateEnums.SUCCESS) {
                count++;
            }
        }
        Assert.assertEquals(5, count);

    }


    /**
     * 测试终端升级任务状态同步任务信息为空的情况
     */
    @Test
    public void testStateSynchronizeTaskIsEmpty() {
        List<TerminalSystemUpgradeInfo> upgradeInfoList = null;
        List<SystemUpgradeTask> upgradingTaskList = null;
        new Expectations() {
            {
                taskManager.getUpgradingTask();
                result = upgradingTaskList;

                terminalSystemUpgradeService.readSystemUpgradeStateFromFile();
                result = upgradeInfoList;

            }

        };

        terminalSystemUpgradeTaskQuartz.stateSynchronize();

        Assert.assertTrue(CollectionUtils.isEmpty(upgradingTaskList));

    }


    /**
     * 测试定时处理等待中的任务
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDealWaitTask() throws BusinessException {
        TermianlSystemUpgradePackageEntity upgradePackage = buildUpgradePackage();
        List<SystemUpgradeTask> taskList = buildTaskList();
        new Expectations() {
            {
                taskManager.startWaitTask();
                result = taskList;

                termianlSystemUpgradePackageDAO
                        .findTermianlSystemUpgradePackageByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;

                terminalSystemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
            }
        };

        terminalSystemUpgradeTaskQuartz.dealWaitingTask();

        new Verifications() {
            {
                terminalSystemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 5;
            }
        };

    }

    /**
     * 测试定时处理等待中的任务
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDealWaitTaskSendUpgradeMsgFail() throws BusinessException {
        TermianlSystemUpgradePackageEntity upgradePackage = buildUpgradePackage();
        List<SystemUpgradeTask> taskList = buildTaskList();
        new Expectations() {
            {
                taskManager.startWaitTask();
                result = taskList;

                termianlSystemUpgradePackageDAO
                        .findTermianlSystemUpgradePackageByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;

                terminalSystemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                result = new BusinessException(anyString);

                taskManager.removeTaskByTerminalId(anyString);
            }
        };

        terminalSystemUpgradeTaskQuartz.dealWaitingTask();

        new Verifications() {
            {
                taskManager.removeTaskByTerminalId(anyString);
                times = 5;
            }
        };

    }


    /**
     * 测试定时处理等待中的任务，任务升级包不存在
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDealWaitTaskUpgradePackageIsNull() throws BusinessException {

        List<SystemUpgradeTask> startTaskList = buildTaskList();
        new Expectations() {
            {
                taskManager.startWaitTask();
                result = startTaskList;

                termianlSystemUpgradePackageDAO
                        .findTermianlSystemUpgradePackageByPackageType((CbbTerminalTypeEnums) any);
                result = null;

                taskManager.modifyTaskState(anyString, (CbbSystemUpgradeStateEnums) any);

            }
        };

        terminalSystemUpgradeTaskQuartz.dealWaitingTask();

        new Verifications() {
            {
                taskManager.modifyTaskState(anyString, (CbbSystemUpgradeStateEnums) any);
                times = 5;
            }
        };

    }

    /**
     * 测试定时处理等待中的任务，任务升级包不存在
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDealWaitTaskUpgradeTaskListIsNull() throws BusinessException {

        new Expectations() {
            {
                taskManager.startWaitTask();
                result = null;

            }
        };

        terminalSystemUpgradeTaskQuartz.dealWaitingTask();

        new Verifications() {
            {
                taskManager.startWaitTask();
                times = 1;

                termianlSystemUpgradePackageDAO
                        .findTermianlSystemUpgradePackageByPackageType((CbbTerminalTypeEnums) any);
                times = 0;
            }
        };

    }

    private TermianlSystemUpgradePackageEntity buildUpgradePackage() {
        TermianlSystemUpgradePackageEntity upgradePackage = new TermianlSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setInternalVersion("internalVersion");
        upgradePackage.setExternalVersion("externalVersion");
        upgradePackage.setName("packageName");
        upgradePackage.setPackageType(CbbTerminalTypeEnums.VDI);
        upgradePackage.setStorePath("storePath");
        upgradePackage.setUploadTime(new Date());
        return upgradePackage;
    }

    private List<SystemUpgradeTask> buildTaskList() {
        List<SystemUpgradeTask> taskList = new ArrayList<>();

        String baseTerminalId = "id";
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.VDI;
        for (int i = 0; i < 5; i++) {
            SystemUpgradeTask task = buildUpgradingTask(baseTerminalId + i, terminalType);
            taskList.add(task);
        }
        return taskList;
    }

    private List<SystemUpgradeTask> getMockedUpgradingTask() {
        List<SystemUpgradeTask> upgradeTaskList = new ArrayList<>();
        String baseTerminalId = "id";
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.VDI;
        for (int i = 0; i < 11; i++) {
            SystemUpgradeTask task = buildUpgradingTask(baseTerminalId + i, terminalType);
            upgradeTaskList.add(task);
        }

        SystemUpgradeTask nullTask = buildUpgradingTask(baseTerminalId, terminalType);
        nullTask.setTerminalId(null);
        upgradeTaskList.add(nullTask);

        return upgradeTaskList;
    }

    private SystemUpgradeTask buildUpgradingTask(String terminalId, CbbTerminalTypeEnums terminalType) {
        SystemUpgradeTask task = new SystemUpgradeTask();
        task.setTerminalId(terminalId);
        task.setTerminalType(terminalType);
        task.setState(CbbSystemUpgradeStateEnums.DOING);
        task.setIsSend(true);
        task.setStartTime(System.currentTimeMillis() - 50000);
        task.setTimeStamp(System.currentTimeMillis() - 10000);

        return task;
    }

    private List<TerminalSystemUpgradeInfo> getMockedUpgradeInfoList() {
        List<TerminalSystemUpgradeInfo> upgradeInfoList = new ArrayList<>();
        String baseTerminalId = "id";
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.VDI;
        for (int i = 0; i < 10; i++) {
            TerminalSystemUpgradeInfo info = buildUpgradeInfo(baseTerminalId + i, terminalType);
            info.setLastUpdateTime(System.currentTimeMillis() - 3000);
            if (i % 2 == 0) {
                info.setState(CbbSystemUpgradeStateEnums.DOING);
            } else {
                info.setState(CbbSystemUpgradeStateEnums.SUCCESS);
            }
            upgradeInfoList.add(info);
        }

        TerminalSystemUpgradeInfo notIncacheInfo = buildUpgradeInfo(baseTerminalId + 12, terminalType);
        notIncacheInfo.setLastUpdateTime(System.currentTimeMillis() - 3000);
        notIncacheInfo.setState(CbbSystemUpgradeStateEnums.DOING);
        upgradeInfoList.add(notIncacheInfo);

        // 构建无效信息
        buildUnvalidUpgradeInfo(upgradeInfoList);

        return upgradeInfoList;
    }

    private void buildUnvalidUpgradeInfo(List<TerminalSystemUpgradeInfo> upgradeInfoList) {
        String baseTerminalId = "unvalidId";
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.VDI;

        TerminalSystemUpgradeInfo info1 = buildUpgradeInfo(baseTerminalId, terminalType);
        info1.setState(CbbSystemUpgradeStateEnums.DOING);
        upgradeInfoList.add(info1);

        TerminalSystemUpgradeInfo info2 = buildUpgradeInfo(baseTerminalId, terminalType);
        info2.setLastUpdateTime(System.currentTimeMillis());
        upgradeInfoList.add(info2);

        TerminalSystemUpgradeInfo info4 = buildUpgradeInfo(baseTerminalId, terminalType);
        info4.setLastUpdateTime(System.currentTimeMillis());
        info4.setState(CbbSystemUpgradeStateEnums.DOING);
        info4.setTerminalType(null);
        upgradeInfoList.add(info4);
    }


    private TerminalSystemUpgradeInfo buildUpgradeInfo(String terminalId, CbbTerminalTypeEnums terminalType) {
        TerminalSystemUpgradeInfo info = new TerminalSystemUpgradeInfo();
        info.setTerminalId(terminalId);
        info.setTerminalType(terminalType);
        info.setExternalVersion("ExternalVersion");
        info.setInternalVersion("InternalVersion");

        return info;
    }



}
