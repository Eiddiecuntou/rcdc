package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.List;
import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

/**
 * 
 * Description: 终端刷机任务终端记录DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月13日
 * 
 * @author nt
 */
public interface TerminalSystemUpgradeTerminalDAO
        extends SkyEngineJpaRepository<TerminalSystemUpgradeTerminalEntity, UUID> {

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
    List<TerminalSystemUpgradeTerminalEntity> findBySysUpgradeIdAndState(UUID sysUpgradeId,
            CbbSystemUpgradeStateEnums state);

    /**
     * 根据刷机状态统计刷机数量
     * 
     * @param state 刷机终端状态
     * @return 统计数量
     */
    int countByState(CbbSystemUpgradeStateEnums state);

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
    List<TerminalSystemUpgradeTerminalEntity> findByTerminalIdAndState(String terminalId,
            CbbSystemUpgradeStateEnums state);

}
