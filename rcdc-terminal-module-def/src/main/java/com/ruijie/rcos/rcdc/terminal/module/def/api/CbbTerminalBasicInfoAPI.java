package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNetworkRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

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
    @NoRollback
    DefaultResponse delete(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 根据terminalId获取终端基本信息
     *
     * @param request 请求参数对象
     * @return 终端基本信息DTO
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalBasicInfoResponse findBasicInfoByTerminalId(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 修改终端名称
     *
     * @param request 请求参数对象
     * @return 返回成功失败状态
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse modifyTerminalName(CbbTerminalNameRequest request) throws BusinessException;

    /**
     * 修改终端网络配置
     *
     * @param request 请求参数对象
     * @return 返回成功失败状态
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse modifyTerminalNetworkConfig(CbbTerminalNetworkRequest request) throws BusinessException;

}
