package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalUpgradePackageUploadDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author hs
 */
public interface TerminalSystemUpgradePackageHandler {

    /**
     * 上传系统升级包
     * @param request 请求参数
     * @throws BusinessException 异常
     */
    void uploadUpgradePackage(CbbTerminalUpgradePackageUploadDTO request) throws BusinessException;

    /**
     * 系统升级包上传前处理方法
     */
    void preUploadPackage();

    /**
     * 系统升级包上传后处理方法
     */
    void postUploadPackage();

    /**
     * 获取系统升级包存放的路径
     *
     * @return 存放路径
     */
    String getUpgradePackageFileDir();

    /**
     * 检验上传文件名称是否重复
     *
     * @param fileName 文件大小
     * @return 上传文件名称是否重复
     */
    boolean checkFileNameNotDuplicate(String fileName);

    /**
     * 检验磁盘空间是否满足升级包上传
     *
     * @param fileSize 文件大小
     * @param fileStorePath 文件存放路径
     * @return 磁盘空间是否足够
     */
    boolean checkServerDiskSpaceIsEnough(Long fileSize, String fileStorePath);
}
