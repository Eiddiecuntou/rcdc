package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import java.util.List;
import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: 终端刷机包
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月17日
 * 
 * @author nt
 */
public interface TerminalSystemUpgradePackageService {

    /**
     * 
     * 保存终端升级版本信息
     * 
     * @param versionInfo 升级版本信息
     * @throws BusinessException 业务异常
     */
    void saveTerminalUpgradePackage(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException;

    /**
     * 读取终端刷机成功状态
     * 
     * @return 升级状态信息集合
     * @throws BusinessException 业务异常
     */
    List<TerminalSystemUpgradeInfo> readSystemUpgradeSuccessStateFromFile() throws BusinessException;

    /**
     * 读取终端刷机开始状态
     * 
     * @return 升级状态信息集合
     * @throws BusinessException 业务异常
     */
    List<TerminalSystemUpgradeInfo> readSystemUpgradeStartStateFromFile() throws BusinessException;

    /**
     * 获取刷机包
     * 
     * @param upgradePackageId 刷机包id
     * @return 刷机包对象
     * @throws BusinessException 业务异常
     */
    TerminalSystemUpgradePackageEntity getSystemUpgradePackage(UUID upgradePackageId) throws BusinessException;

    /**
     * 软删除终端升级包
     * 
     * @param packageId 终端升级包id
     * @throws BusinessException 业务异常
     */
    void deleteSoft(UUID packageId) throws BusinessException;

}
