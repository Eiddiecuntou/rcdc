package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
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
public class SystemUpgradeStateSyncQuartzTest {

    @Tested
    private SystemUpgradeStateSyncQuartz upgradeStateSyncQuartz;

    @Injectable
    private SystemUpgradeTaskManager taskManager;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;


    /**
     * 测试终端升级任务状态同步
     * @throws Exception 
     */
    @Test
    public void testStateSynchronize() throws Exception {
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

        upgradeStateSyncQuartz.execute();

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
     * @throws Exception 
     */
    @Test
    public void testStateSynchronizeTaskIsEmpty() throws Exception {
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

        upgradeStateSyncQuartz.execute();

        Assert.assertTrue(CollectionUtils.isEmpty(upgradingTaskList));

    }

    private List<SystemUpgradeTask> getMockedUpgradingTask() {
        List<SystemUpgradeTask> upgradeTaskList = new ArrayList<>();
        String baseTerminalId = "id";
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.VDI;
        for (int i = 0; i < 11; i++) {
            SystemUpgradeTask task = buildUpgradingTask(baseTerminalId + i, terminalType);
            upgradeTaskList.add(task);
        }

        return upgradeTaskList;
    }

    private SystemUpgradeTask buildUpgradingTask(String terminalId, CbbTerminalTypeEnums terminalType) {
        SystemUpgradeTask task = new SystemUpgradeTask();
        task.setTerminalId(terminalId);
        task.setTerminalType(terminalType);
        task.setState(CbbSystemUpgradeStateEnums.UPGRADING);
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
                info.setState(CbbSystemUpgradeStateEnums.UPGRADING);
            } else {
                info.setState(CbbSystemUpgradeStateEnums.SUCCESS);
            }
            upgradeInfoList.add(info);
        }

        TerminalSystemUpgradeInfo notIncacheInfo = buildUpgradeInfo(baseTerminalId + 12, terminalType);
        notIncacheInfo.setLastUpdateTime(System.currentTimeMillis() - 3000);
        notIncacheInfo.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeInfoList.add(notIncacheInfo);

        // 构建无效信息
        buildUnvalidUpgradeInfo(upgradeInfoList);

        return upgradeInfoList;
    }

    private void buildUnvalidUpgradeInfo(List<TerminalSystemUpgradeInfo> upgradeInfoList) {
        String baseTerminalId = "unvalidId";
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.VDI;

        TerminalSystemUpgradeInfo info1 = buildUpgradeInfo(baseTerminalId, terminalType);
        info1.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        upgradeInfoList.add(info1);

        TerminalSystemUpgradeInfo info2 = buildUpgradeInfo(baseTerminalId, terminalType);
        info2.setLastUpdateTime(System.currentTimeMillis());
        upgradeInfoList.add(info2);

        TerminalSystemUpgradeInfo info4 = buildUpgradeInfo(baseTerminalId, terminalType);
        info4.setLastUpdateTime(System.currentTimeMillis());
        info4.setState(CbbSystemUpgradeStateEnums.UPGRADING);
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
