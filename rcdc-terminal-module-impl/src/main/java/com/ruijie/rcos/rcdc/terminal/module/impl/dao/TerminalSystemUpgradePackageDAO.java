package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

/**
 * Description: 终端基本信息表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalSystemUpgradePackageDAO
        extends SkyEngineJpaRepository<TerminalSystemUpgradePackageEntity, UUID> {

    /**
     * 
     * 获取系统升级包信息
     * 
     * @param packageType 升级包类型
     * @return 返回系统升级包信息
     */
    TerminalSystemUpgradePackageEntity findFirstByPackageType(CbbTerminalTypeEnums packageType);


    /**
     * 
     * 获取系统升级包列表信息
     * 
     * @param packageType 升级包类型
     * @return 返回系统升级包信息
     */
    List<TerminalSystemUpgradePackageEntity> findByPackageType(CbbTerminalTypeEnums packageType);


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
    @Query("update TerminalSystemUpgradePackageEntity set packageVersion=:packageVersion "
            + ",imgName=:imgName,version = version + 1 where packageType=:packageType and version=:version")
    int modifyTerminalUpgradePackageVersion(@Param("imgName") String imgName,
            @Param("packageType") CbbTerminalTypeEnums packageType, @Param("packageVersion") String packageVersion,
            @Param("version") int version);

}
