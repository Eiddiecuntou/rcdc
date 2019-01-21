package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBatDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalIdResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

/**
 * Description: 终端操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/5
 *
 * @author Jarman
 */
public interface CbbTerminalOperatorAPI {

    /**
     * 关闭终端
     *
     * @param request 终端id请求参数对象
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse shutdown(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 重启终端
     *
     * @param request 终端id请求参数对象
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse restart(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 修改终端管理员密码
     *
     * @param request 修改密码请求参数对象
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse changePassword(CbbChangePasswordRequest request) throws BusinessException;

    /**
     * 收集终端日志
     *
     * @param request 终端id请求参数对象
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse collectLog(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 终端检测
     *
     * @param request 终端检测请求参数对象
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse detect(CbbTerminalDetectRequest request) throws BusinessException;

    /**
     * 批量终端检测
     *
     * @param request 请求参数
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse detect(CbbTerminalBatDetectRequest request) throws BusinessException;

    /**
     * 获取终端日志文件名
     *
     * @param request 终端id请求参数对象
     * @return 返回日志文件名
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalNameResponse getTerminalLogName(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 获取检测记录分页列表
     * 
     * @param request 分页请求参数
     * @return 检测记录分页列表
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultPageResponse<CbbTerminalDetectDTO> listDetect(CbbTerminalDetectPageRequest request) throws BusinessException;
    
    /**
     * 获取终端最后检测记录
     * 
     * @param request 请求参数
     * @return 检测记录信息
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbDetectInfoResponse getRecentDetect(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 获取终端检测记录结果
     * 
     * @param request 检测结果请求参数
     * @return 终端检测结果
     */
    @NoRollback
    CbbDetectResultResponse getDetectResult(CbbTerminalDetectResultRequest request);

    /**
     * 获取终端基本信息
     * 
     * @param request id请求参数
     * @return 终端基本信息
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalBasicInfoResponse getTerminalBaiscInfo(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 获取终端收集日志状态
     * 
     * @param idRequest
     * @return
     * @throws BusinessException
     */
    @NoRollback
    CbbTerminalCollectLogStatusResponse getCollectLog(CbbTerminalIdRequest idRequest) throws BusinessException;

}
