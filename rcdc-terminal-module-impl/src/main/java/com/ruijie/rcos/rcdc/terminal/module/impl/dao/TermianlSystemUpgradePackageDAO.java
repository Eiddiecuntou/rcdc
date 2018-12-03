package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TermianlSystemUpgradePackageEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 终端基本信息表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TermianlSystemUpgradePackageDAO
        extends SkyEngineJpaRepository<TermianlSystemUpgradePackageEntity, UUID> {

    /**
     * 
     * 获取系统升级包信息
     * 
     * @param packageType 升级包类型
     * @return 返回系统升级包信息
     */
    TermianlSystemUpgradePackageEntity findTermianlSystemUpgradePackageByPackageType(CbbTerminalTypeEnums packageType);

    /**
     * 
     * 修改升级包信息
     * 
     * @param packageType 升级包类型
     * @param internalVersion 内部版本号
     * @param externalVersion 外部版本号
     * @return 修改受影响行数
     */
    @Modifying
    @Transactional
    @Query("update TermianlSystemUpgradePackageEntity set internalVersion=:internalVersion "
            + ",externalVersion=:externalVersion where packageType=:packageType")
    int modifyTerminalUpgradePackageVersion(CbbTerminalTypeEnums packageType, String internalVersion,
            String externalVersion);

}
