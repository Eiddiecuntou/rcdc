package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalOfflineCommandEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

import java.util.UUID;

/**
 * Description: 终端离线命令表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalOfflineCommandDAO extends SkyEngineJpaRepository<TerminalOfflineCommandEntity, UUID> {

}
