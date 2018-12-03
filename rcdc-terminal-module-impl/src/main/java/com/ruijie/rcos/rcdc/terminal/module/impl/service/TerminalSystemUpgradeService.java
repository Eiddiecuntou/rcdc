package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import java.util.List;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: 终端升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author "nt"
 */
public interface TerminalSystemUpgradeService {

    /**
     * 
     * 修改终端升级版本信息
     * 
     * @param versionInfo 升级版本信息
     * @throws BusinessException 业务异常
     */
    void modifyTerminalUpgradePackageVersion(TerminalUpgradeVersionFileInfo versionInfo)
            throws BusinessException;

    /**
     * 
     * 添加终端升级版本信息
     * 
     * @param versionInfo 终端升级版本信息
     * @throws BusinessException 业务异常
     */
    void addTerminalUpgradePackage(TerminalUpgradeVersionFileInfo versionInfo)
            throws BusinessException;

    /**
     * 读取系统升级状态
     * 
     * @return 升级状态信息集合
     */
    List<TerminalSystemUpgradeInfo> readSystemUpgradeStateFromFile();

    /**
     * 系统升级
     * 
     * @param terminalId 终端id
     * @param upgradeMsg 升级信息
     * @throws BusinessException 业务异常
     */
    void systemUpgrade(String terminalId, TerminalSystemUpgradeMsg upgradeMsg) throws BusinessException;

}
