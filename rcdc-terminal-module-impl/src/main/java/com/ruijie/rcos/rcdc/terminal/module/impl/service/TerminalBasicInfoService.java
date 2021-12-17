package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
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
     * @param terminalEntity 终端实体类
     * @param shineTerminalBasicInfo 终端信息
     * @param authed 是否授权
     */
    void saveBasicInfo(TerminalEntity terminalEntity, CbbShineTerminalBasicInfo shineTerminalBasicInfo, Boolean authed);

    /**
     * 根据终端信息获取终端类型
     * @param terminalEntity 终端实体类
     * @return 终端类型
     */
    CbbTerminalTypeEnums obtainTerminalType(TerminalEntity terminalEntity);

    /**
     * 通过shine上报的终端基本信息，构建终端entity
     * @param terminalId 终端id
     * @param isNewConnection 是否是新连接
     * @param shineTerminalBasicInfo shine上报的终端基本信息
     * @return basicInfoEntity 终端entity
     */
    TerminalEntity convertBasicInfo2TerminalEntity(String terminalId, boolean isNewConnection,
        CbbShineTerminalBasicInfo shineTerminalBasicInfo);

    /**
     * 自学习终端型号
     * @param basicInfo shine上报的终端基本信息
     */
    void saveTerminalModel(CbbShineTerminalBasicInfo basicInfo);

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

    /**
     *  根据终端信息获取终端架构类型
     *
     * @param basicInfoEntity 终端信息
     * @return 终端架构类型
     */
    TerminalTypeArchType obtainTerminalArchType(TerminalEntity basicInfoEntity);
}
