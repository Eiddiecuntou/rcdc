package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCheckAllowUploadPackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckAllowUploadPackageResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;


/**
 * 
 * Description: 终端刷机包接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月17日
 * 
 * @author nt
 */
public interface CbbTerminalSystemUpgradePackageAPI {

    /**
     * 
     * 上传终端系统升级文件
     * 
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    
    void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException;

    /**
     * 
     * 删除终端系统升级文件
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */

    String deleteUpgradePackage(IdRequest request)
            throws BusinessException;

    /**
     * 
     * 获取系统升级包列表
     *
     * 
     * @return 升级包列表
     * @throws BusinessException 业务异常
     */

    CbbTerminalSystemUpgradePackageInfoDTO[] listSystemUpgradePackage()
            throws BusinessException;

    /**
     * 获取终端升级包信息
     * 
     * @param request 请求参数
     * @return 终端升级包信息
     * @throws BusinessException 业务异常
     */

    CbbTerminalSystemUpgradePackageInfoDTO getById(IdRequest request) throws BusinessException;

    /**
     * 校验升级包是否允许上传
     * 
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    
    CbbCheckAllowUploadPackageResponse checkAllowUploadPackage(CbbCheckAllowUploadPackageRequest request)
            throws BusinessException;

}
