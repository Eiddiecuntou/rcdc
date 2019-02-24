package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.ViewUpgradeableTerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

/**
 * 
 * Description: 可刷机终端DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月24日
 * 
 * @author nt
 */
public interface UpgradeableTerminalDAO extends SkyEngineJpaRepository<ViewUpgradeableTerminalEntity, UUID> {

}
