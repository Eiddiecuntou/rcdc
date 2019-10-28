package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
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
     *
     * @param terminalId 终端id
     * @param shineTerminalBasicInfo 终端信息
     */
    void saveBasicInfo(String terminalId, ShineTerminalBasicInfo shineTerminalBasicInfo);

    /**
     * 修改终端名称
     * 
     * @param terminalId 终端id
     * @param terminalName 终端名称
     * @throws BusinessException 业务异常
     */
    void modifyTerminalName(String terminalId, String terminalName) throws BusinessException;

    /**
     * 修改终端网络配置
     * 
     * @param terminalId 终端id
     * @param shineNetworkConfig 终端网络信息配置
     * @throws BusinessException 业务异常
     */
    void modifyTerminalNetworkConfig(String terminalId, ShineNetworkConfig shineNetworkConfig) throws BusinessException;

    /**
     * 修改终端状态
     *
     * @param terminalId 终端id
     * @param state 终端状态
     */
    void modifyTerminalState(String terminalId, CbbTerminalStateEnums state);

    /**
     * 修改终端状态为离线状态
     *
     * @param terminalId 终端id
     */
    void modifyTerminalStateToOffline(String terminalId);

    /**
     * 判断终端是否在
     * 
     * @param terminalId 终端id
     * @return true 在线，false 离线
     */
    boolean isTerminalOnline(String terminalId);
}
