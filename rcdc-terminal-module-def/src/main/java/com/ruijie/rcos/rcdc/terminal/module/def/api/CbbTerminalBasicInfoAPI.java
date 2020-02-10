package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbModifyTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNetworkInfoResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;


/**
 * Description: 终端基本信息操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface CbbTerminalBasicInfoAPI {

    /**
     * 删除终端信息
     *
     * @param request 请求参数对象
     * @return 返回成功失败状态
     * @throws BusinessException 业务异常
     */
    DefaultResponse delete(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 根据terminalId获取终端基本信息
     *
     * @param request 请求参数对象
     * @return 终端基本信息DTO
     * @throws BusinessException 业务异常
     */
    CbbTerminalBasicInfoResponse findBasicInfoByTerminalId(CbbTerminalIdRequest request) throws BusinessException;

    /**
     *  编辑终端信息
     *
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    DefaultResponse modifyTerminal(CbbModifyTerminalRequest request) throws BusinessException;


    /**
     *  获取终端网络信息
     *
     * @param request 请求参数
     * @return 终端网络信息
     */
    CbbTerminalNetworkInfoResponse getTerminalNetworkInfo(CbbTerminalIdRequest request);
}
