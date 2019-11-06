package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalNetworkInfoEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 终端基本信息表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/25
 *
 * @author nt
 */
public interface TerminalNetworkInfoDAO extends SkyEngineJpaRepository<TerminalNetworkInfoEntity, UUID> {

    /**
     *  根据终端id删除终端网络信息
     * @param terminalId 终端id
     * @return 影响行数
     */
    @Modifying
    @Transactional
    int deleteByTerminalId(String terminalId);

    /**
     *  根据终端id获取终端网络信息列表
     *
     * @param terminalId 终端id
     * @return 终端网络信息列表
     */
    List<TerminalNetworkInfoEntity> findByTerminalId(String terminalId);
}
