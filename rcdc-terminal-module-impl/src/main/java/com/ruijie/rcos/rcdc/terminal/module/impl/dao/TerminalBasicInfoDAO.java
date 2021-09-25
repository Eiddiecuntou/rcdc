package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 终端基本信息表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalBasicInfoDAO extends SkyEngineJpaRepository<TerminalEntity, UUID> {

    /**
     * 获取终端详细基本信息
     *
     * @param terminalId 终端id
     * @return 返回终端信息
     */

    TerminalEntity findTerminalEntityByTerminalId(String terminalId);

    /**
     * 获取符合平台类型、授权情况的终端列表
     *
     * @param authMode 终端平台类型
     * @param authed   终端是否授权
     * @return 符合平台类型、授权情况的终端列表
     */
    List<TerminalEntity> findTerminalEntitiesByAuthModeAndAuthed(CbbTerminalPlatformEnums authMode, Boolean authed);

    /**
     * 根据terminalId获取terminalName
     *
     * @param terminalId 终端id
     * @return 返回终端名称
     */
    @Query("select terminalName from TerminalEntity where terminalId=?1")
    String getTerminalNameByTerminalId(String terminalId);

    /**
     * 获取终端详细基本信息
     *
     * @param terminalIdList 终端集合
     * @return 返回终端信息集合
     */
    List<TerminalEntity> findByTerminalIdIn(List<String> terminalIdList);

    /**
     * 根据状态查询终端列表
     *
     * @param state 终端状态
     * @return 返回终端列表
     */
    List<TerminalEntity> findTerminalEntitiesByState(CbbTerminalStateEnums state);

    /**
     * 删除终端数据
     *
     * @param terminalId 终端id
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    int deleteByTerminalId(String terminalId);

    /**
     * 修改终端名称
     *
     * @param terminalId   终端id
     * @param version      数据版本号
     * @param terminalName 终端名称
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("update TerminalEntity set terminalName=?3,version=version+1 where terminalId=?1 and version=?2")
    int modifyTerminalName(String terminalId, Integer version, String terminalName);

    /**
     * 修改终端状态为离线状态
     *
     * @param state           终端状态，在线或离线
     * @param lastOfflineTime 最后离线时间
     * @param terminalId      终端id
     * @param version         数据版本号
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("update TerminalEntity set state=?1,lastOfflineTime=?2,version=version+1 where terminalId=?3 and version=?4")
    int modifyTerminalStateOffline(CbbTerminalStateEnums state, Date lastOfflineTime, String terminalId, Integer version);

    /**
     * 根据分组id查询终端
     *
     * @param groupId 分组id
     * @return 终端列表
     */
    List<TerminalEntity> findByGroupId(UUID groupId);

    /**
     * 根据分组id、平台类型、系统类型查询终端
     *
     * @param groupId  分组id
     * @param platform 平台类型
     * @param osType   系统类型
     * @return 终端列表
     */
    List<TerminalEntity> findByGroupIdAndAuthModeAndTerminalOsType(UUID groupId, CbbTerminalPlatformEnums platform, String osType);


    /**
     * 终端类型查询运行平台类型
     *
     * @param platformArr 终端类型
     * @return 运行平台类型列表
     */
    @Query("select distinct terminalOsType from TerminalEntity where platform in ?1")
    List<String> getTerminalOsTypeByPlatform(CbbTerminalPlatformEnums[] platformArr);

    /**
     * 根据授权类型、是否授权统计终端数量
     *
     * @param authMode 终端平台类型
     * @param authed   终端是否授权
     * @return 符合终端类型、授权情况的终端数量
     */
    long countByAuthModeAndAuthed(CbbTerminalPlatformEnums authMode, Boolean authed);

    /**
     * 根据终端ID获取OCS磁盘的SN，可以为空
     *
     * @param terminalId 终端id
     * @return 磁盘SN
     */
    String getOcsSnByTerminalId(String terminalId);

    /**
     * @param ocsSn ocs磁盘序列号
     * @return 终端对象
     */
    TerminalEntity findByOcsSn(String ocsSn);
}
