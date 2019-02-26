package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckUploadingResultResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

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
     * @return 上传文件结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse uploadUpgradeFile(CbbTerminalUpgradePackageUploadRequest request) throws BusinessException;
    
    /**
     * 
     * 判断是否正在上传刷机包
     * 
     * @param request 请求参数
     * @return 上传文件结果
     */
    @NoRollback
    CbbCheckUploadingResultResponse isUpgradeFileUploading(CbbTerminalPlatformRequest request);
    
    /**
     * 
     * 获取系统升级包分页列表
     * 
     * @param request 请求参数
     * 
     * @return 分页列表查询结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultPageResponse<CbbTerminalSystemUpgradePackageInfoDTO> listSystemUpgradePackage(
            PageSearchRequest request) throws BusinessException;

}
