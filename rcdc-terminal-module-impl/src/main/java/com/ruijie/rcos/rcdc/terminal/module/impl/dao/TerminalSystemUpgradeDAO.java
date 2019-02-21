package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.List;
import java.util.UUID;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

/**
 * 
 * Description: 终端刷机任务DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月13日
 * 
 * @author nt
 */
public interface TerminalSystemUpgradeDAO extends SkyEngineJpaRepository<TerminalSystemUpgradeEntity, UUID> {

    /**
     * 根据任务状态查询升级任务列表
     * 
     * @param state 任务状态
     * @return 任务列表
     */
    List<TerminalSystemUpgradeEntity> findByStateOrderByCreateTimeAsc(CbbSystemUpgradeTaskStateEnums state);

}
