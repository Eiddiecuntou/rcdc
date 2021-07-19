package com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalModelDriverEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 18:30
 *
 * @author TING
 */
public interface TerminalLicenseWhiteListDAO extends SkyEngineJpaRepository<TerminalModelDriverEntity, UUID> {
}
