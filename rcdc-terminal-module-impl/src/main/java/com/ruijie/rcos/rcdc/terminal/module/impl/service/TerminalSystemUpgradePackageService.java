package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

import java.util.List;
import java.util.UUID;

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

    /**
     *
     * 判断是否正在上传刷机包
     *
     * @param terminalType 终端类型
     * @return 上传文件结果
     */
    @NoRollback
    boolean isUpgradeFileUploading(CbbTerminalTypeEnums terminalType);

    /**
     *
     * @param request 请求参数
     * @param terminalType 终端类型
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request, CbbTerminalTypeEnums terminalType) throws BusinessException;

}
