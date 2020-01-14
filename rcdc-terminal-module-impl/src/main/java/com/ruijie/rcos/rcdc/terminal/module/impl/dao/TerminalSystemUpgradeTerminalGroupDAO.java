package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalGroupEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Description: 终端刷机任务终端分组记录DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月13日
 *
 * @author nt
 */
public interface TerminalSystemUpgradeTerminalGroupDAO extends SkyEngineJpaRepository<TerminalSystemUpgradeTerminalGroupEntity, UUID> {

    /**
     * 查询刷机任务的终端分组列表
     *
     * @param sysUpgradeId 刷机任务id
     * @return 刷机任务终端分组列表
     */
    List<TerminalSystemUpgradeTerminalGroupEntity> findBySysUpgradeId(UUID sysUpgradeId);

    /**
     * 查询刷机任务的终端分组
     *
     * @param sysUpgradeId    刷机任务id
     * @param terminalGroupId 终端分组id
     * @return 刷机任务终端分组
     */
    TerminalSystemUpgradeTerminalGroupEntity findBySysUpgradeIdAndTerminalGroupId(UUID sysUpgradeId, UUID terminalGroupId);

    /**
     *  根据升级任务id删除升级分组信息
     *
     * @param sysUpgradeId 系统升级任务id
     */
    void deleteBySysUpgradeId(UUID sysUpgradeId);

    /**
     * 根据分组id删除升级的分组记录
     *
     * @param groupId 分组id
     */
    void deleteByTerminalGroupId(UUID groupId);
}
