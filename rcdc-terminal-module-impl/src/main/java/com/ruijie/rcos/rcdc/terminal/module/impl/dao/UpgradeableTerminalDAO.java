package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.ViewUpgradeableTerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import com.ruijie.rcos.sk.pagekit.api.PageQueryDAO;

import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/11
 *
 * @author nt
 */
public interface UpgradeableTerminalDAO extends SkyEngineJpaRepository<ViewUpgradeableTerminalEntity, UUID>
        , PageQueryDAO<ViewUpgradeableTerminalEntity> {

    /**
     * 根据终端平台类型和操作系统获取全部终端
     * @param platform 平台类型
     * @param osType 操作系统
     * @return 终端信息
     */
    List<ViewUpgradeableTerminalEntity> findAllByPlatformEqualsAndTerminalOsTypeEquals(CbbTerminalPlatformEnums platform, String osType);

}

