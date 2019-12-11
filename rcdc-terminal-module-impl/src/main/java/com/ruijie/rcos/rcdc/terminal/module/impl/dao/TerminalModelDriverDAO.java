package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalModelDriverEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/09
 *
 * @author nt
 */
public interface TerminalModelDriverDAO extends SkyEngineJpaRepository<TerminalModelDriverEntity, UUID> {


    /**
     *  根据平台类型查询终端类型
     *
     * @param platform 平台类型
     * @return 终端类型列表
     */
    List<TerminalModelDriverEntity> findByPlatform(CbbTerminalPlatformEnums platform);

    /**
     *  根据终端类型id查询
     *
     * @param productId 终端型号id
     * @return 终端类型列表
     */
    List<TerminalModelDriverEntity> findByProductId(String productId);
}

