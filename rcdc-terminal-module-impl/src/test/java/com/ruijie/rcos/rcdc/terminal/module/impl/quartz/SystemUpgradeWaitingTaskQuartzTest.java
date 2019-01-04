package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: 定时处理等待中的系统升级任务测试
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class SystemUpgradeWaitingTaskQuartzTest {
    
    @Tested
    private SystemUpgradeWaitingTaskQuartz waitingTaskQuartz;

    @Injectable
    private SystemUpgradeTaskManager taskManager;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;
    
    /**
     * 测试定时处理等待中的任务
     * @throws Exception 异常
     */
    @Test
    public void testDealWaitTask() throws Exception {
        TerminalSystemUpgradePackageEntity upgradePackage = buildUpgradePackage();
        List<SystemUpgradeTask> taskList = buildTaskList();
        new Expectations() {
            {
                taskManager.startWaitTask();
                result = taskList;

                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;

                terminalSystemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
            }
        };

        waitingTaskQuartz.execute();

        new Verifications() {
            {
                terminalSystemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                times = 5;
            }
        };

    }

    /**
     * 测试定时处理等待中的任务
     * @throws Exception 异常
     */
    @Test
    public void testDealWaitTaskSendUpgradeMsgFail() throws Exception {
        TerminalSystemUpgradePackageEntity upgradePackage = buildUpgradePackage();
        List<SystemUpgradeTask> taskList = buildTaskList();
        new Expectations() {
            {
                taskManager.startWaitTask();
                result = taskList;

                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;

                terminalSystemUpgradeService.systemUpgrade(anyString, (TerminalSystemUpgradeMsg) any);
                result = new BusinessException("key");

                taskManager.modifyTaskState(anyString, (CbbSystemUpgradeStateEnums) any);
            }
        };

        waitingTaskQuartz.execute();

        new Verifications() {
            {
                taskManager.modifyTaskState(anyString, (CbbSystemUpgradeStateEnums) any);
                times = 5;
            }
        };

    }


    /**
     * 测试定时处理等待中的任务，任务升级包不存在
     * @throws Exception 异常
     */
    @Test
    public void testDealWaitTaskUpgradePackageIsNull() throws Exception {

        List<SystemUpgradeTask> startTaskList = buildTaskList();
        new Expectations() {
            {
                taskManager.startWaitTask();
                result = startTaskList;

                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = null;

                taskManager.modifyTaskState(anyString, (CbbSystemUpgradeStateEnums) any);

            }
        };

        waitingTaskQuartz.execute();

        new Verifications() {
            {
                taskManager.modifyTaskState(anyString, (CbbSystemUpgradeStateEnums) any);
                times = 5;
            }
        };

    }

    /**
     * 测试定时处理等待中的任务，任务升级包不存在
     * @throws Exception 异常
     */
    @Test
    public void testDealWaitTaskUpgradeTaskListIsNull() throws Exception {

        new Expectations() {
            {
                taskManager.startWaitTask();
                result = null;

            }
        };

        waitingTaskQuartz.execute();

        new Verifications() {
            {
                taskManager.startWaitTask();
                times = 1;

                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 0;
            }
        };

    }
    
    private TerminalSystemUpgradePackageEntity buildUpgradePackage() {
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setPackageVersion("version");
        upgradePackage.setImgName("packageName");
        upgradePackage.setPackageType(CbbTerminalTypeEnums.VDI);
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
    
}
