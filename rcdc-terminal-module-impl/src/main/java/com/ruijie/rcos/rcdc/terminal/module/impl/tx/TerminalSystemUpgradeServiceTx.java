package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: 终端刷机存在事物的操作
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月15日
 * 
 * @author nt
 */
public interface TerminalSystemUpgradeServiceTx {

    /**
     * 添加终端刷机任务
     * @param upgradePackage 刷机包对象
     * @param terminalIdArr 终端id数组
     * @return 刷机任务id
     */
    UUID addSystemUpgradeTask(TerminalSystemUpgradePackageEntity upgradePackage, String[] terminalIdArr);

    /**
     * 结束刷机任务
     * @param upgradeTaskId 刷机任务id
     * @throws BusinessException 业务异常
     */
    void closeSystemUpgradeTask(UUID upgradeTaskId) throws BusinessException;
    
    
    /**
     * 开始等待中的终端进行刷机
     * @param upgradeTaskId 刷机任务id
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void startTerminalUpgrade(UUID upgradeTaskId, String terminalId) throws BusinessException;
    
    /**
     * 修改刷机终端状态
     * 
     * @param upgradeTaskId 刷机任务id
     * @param terminalId 终端id
     * @param state 状态
     * @throws BusinessException 业务异常
     */
    void modifySystemUpgradeTerminalState(UUID upgradeTaskId, String terminalId, CbbSystemUpgradeStateEnums state) throws BusinessException;


}
