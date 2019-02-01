package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: 测试系统刷机任务管理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月22日
 * 
 * @author "nt"
 */
@RunWith(JMockit.class)
public class SystemUpgradeTaskManagerTest {

    @Tested
    private SystemUpgradeTaskManager manager;

    /**
     * 测试添加升级任务
     */
    @Test
    public void testAddTask() {

        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");
        String baseTerminalId = "bt-";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;

        for (int i = 0; i < 10000; i++) {
            try {
                manager.addTask(baseTerminalId + i, terminalType);
            } catch (BusinessException e) {
                Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_NUM_EXCEED_LIMIT);
            }
        }

        Assert.assertEquals(100, caches.size());
        Assert.assertEquals(100, manager.getAllTasks().size());
        Assert.assertEquals(50, manager.getUpgradingTask().size());
        caches.clear();
        
    }

    /**
     * 测试添加重复的任务
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddTaskWithExistTask() throws BusinessException {

        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");
        String terminalId = "id1";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;

        manager.addTask(terminalId, terminalType);

        Assert.assertEquals(1, manager.getTaskMap().size());
        Assert.assertEquals(1, manager.countUpgradingNum());

        SystemUpgradeTask task1 = manager.getTaskByTerminalId(terminalId);
        Assert.assertEquals(terminalId, task1.getTerminalId());


        SystemUpgradeTask task2 = manager.addTask(terminalId, terminalType);
        Assert.assertEquals(1, manager.getTaskMap().size());
        Assert.assertEquals(1, manager.getTaskMap().size());
        Assert.assertEquals(terminalId, task2.getTerminalId());
        Assert.assertTrue(task2 == task1);
        caches.clear();
    }

    /**
     * 测试删除任务成功
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testRemoveTaskByTerminalId() throws BusinessException {

        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String terminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;
        manager.addTask(terminalId, terminalType);
        Assert.assertEquals(1, manager.getTaskMap().size());

        manager.removeTaskByTerminalId(terminalId);
        Assert.assertEquals(0, manager.getTaskMap().size());
        caches.clear();
    }

    /**
     * 测试删除任务成功
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testRemoveTaskByTerminalIdTerminalIdIsNull() throws BusinessException {

        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String terminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;
        manager.addTask(terminalId, terminalType);
        Assert.assertEquals(1, manager.getTaskMap().size());

        try {
            manager.removeTaskByTerminalId(null);
            fail();
        } catch (Exception e) {
            assertEquals("terminalId 不能为空", e.getMessage());
        }

        Assert.assertEquals(1, manager.getTaskMap().size());
        caches.clear();
    }

    /**
     * 测试修改任务状态
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testModifyTaskState() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String terminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;
        SystemUpgradeTask addTask = manager.addTask(terminalId, terminalType);
        Assert.assertEquals(1, manager.getTaskMap().size());

        manager.modifyTaskState(terminalId, CbbSystemUpgradeStateEnums.SUCCESS);
        Assert.assertEquals(1, manager.getTaskMap().size());
        Assert.assertEquals(CbbSystemUpgradeStateEnums.SUCCESS, addTask.getState());
        caches.clear();
    }

    /**
     * 测试修改任务状态
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testModifyTaskStateTaskNotExist() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String terminalId = "id";
        String terminalId2 = "id2";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;
        SystemUpgradeTask addTask = manager.addTask(terminalId, terminalType);
        Assert.assertEquals(1, manager.getTaskMap().size());

        manager.modifyTaskState(terminalId2, CbbSystemUpgradeStateEnums.SUCCESS);
        Assert.assertEquals(1, manager.getTaskMap().size());
        Assert.assertNotEquals(CbbSystemUpgradeStateEnums.SUCCESS, addTask.getState());
        caches.clear();
    }


    /**
     * 测试开始升级等待中的任务
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartWaitTask() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String baseTerminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;
        SystemUpgradeTask task = null;
        for (int i = 0; i < 20; i++) {
            String terminalId = baseTerminalId + i;
            task = buildSystemUpgradeTask(terminalId, terminalType);
            caches.put(terminalId, task);
        }
        
        for (int i = 50; i < 100; i++) {
            String terminalId = baseTerminalId + i;
            task = buildSystemUpgradeTask(terminalId, terminalType);
            task.setState(CbbSystemUpgradeStateEnums.WAIT);
            caches.put(terminalId, task);
        }
        Assert.assertEquals(70, caches.size());

        List<SystemUpgradeTask> taskList = manager.startWaitTask();
        Assert.assertEquals(70, caches.size());
        Assert.assertEquals(30, taskList.size());
        Assert.assertEquals(50, manager.countUpgradingNum());
        caches.clear();
    }


    /**
     * 测试开始升级等待中的任务(升级中队列已达到限制)
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartWaitTaskWithUpgradingReachedUpperLimit() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String baseTerminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;
        SystemUpgradeTask task = null;
        for (int i = 0; i < 50; i++) {
            String terminalId = baseTerminalId + i;
            task = buildSystemUpgradeTask(terminalId, terminalType);
            caches.put(terminalId, task);
        }
        Assert.assertEquals(50, caches.size());
        Assert.assertEquals(50, manager.countUpgradingNum());
        for (int i = 50; i < 100; i++) {
            String terminalId = baseTerminalId + i;
            task = buildSystemUpgradeTask(terminalId, terminalType);
            task.setState(CbbSystemUpgradeStateEnums.WAIT);
            caches.put(terminalId, task);
        }
        Assert.assertEquals(100, caches.size());

        List<SystemUpgradeTask> taskList = manager.startWaitTask();
        Assert.assertEquals(100, caches.size());
        Assert.assertEquals(0, taskList.size());
        Assert.assertEquals(50, manager.countUpgradingNum());
        caches.clear();
    }

    /**
     * 测试开始升级等待中的任务(缓存中无任务)
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartWaitTaskEmptyCache() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");


        Assert.assertEquals(0, caches.size());
        Assert.assertEquals(0, manager.countUpgradingNum());

        List<SystemUpgradeTask> taskList = manager.startWaitTask();
        Assert.assertEquals(0, caches.size());
        Assert.assertEquals(0, taskList.size());
        Assert.assertEquals(0, manager.countUpgradingNum());
        caches.clear();
    }

    /**
     * 测试开始升级等待中的任务开始失败
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartWaitTaskStartTaskFail() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");
        String baseTerminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;
        SystemUpgradeTask task = null;
        for (int i = 0; i < 5; i++) {
            String terminalId = baseTerminalId + i;
            task = buildSystemUpgradeTask(terminalId, terminalType);
            caches.put(terminalId, task);
        }
        for (int i = 50; i < 55; i++) {
            String terminalId = baseTerminalId + i;
            task = buildSystemUpgradeTask(terminalId, terminalType);
            task.setState(CbbSystemUpgradeStateEnums.WAIT);
            caches.put(terminalId, task);
        }
        
        new MockUp<SystemUpgradeTaskManager>() {
            
            @Mock
            public void startTask(SystemUpgradeTask task) throws BusinessException {
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADING_NUM_EXCEED_LIMIT);
            }
        };
        

        manager.startWaitTask();
    
        
        Assert.assertEquals(10, caches.size());
        caches.clear();
    }


    /**
     * 测试开始升级等待中的任务
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartTask() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String terminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;

        SystemUpgradeTask task = buildSystemUpgradeTask(terminalId, terminalType);
        task.setState(CbbSystemUpgradeStateEnums.WAIT);
        caches.put(terminalId, task);

        manager.startTask(task);

        Assert.assertEquals(1, caches.size());
        Assert.assertEquals(1, manager.countUpgradingNum());
        caches.clear();
    }

    /**
     * 测试开始升级等待中的任务(同步资源竞争问题)
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartTaskNoUpgradingPosition() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String terminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;

        SystemUpgradeTask task = buildSystemUpgradeTask(terminalId, terminalType);
        task.setState(CbbSystemUpgradeStateEnums.WAIT);
        caches.put(terminalId, task);

        new MockUp<SystemUpgradeTaskManager>() {
            @Mock
            public int countUpgradingNum() {
                return 51;
            }

        };

        try {
            manager.startTask(task);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADING_NUM_EXCEED_LIMIT, e.getKey());
        }

        Assert.assertEquals(1, caches.size());
        caches.clear();
    }



    /**
     * 测试开始升级等待中的任务
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartTaskTaskNotInCache() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String terminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;

        SystemUpgradeTask task = buildSystemUpgradeTask(terminalId, terminalType);
        task.setState(CbbSystemUpgradeStateEnums.WAIT);
        caches.clear();
        caches.put(terminalId, task);

        SystemUpgradeTask notInTask = buildSystemUpgradeTask("1111", terminalType);

        try {
            manager.startTask(notInTask);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST, e.getKey());
        }

        Assert.assertEquals(1, caches.size());
        Assert.assertEquals(0, manager.countUpgradingNum());
        caches.clear();
    }

    /**
     * 测试开始升级等待中的任务
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testStartTaskTaskIsNotWait() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");

        String terminalId = "id";
        TerminalPlatformEnums terminalType = TerminalPlatformEnums.IDV;

        SystemUpgradeTask task = buildSystemUpgradeTask(terminalId, terminalType);
        task.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        caches.put(terminalId, task);

        Assert.assertEquals(1, caches.size());
        Assert.assertEquals(1, manager.countUpgradingNum());

        try {
            manager.startTask(task);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_STATE_INCORRECT, e.getKey());
        }

        Assert.assertEquals(1, caches.size());
        Assert.assertEquals(1, manager.countUpgradingNum());
        caches.clear();
    }

    /**
     * 测试checkMaxAddNum，超过最大值
     * @throws BusinessException 异常
     */
    @Test
    public void testCheckMaxAddNumIsTrue() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");
        String baseTerminalId = "bt-";
        SystemUpgradeTask systemUpgradeTask = new SystemUpgradeTask();
        for (int i = 0; i < 200; i++) {
            caches.put(baseTerminalId + i, systemUpgradeTask);
        }
        assertTrue(manager.checkMaxAddNum());
        caches.clear();
    }
    
    /**
     * 测试checkMaxAddNum，未超过最大值
     * @throws BusinessException 异常
     */
    @Test
    public void testCheckMaxAddNumIsFalse() throws BusinessException {
        Map<String, SystemUpgradeTask> caches = Deencapsulation.getField(manager, "TASK_MAP");
        String baseTerminalId = "bt-";
        SystemUpgradeTask systemUpgradeTask = new SystemUpgradeTask();
        for (int i = 0; i < 90; i++) {
            caches.put(baseTerminalId + i, systemUpgradeTask);
        }
        assertFalse(manager.checkMaxAddNum());
        caches.clear();
    }
    
    private SystemUpgradeTask buildSystemUpgradeTask(String terminalId, TerminalPlatformEnums terminalType) {
        SystemUpgradeTask task = new SystemUpgradeTask();
        task.setTerminalId(terminalId);
        task.setPlatform(terminalType);
        task.setStartTime(System.currentTimeMillis());
        task.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        task.setIsSend(false);
        return task;
    }



}
