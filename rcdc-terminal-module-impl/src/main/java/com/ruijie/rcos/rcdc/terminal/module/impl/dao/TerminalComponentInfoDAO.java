package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalComponentInfoEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

import java.util.UUID;

/**
 * Description: 终端系统内组件信息表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalComponentInfoDAO extends SkyEngineJpaRepository<TerminalComponentInfoEntity, UUID> {

}
