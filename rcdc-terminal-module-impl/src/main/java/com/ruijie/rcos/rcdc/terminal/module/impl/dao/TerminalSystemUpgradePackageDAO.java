package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Description: 终端基本信息表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalSystemUpgradePackageDAO extends SkyEngineJpaRepository<TerminalSystemUpgradePackageEntity, UUID> {

    /**
     * 获取系统升级包信息
     *
     * @param packageType 升级包类型
     * @return 返回系统升级包信息
     */
    TerminalSystemUpgradePackageEntity findFirstByPackageType(CbbTerminalTypeEnums packageType);


    /**
     * 获取系统升级包列表信息
     *
     * @param packageType 升级包类型
     * @return 返回系统升级包信息
     */
    List<TerminalSystemUpgradePackageEntity> findByPackageType(CbbTerminalTypeEnums packageType);

    /**
     * 查询终端系统升级包列表
     *
     * @param isDelete 是否已删除
     * @return 升级包列表
     */
    List<TerminalSystemUpgradePackageEntity> findByIsDelete(boolean isDelete);


    /**
     * 修改升级包信息
     *
     * @param packageType    升级包类型
     * @param imgName        镜像名称
     * @param packageVersion 升级包版本号
     * @param version        版本号
     * @return 修改受影响行数
     */
    @Modifying
    @Transactional
    @Query("update TerminalSystemUpgradePackageEntity set packageVersion=:packageVersion "
            + ",imgName=:imgName,version = version + 1 where packageType=:packageType and version=:version")
    int modifyTerminalUpgradePackageVersion(@Param("imgName") String imgName, @Param("packageType") CbbTerminalPlatformEnums packageType,
                                            @Param("packageVersion") String packageVersion, @Param("version") int version);


    /**
     * 根据id 更新seed的md5
     *
     * @param id      升级包id
     * @param seedMd5 种子md5值
     */
    @Modifying
    @Transactional
    @Query(value = "update TerminalSystemUpgradePackageEntity set seedMd5=?2 where id=?1")
    void updateSeedMd5ById(UUID id, String seedMd5);

}
