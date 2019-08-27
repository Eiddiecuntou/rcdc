package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年07月08日
 * 
 * @author nt
 */
public interface TerminalGroupDAO extends SkyEngineJpaRepository<TerminalGroupEntity, UUID> {

    /**
     * 根据终端类型和终端分组父分组id查询下级分组列表
     * 
     * @param parentId 终端分组id
     * @return 下级分组列表
     */
    List<TerminalGroupEntity> findByParentId(UUID parentId);

    /**
     * 根据终端类型及父分组id统计子分组数量
     *
     * @param parentGroupId 父分组id
     * @return 子分组数量
     */
    long countByParentId(UUID parentGroupId);

    /**
     * 根据终端类型、父分组id及子分组名称查找分组
     *
     * @param groupId 组id
     * @param name 组名
     * @return TerminalGroupEntity 列表
     */
    List<TerminalGroupEntity> findByParentIdAndName(UUID groupId, String name);
}
