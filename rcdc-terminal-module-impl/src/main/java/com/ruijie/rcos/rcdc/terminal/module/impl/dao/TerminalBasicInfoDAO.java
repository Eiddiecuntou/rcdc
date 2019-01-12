package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNetworkRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

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
    @Query("update TerminalEntity set name=?3,version=version+1 where terminalId=?1 and version=?2")
    int modifyTerminalName(String terminalId, Integer version, String terminalName);

    /**
     * 修改终端网络配置
     *
     * @param terminalId 终端id
     * @param version    数据版本号
     * @param network    终端网络信息配置
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("update TerminalEntity " +
            "set ip=:#{#network.ip},gateway=:#{#network.gateway},mainDns=:#{network.mainDns}," +
            "secondDns=:#{network.secondDns},getIpMode=:#{network.getIpMode.ordinal}," +
            "getDnsMode=:#{network.getDnsMode.ordinal},version=:version+1 " +
            "where terminalId=:terminalId and version=:version")
    int modifyTerminalNetworkConfig(@Param("terminalId") String terminalId, @Param("version") Integer version,
                                    @Param("network") CbbTerminalNetworkRequest network);

    /**
     * 修改终端状态
     *
     * @param terminalId 终端id
     * @param version    数据版本号
     * @param state      终端状态，在线或离线
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("update TerminalEntity set state=:state,version=version+1 where terminalId=:terminalId and version=:version")
    int modifyTerminalState(@Param("terminalId") String terminalId, @Param("version") Integer version, @Param("state") CbbTerminalStateEnums state);

}