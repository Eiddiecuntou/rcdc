package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbModifyTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;


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
     * @param terminalId 请求参数对象
     * @throws BusinessException 业务异常
     */
    void delete(String terminalId) throws BusinessException;

    /**
     * 根据terminalId获取终端基本信息
     *
     * @param terminalId 请求参数对象
     * @return 终端基本信息DTO
     * @throws BusinessException 业务异常
     */
    CbbTerminalBasicInfoResponse findBasicInfoByTerminalId(String terminalId) throws BusinessException;

    /**
     *  编辑终端信息
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    void modifyTerminal(CbbModifyTerminalRequest request) throws BusinessException;

}
