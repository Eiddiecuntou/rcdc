package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.TerminalTypeEnums;
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
     * 修改分组名称及父级分组
     * 
     * @param id 分组id
     * @param groupName 分组名称
     * @param parentGroupId 父级分组id
     * @param version 版本
     */
    @Transactional
    @Modifying
    @Query(value = "update TerminalGroupEntity set name = ?2, parentId = ?3, version = version + 1 where id = ?1 and version = ?4")
    void modifyGroupNameAndParent(UUID id, String groupName, UUID parentGroupId, int version);

    /**
     * 根据终端类型和终端分组父分组id查询下级分组列表
     * 
     * @param terminalType 终端类型
     * @param parentId 终端分组id
     * @return 下级分组列表
     */
    List<TerminalGroupEntity> findByTerminalTypeAndParentId(TerminalTypeEnums terminalType, UUID parentId);
    
    /**
     *  根据终端类型查询所有分组列表
     * @param terminalType 终端类型
     * @return 分组列表
     */
    List<TerminalGroupEntity> findByTerminalType(TerminalTypeEnums terminalType);

    /**
     * 根据终端类型统计分组数量
     * 
     * @param terminalType 终端类型
     * @return 同级数量
     */
    long countByTerminalType(TerminalTypeEnums terminalType);


    /**
     * 根据终端类型及父分组id统计子分组数量
     *
     * @param terminalType 终端类型
     * @param parentGroupId 父分组id
     * @return 子分组数量
     */
    long countByTerminalTypeAndParentId(TerminalTypeEnums terminalType, UUID parentGroupId);

    /**
     * 根据终端类型、父分组id及子分组名称查找分组
     *
     * @param terminalType 终端类型
     * @param groupId 组id
     * @param name 组名
     * @return TerminalGroupEntity 列表
     */
    List<TerminalGroupEntity> findByTerminalTypeAndParentIdAndName(TerminalTypeEnums terminalType, UUID groupId, String name);
}
