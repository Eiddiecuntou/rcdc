package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: 终端基本信息维护接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
public interface TerminalBasicInfoService {

    /**
     * 修改终端名称
     * @param terminalId 终端id
     * @param terminalName 终端名称
     * @throws BusinessException 业务异常
     */
    void modifyTerminalName(String terminalId, String terminalName) throws BusinessException;

    /**
     * 修改终端网络配置
     * @param terminalId 终端id
     * @param shineNetworkConfig 终端网络信息配置
     * @throws BusinessException 业务异常
     */
    void modifyTerminalNetworkConfig(String terminalId, ShineNetworkConfig shineNetworkConfig) throws BusinessException;

    /**
     * 修改终端状态
     *
     * @param terminalId 终端id
     * @param state 终端状态，在线或离线
     * @throws BusinessException 业务异常
     */
    void modifyTerminalState(String terminalId, CbbTerminalStateEnums state);
}
