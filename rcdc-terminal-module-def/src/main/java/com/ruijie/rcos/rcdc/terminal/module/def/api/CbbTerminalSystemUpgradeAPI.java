package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbUpgradeableTerminalListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbAddSystemUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbGetTaskUpgradeTerminalResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbGetTerminalUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

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
     * 单次刷机任务最大刷机终端数量
     */
    long MAX_UPGRADE_NUM_PER_TASK = 500;

    /**
     * 添加刷机任务
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbAddSystemUpgradeTaskResponse addSystemUpgradeTask(CbbAddSystemUpgradeTaskRequest request) throws BusinessException;

    /**
     * 开启OTA升级任务
     *
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse startOtaUpgradeTask(CbbStartOtaUpgradeTaskRequest request) throws BusinessException;

    /**
     * 
     * 添加刷机任务终端
     * 
     * @param request 请求参数
     * @return 添加结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalNameResponse addSystemUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException;

    /**
     * 
     * 获取刷机任务列表信息
     * 
     * @param request 请求参数
     * @return 任务列表
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultPageResponse<CbbSystemUpgradeTaskDTO> listSystemUpgradeTask(PageSearchRequest request) throws BusinessException;

    /**
     * 获取刷机任务终端列表
     * 
     * @param request 请求参数
     * @return 刷机终端列表
     */
    @NoRollback
    DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> listSystemUpgradeTaskTerminal(PageSearchRequest request);

    /**
     * 关闭刷机任务
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse closeSystemUpgradeTask(IdRequest request) throws BusinessException;

    /**
     * 终端可刷机的列表
     * 
     * @param apiRequest 请求参数
     * @return 终端列表
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultPageResponse<CbbUpgradeableTerminalListDTO> listUpgradeableTerminal(
            CbbUpgradeableTerminalPageSearchRequest apiRequest) throws BusinessException;

    /**
     * 获取刷机任务终端列表
     * 
     * @param request 请求参数
     * @return 刷机终端信息列表
     */
    @NoRollback
    CbbGetTaskUpgradeTerminalResponse getUpgradeTerminalByTaskId(CbbGetTaskUpgradeTerminalRequest request);

    /**
     * 取消刷机终端
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalNameResponse cancelUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException;

    /**
     * 重试终端刷机
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalNameResponse retryUpgradeTerminal(CbbUpgradeTerminalRequest request) throws BusinessException;

    /**
     * 获取终端刷机任务信息
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbGetTerminalUpgradeTaskResponse getTerminalUpgradeTaskById(IdRequest request) throws BusinessException;

}
