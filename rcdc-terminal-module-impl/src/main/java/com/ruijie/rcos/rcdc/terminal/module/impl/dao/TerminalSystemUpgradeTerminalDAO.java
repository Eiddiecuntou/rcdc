package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 
 * Description: 终端刷机任务终端记录DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月13日
 * 
 * @author nt
 */
public interface TerminalSystemUpgradeTerminalDAO extends SkyEngineJpaRepository<TerminalSystemUpgradeTerminalEntity, UUID> {

    /**
     * 查询刷机任务的终端列表
     * 
     * @param sysUpgradeId 刷机任务id
     * @return 刷机任务终端列表
     */
    List<TerminalSystemUpgradeTerminalEntity> findBySysUpgradeId(UUID sysUpgradeId);

    /**
     * 查询刷机任务的终端
     * 
     * @param sysUpgradeId 刷机任务id
     * @param terminalId 终端id
     * @return 刷机任务终端列表
     */
    TerminalSystemUpgradeTerminalEntity findFirstBySysUpgradeIdAndTerminalId(UUID sysUpgradeId, String terminalId);

    /**
     * 查询刷机任务中指定状态的终端
     * 
     * @param sysUpgradeId 刷机任务id
     * @param state 刷机终端状态
     * @return 刷机任务终端列表
     */
    List<TerminalSystemUpgradeTerminalEntity> findBySysUpgradeIdAndState(UUID sysUpgradeId, CbbSystemUpgradeStateEnums state);

    /**
     * 根据刷机状态统计刷机数量
     *
     * @param terminalType 终端类型
     * @param state 刷机终端状态
     * @return 统计数量
     */
    int countByState(CbbTerminalTypeEnums terminalType, CbbSystemUpgradeStateEnums state);

    /**
     * 统计刷机任务指定状态的终端数量
     * 
     * @param sysUpgradeId 刷机任务id
     * @param state 刷机终端状态
     * @return 统计数量
     */
    int countBySysUpgradeIdAndState(UUID sysUpgradeId, CbbSystemUpgradeStateEnums state);

    /**
     * 获取刷机终端记录
     * 
     * @param terminalId 终端id
     * @param state 刷机状态
     * @return 刷机终端记录
     */
    List<TerminalSystemUpgradeTerminalEntity> findByTerminalIdAndState(String terminalId, CbbSystemUpgradeStateEnums state);

    /**
     *
     * @param terminalType 终端类型
     * @param state 升级状态
     * @return 升级终端记录
     */
    List<TerminalSystemUpgradeTerminalEntity> findByTerminalTypeAndState(CbbTerminalTypeEnums terminalType, CbbSystemUpgradeStateEnums state);

    /**
     * 删除终端刷机记录
     * 
     * @param terminalId 终端id
     * @return 删除记录数
     */
    int deleteByTerminalId(String terminalId);

    /**
     * 统计终端升级任务升级终端数量
     *
     * @param sysUpgradeId 升级任务id
     * @return 升级终端数量
     */
    int countBySysUpgradeId(UUID sysUpgradeId);
}
