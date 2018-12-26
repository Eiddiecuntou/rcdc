package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 终端系统升级任务管理器，用于存储前端发起的系统升级请求及任务状态
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月21日
 * 
 * @author nt
 */
@Service
public class SystemUpgradeTaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeTaskManager.class);

    /**
     * 升级任务集合
     */
    private static final Map<String, SystemUpgradeTask> TASK_MAP = new LinkedHashMap<>();

    /**
     * 最大同时刷机数
     */
    private static final int UPGRADING_MAX_NUM = 50;

    /**
     * 队列最大数量
     */
    private static final int TASK_MAP_MAX_NUM = 100;

    /**
     * 
     * 添加升级任务
     * 
     * @param terminalId 终端id
     * @param terminalType 终端类型
     * @return 添加的任务
     * @throws BusinessException 业务异常
     */
    public SystemUpgradeTask addTask(String terminalId, CbbTerminalTypeEnums terminalType) throws BusinessException {
        Assert.hasText(terminalId, "terminalId can not be empty");
        Assert.notNull(terminalType, "terminalType can not be null");

        // 队列中已经有升级任务
        SystemUpgradeTask systemUpgradeTask = TASK_MAP.get(terminalId);
        if (systemUpgradeTask != null) {
            LOGGER.debug("system upgrade task has exist, terminalId[{}], terminalType[{}]", terminalId, terminalType);
            return systemUpgradeTask;
        }

        SystemUpgradeTask task = buildSystemUpgradeTask(terminalId, terminalType);
        synchronized (TASK_MAP) {
            if (TASK_MAP.size() >= TASK_MAP_MAX_NUM) {
                LOGGER.debug("system upgrade task map exceed limit number, terminalId[{}], terminalType[{}]",
                        terminalId, terminalType);
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_NUM_EXCEED_LIMIT);
            }
            int count = countUpgradingNum();
            if (count >= UPGRADING_MAX_NUM) {
                LOGGER.debug("system upgrade task doing number exceed limit number, terminalId[{}], terminalType[{}]",
                        terminalId, terminalType);
                task.setState(CbbSystemUpgradeStateEnums.WAIT);
            }
            TASK_MAP.put(terminalId, task);
        }
        return task;
    }

    private SystemUpgradeTask buildSystemUpgradeTask(String terminalId, CbbTerminalTypeEnums terminalType) {
        SystemUpgradeTask task = new SystemUpgradeTask();
        task.setTerminalId(terminalId);
        task.setTerminalType(terminalType);
        long currentTime = System.currentTimeMillis();
        task.setStartTime(currentTime);
        task.setTimeStamp(currentTime);
        task.setState(CbbSystemUpgradeStateEnums.DOING);
        task.setIsSend(false);
        return task;
    }

    /**
     * 移除升级任务
     * 
     * @param terminalId 终端id
     * @return 移除的升级任务
     */
    public SystemUpgradeTask removeTaskByTerminalId(String terminalId) {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        return TASK_MAP.remove(terminalId);
    }

    /**
     * 
     * 修改升级任务状态
     * 
     * @param terminalId 终端id
     * @param state 升级状态
     */
    public void modifyTaskState(String terminalId, CbbSystemUpgradeStateEnums state) {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        Assert.notNull(state, "systemUpgradeState 不能为空");

        SystemUpgradeTask task = TASK_MAP.get(terminalId);
        if (task != null) {
            LOGGER.debug("task is not null; terminal id[{}]", task.getTerminalId());
            task.setState(state);
        }
    }


    /**
     * 获取升级任务
     * 
     * @param terminalId 终端id
     * @return 升级任务
     */
    public SystemUpgradeTask getTaskByTerminalId(String terminalId) {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        return TASK_MAP.get(terminalId);
    }

    /**
     * 统计升级中的任务数量
     * 
     * @return 升级中的任务数量
     */
    public int countUpgradingNum() {
        int count = 0;
        Set<Entry<String, SystemUpgradeTask>> entrySet = TASK_MAP.entrySet();
        for (Iterator<Entry<String, SystemUpgradeTask>> it = entrySet.iterator(); it.hasNext();) {
            SystemUpgradeTask task = it.next().getValue();
            if (task.getState() == CbbSystemUpgradeStateEnums.DOING) {
                LOGGER.debug("task is doing; terminal id[{}]", task.getTerminalId());
                count++;
            }
        }
        return count;
    }


    /**
     * 获取升级任务队列缓存
     * 
     * @return 升级任务队列缓存
     */
    public Map<String, SystemUpgradeTask> getTaskMap() {
        return TASK_MAP;
    }


    /**
     * 获取升级中的任务列表
     * 
     * @return 升级中的任务列表
     */
    public List<SystemUpgradeTask> getUpgradingTask() {
        List<SystemUpgradeTask> upgradingTaskList = new ArrayList<>();
        for (Iterator<Entry<String, SystemUpgradeTask>> it = TASK_MAP.entrySet().iterator(); it.hasNext();) {
            SystemUpgradeTask task = it.next().getValue();
            if (task.getState() == CbbSystemUpgradeStateEnums.DOING) {
                LOGGER.debug("task is doing; terminal id[{}]", task.getTerminalId());
                upgradingTaskList.add(task);
            }
        }
        return upgradingTaskList;
    }


    /**
     * 获取所有升级任务列表
     * 
     * @return 任务列表
     */
    public List<SystemUpgradeTask> getAllTasks() {
        List<SystemUpgradeTask> upgradeTaskList = new ArrayList<>();
        for (Iterator<Entry<String, SystemUpgradeTask>> it = TASK_MAP.entrySet().iterator(); it.hasNext();) {
            upgradeTaskList.add(it.next().getValue());
        }
        return upgradeTaskList;
    }

    /**
     * 
     * 开始等待中的升级任务
     * 
     * @return 此次开始升级任务的列表
     */
    public List<SystemUpgradeTask> startWaitTask() {

        int count = UPGRADING_MAX_NUM - countUpgradingNum();
        if (count <= 0) {
            LOGGER.debug("system upgrade task doing number exceed limit number");
            return Collections.emptyList();
        }

        List<SystemUpgradeTask> startTaskList = new ArrayList<>();
        for (Iterator<Entry<String, SystemUpgradeTask>> it = TASK_MAP.entrySet().iterator(); it.hasNext();) {
            if (count <= 0) {
                LOGGER.debug("system upgrade task doing number exceed limit number, stop to start task");
                break;
            }
            // 将等待中的任务设置为开始升级
            SystemUpgradeTask task = it.next().getValue();
            if (task.getState() == CbbSystemUpgradeStateEnums.WAIT) {
                LOGGER.debug("start system upgrade task, {}", task.toString());
                try {
                    startTask(task);
                    startTaskList.add(task);
                    count--;
                } catch (BusinessException e) {
                    // 系统升级数量已达最大值，不再开始任务
                    LOGGER.debug("start system upgrade task error, which says task doing num exceed limit {}",
                            task.toString());
                    break;
                }
            }
        }
        return startTaskList;
    }

    /**
     * 
     * 开始升级任务
     * 
     * @param task 升级任务
     * @throws BusinessException 业务异常
     */
    public void startTask(SystemUpgradeTask task) throws BusinessException {

        Assert.notNull(task, "system upgrade task can not be null");

        SystemUpgradeTask toStartTask = getTaskByTerminalId(task.getTerminalId());
        if (toStartTask == null) {
            LOGGER.debug("can not find system upgrade task, terminal id[{}]", task.getTerminalId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST);
        }

        if (task.getState() != CbbSystemUpgradeStateEnums.WAIT) {
            LOGGER.debug("system upgrade task state incorrect, terminal id[{}], terminal state[{}]",
                    task.getTerminalId(), task.getState());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_STATE_INCORRECT);
        }

        synchronized (TASK_MAP) {
            int count = countUpgradingNum();
            if (count >= UPGRADING_MAX_NUM) {
                LOGGER.debug("system upgrade task doing number exceed limit number, terminal id[{}]",
                        task.getTerminalId());
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADING_NUM_EXCEED_LIMIT);
            }
            toStartTask.setState(CbbSystemUpgradeStateEnums.DOING);
        }

    }


    /**
     * 查询数量是否超过最大数量
     * 
     * @param num 数量
     * @return 是否超出数量限制 true: 超过,  false: 未超过
     */
    public boolean checkMaxAddNum(int num) {
        return (TASK_MAP_MAX_NUM - TASK_MAP.size()) < num;
    }
}
