package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.ViewUpgradeableTerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/11
 *
 * @author nt
 */
public interface UpgradeableTerminalDAO extends SkyEngineJpaRepository<ViewUpgradeableTerminalEntity, UUID> {

}

