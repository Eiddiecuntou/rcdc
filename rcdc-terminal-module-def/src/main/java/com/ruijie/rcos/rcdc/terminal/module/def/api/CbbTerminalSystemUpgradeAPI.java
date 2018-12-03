package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbBatchAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRemoveTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbBaseListResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;

/**
 * 
 * Description: 终端升级接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 *
 * @author nt
 */
public interface CbbTerminalSystemUpgradeAPI {

    /**
     * 
     * 上传终端系统升级文件
     * 
     * @param file 上传文件信息
     * @return 上传文件结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse uploadUpgradeFile(ChunkUploadFile file) throws BusinessException;

    /**
     * 
     * 获取系统升级包列表
     * 
     * @param request 请求参数
     * 
     * @return 列表查询结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbBaseListResponse<CbbTerminalSystemUpgradePackageInfoDTO> listSystemUpgradePackage(
            CbbTerminalSystemUpgradePackageListRequest request) throws BusinessException;

    /**
     * 
     * 添加终端系统升级任务
     * 
     * @param request 请求参数
     * @return 添加结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse addSystemUpgradeTask(CbbAddTerminalSystemUpgradeTaskRequest request) throws BusinessException;

    /**
     * 
     * 批量添加终端系统升级任务
     * 
     * @param request 请求参数
     * @return 批量添加结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse batchAddSystemUpgradeTask(CbbBatchAddTerminalSystemUpgradeTaskRequest request)
            throws BusinessException;

    /**
     * 移除终端系统升级任务
     * 
     * @param request 请求参数
     * @return 删除结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse removeTerminalSystemUpgradeTask(CbbRemoveTerminalSystemUpgradeTaskRequest request)
            throws BusinessException;

    /**
     * 
     * 获取终端系统升级任务列表信息
     * 
     * @return 任务列表结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbBaseListResponse<TerminalSystemUpgradeTaskDTO> listTerminalSystemUpgradeTask() throws BusinessException;


}
