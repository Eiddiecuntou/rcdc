package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;


import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: 端系统升级处理对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @param <T> 返回的升级对象信息
 *
 * @author nt
 */
public interface TerminalSystemUpgradeHandler<T> {

    /**
     * 终端系统升级版本检测
     * 
     * @param terminalType 终端类型
     * @param terminalEntity 终端id
     * @return 终端系统升级检测结果
     * @throws BusinessException 业务异常
     */
    SystemUpgradeCheckResult<T> checkSystemUpgrade(CbbTerminalTypeEnums terminalType, TerminalEntity terminalEntity) throws BusinessException;


    /**
     * 添加升级任务后进行的处理
     *
     * @param upgradePackage 升级包对象
     * @throws BusinessException 业务异常
     */
    void afterAddSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException;

    /**
     * 关闭升级任务后进行的处理
     *
     * @param upgradePackage 升级包对象
     * @throws BusinessException 业务异常
     */
    void afterCloseSystemUpgrade(TerminalSystemUpgradePackageEntity upgradePackage) throws BusinessException;

    /**
     * 获取下发终端升级消息
     * 
     * @param upgradePackage 升级包对象
     * @param upgradeTaskId 升级任务id
     * @param upgradeMode 升级模式
     * @return 升级消息对象
     *
     * @throws BusinessException 业务异常
     */
    Object getSystemUpgradeMsg(TerminalSystemUpgradePackageEntity upgradePackage, UUID upgradeTaskId, CbbSystemUpgradeModeEnums upgradeMode)
            throws BusinessException;

    /**
     * 检验是否可以开始升级
     *
     * @param terminalId 终端id
     * @return 是否能够开始升级
     */
    boolean checkAndHoldUpgradeQuota(String terminalId);

    /**
     * 判断终端是否能够升级
     *
     * @param terminalEntity 终端id
     * @param terminalType 终端类型
     * @return boolean
     */
    boolean isTerminalEnableUpgrade(TerminalEntity terminalEntity, CbbTerminalTypeEnums terminalType);

    /**
     * 释放升级占用的位置
     *
     * @param terminalId 终端id
     */
    void releaseUpgradeQuota(String terminalId);

}
