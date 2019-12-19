package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: 终端升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author nt
 */
public interface TerminalSystemUpgradeService {

    /**
     * 系统升级
     * 
     * @param terminalId 终端id
     * @param upgradeMsg 升级信息
     * @throws BusinessException 业务异常
     */
    void systemUpgrade(String terminalId, Object upgradeMsg) throws BusinessException;

    /**
     * 更新刷机任务状态
     * 
     * @param upgradeTask 刷机任务实体
     * @throws BusinessException 业务异常
     */
    void modifySystemUpgradeState(TerminalSystemUpgradeEntity upgradeTask) throws BusinessException;

    /**
     * 判断刷机包是否存在刷机任务处于进行状态
     * 
     * @param upgradePackageId 刷机包id
     * @return 判断结果
     */
    boolean hasSystemUpgradeInProgress(UUID upgradePackageId);

    /**
     * 获取刷机任务
     * 
     * @param systemUpgradeId 刷机任务id
     * @return 刷机任务实体对象
     * @throws BusinessException 业务异常
     */
    TerminalSystemUpgradeEntity getSystemUpgradeTask(UUID systemUpgradeId) throws BusinessException;

    /**
     * 获取升级中的刷机任务
     *
     * @param packageId 刷机包id
     * @return 刷机任务实体对象
     */
    TerminalSystemUpgradeEntity getUpgradingSystemUpgradeTaskByPackageId(UUID packageId);

    /**
     * 获取升级任务中的升级终端
     *
     * @param terminalId 终端id
     * @param taskId 升级任务id
     * @return 升级终端
     */
    TerminalSystemUpgradeTerminalEntity getSystemUpgradeTerminalByTaskId(String terminalId, UUID taskId);
}
