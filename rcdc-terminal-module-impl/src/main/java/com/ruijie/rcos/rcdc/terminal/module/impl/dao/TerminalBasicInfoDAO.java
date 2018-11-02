package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.TerminalNetworkRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalBasicInfoEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Description: 终端基本信息表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalBasicInfoDAO extends SkyEngineJpaRepository<TerminalBasicInfoEntity, UUID> {

    /**
     * 获取终端详细基本信息
     *
     * @param terminalId
     * @return
     */
    TerminalBasicInfoEntity findTerminalBasicInfoEntitiesByTerminalId(String terminalId);

    @Modifying
    @Transactional
    @Query("delete from TerminalBasicInfoEntity where terminalId=:terminalId and version=:version")
    int deleteByTerminalId(String terminalId, Integer version);

    /**
     * 修改终端名称
     *
     * @param terminalId
     * @param version
     * @param terminalName
     * @return
     */
    @Modifying
    @Transactional
    @Query("update TerminalBasicInfoEntity set name=:terminalName,version=version+1 where terminalId=:terminalId and " +
            "version=:version")
    int modifyTerminalName(String terminalId, Integer version, String terminalName);

    /**
     * 修改终端网络配置
     *
     * @param terminalId
     * @param version
     * @param network
     * @return
     */
    @Modifying
    @Transactional
    @Query("update TerminalBasicInfoEntity " +
            "set ip=:#{#network.ip},gateway=:#{#network.gateway},mainDns=:#{network.mainDns}," +
            "secondDns=:#{network.secondDns},getIpMode=:#{network.getIpMode.ordinal}," +
            "getDnsMode=:#{network.getDnsMode.ordinal},version=:version+1 " +
            "where terminalId=:terminalId and version=:version")
    int modifyTerminalNetworkConfig(String terminalId, Integer version, TerminalNetworkRequest network);
}