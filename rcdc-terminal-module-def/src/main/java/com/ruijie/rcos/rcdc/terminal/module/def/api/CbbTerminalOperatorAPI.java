package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
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
    DefaultResponse singleDetect(CbbTerminalDetectRequest request) throws BusinessException;

    /**
     * 批量终端检测
     *
     * @param request 请求参数
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse batchDetect(CbbTerminalBatDetectRequest request) throws BusinessException;

    /**
     * 获取终端收集日志状态
     * 
     * @param idRequest id请求参数
     * @return 终端日志收集状态信息
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalCollectLogStatusResponse getCollectLog(CbbTerminalIdRequest idRequest) throws BusinessException;

    /**
     * 获取终端收集日志路径
     * 
     * @param request 请求参数
     * @return 终端收集日志路径
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalLogFileInfoResponse getTerminalLogFileInfo(CbbTerminalLogNameRequest request) throws BusinessException;

}
