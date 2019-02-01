package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
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
    private static final Cache<String, SystemUpgradeTask> TASK_CACHE = CacheBuilder.newBuilder().maximumSize(150)
            .expireAfterWrite(2, TimeUnit.HOURS).removalListener(new TimeoutListener()).build();

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
     * @param platform 终端平台类型
     * @return 添加的任务
     * @throws BusinessException 业务异常
     */
    public SystemUpgradeTask addTask(String terminalId, TerminalPlatformEnums platform) throws BusinessException {
        Assert.hasText(terminalId, "terminalId can not be empty");
        Assert.notNull(platform, "terminalType can not be null");

        // 队列中已经有升级任务
        SystemUpgradeTask systemUpgradeTask = TASK_CACHE.getIfPresent(terminalId);
        if (systemUpgradeTask != null) {
            LOGGER.debug("system upgrade task has exist, terminalId[{}], terminalType[{}]", terminalId, platform);
            return systemUpgradeTask;
        }

        SystemUpgradeTask task = buildSystemUpgradeTask(terminalId, platform);
        synchronized (TASK_CACHE) {
            if (TASK_CACHE.size() >= TASK_MAP_MAX_NUM) {
                LOGGER.debug("system upgrade task map exceed limit number, terminalId[{}], terminalType[{}]",
                        terminalId, platform);
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_NUM_EXCEED_LIMIT);
            }
            int count = countUpgradingNum();
            if (count >= UPGRADING_MAX_NUM) {
                LOGGER.debug("system upgrade task doing number exceed limit number, terminalId[{}], terminalType[{}]",
                        terminalId, platform);
                task.setState(CbbSystemUpgradeStateEnums.WAIT);
            }
            TASK_CACHE.put(terminalId, task);
        }
        return task;
    }

    private SystemUpgradeTask buildSystemUpgradeTask(String terminalId, TerminalPlatformEnums platform) {
        SystemUpgradeTask task = new SystemUpgradeTask();
        task.setTerminalId(terminalId);
        task.setPlatform(platform);
        long currentTime = System.currentTimeMillis();
        task.setStartTime(currentTime);
        task.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        task.setIsSend(false);
        return task;
    }

    /**
     * 移除升级任务
     * 
     * @param terminalId 终端id
     * @return 移除的升级任务
     */
    public void removeTaskByTerminalId(String terminalId) {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        TASK_CACHE.invalidate(terminalId);
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

        SystemUpgradeTask task = TASK_CACHE.getIfPresent(terminalId);
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
        return TASK_CACHE.getIfPresent(terminalId);
    }

    /**
     * 统计升级中的任务数量
     * 
     * @return 升级中的任务数量
     */
    public int countUpgradingNum() {
        int count = 0;
        ConcurrentMap<String, SystemUpgradeTask> taskMap = TASK_CACHE.asMap();
        if (CollectionUtils.isEmpty(taskMap)) {
            LOGGER.debug("no task in cache");
            return 0;
        }
        Set<Entry<String, SystemUpgradeTask>> entrySet = taskMap.entrySet();
        for (Iterator<Entry<String, SystemUpgradeTask>> it = entrySet.iterator(); it.hasNext();) {
            SystemUpgradeTask task = it.next().getValue();
            if (task.getState() == CbbSystemUpgradeStateEnums.UPGRADING) {
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
        return TASK_CACHE.asMap();
    }


    /**
     * 获取升级中的任务列表
     * 
     * @return 升级中的任务列表
     */
    public List<SystemUpgradeTask> getUpgradingTask() {
        List<SystemUpgradeTask> upgradingTaskList = new ArrayList<>();
        ConcurrentMap<String, SystemUpgradeTask> taskMap = TASK_CACHE.asMap();
        for (Iterator<Entry<String, SystemUpgradeTask>> it = taskMap.entrySet().iterator(); it.hasNext();) {
            SystemUpgradeTask task = it.next().getValue();
            if (task.getState() == CbbSystemUpgradeStateEnums.UPGRADING) {
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
        ConcurrentMap<String, SystemUpgradeTask> taskMap = TASK_CACHE.asMap();
        for (Iterator<Entry<String, SystemUpgradeTask>> it = taskMap.entrySet().iterator(); it.hasNext();) {
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
        ConcurrentMap<String, SystemUpgradeTask> taskMap = TASK_CACHE.asMap();
        for (Iterator<Entry<String, SystemUpgradeTask>> it = taskMap.entrySet().iterator(); it.hasNext();) {
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

        synchronized (TASK_CACHE) {
            int count = countUpgradingNum();
            if (count >= UPGRADING_MAX_NUM) {
                LOGGER.debug("system upgrade task doing number exceed limit number, terminal id[{}]",
                        task.getTerminalId());
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADING_NUM_EXCEED_LIMIT);
            }
            toStartTask.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        }

    }


    /**
     * 查询数量是否达到最大数量
     * 
     * @return 是否超出数量限制 true: 超过, false: 未超过
     */
    public boolean checkMaxAddNum() {
        return TASK_MAP_MAX_NUM <= TASK_CACHE.size();
    }
    
    /**
     * 
     * Description: 刷机任务超时监听器
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年2月1日
     * 
     * @author nt
     */
    protected static class TimeoutListener implements RemovalListener<String, SystemUpgradeTask>{

        @Override
        public void onRemoval(RemovalNotification<String, SystemUpgradeTask> notification) {
            Assert.notNull(notification, "notification can not be null");

            String key = notification.getKey();
            SystemUpgradeTask task = notification.getValue();
            task.setState(CbbSystemUpgradeStateEnums.FAIL);
            TASK_CACHE.put(key, task);
        }
        
    }
}
