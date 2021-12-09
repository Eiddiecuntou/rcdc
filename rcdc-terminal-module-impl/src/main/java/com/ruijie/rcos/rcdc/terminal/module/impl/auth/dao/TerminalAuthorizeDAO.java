package com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 18:30
 *
 * @author TING
 */
public interface TerminalAuthorizeDAO extends SkyEngineJpaRepository<TerminalAuthorizeEntity, UUID> {

    /**
     * 查询授权终端数
     *
     * @param licenseType 授权证书类型
     * @param authMode    授权模式
     * @return 数量
     */
    int countByLicenseTypeAndAuthMode(String licenseType, CbbTerminalPlatformEnums authMode);

    /**
     * 根据终端id删除授权记录
     *
     * @param terminalId 终端id
     */
    @Modifying
    @Transactional
    void deleteByTerminalId(String terminalId);

    /**
     * 根据AuthMode删除终端
     * @param authMode authMode
     */
    @Modifying
    @Transactional
    void deleteByAuthMode(CbbTerminalPlatformEnums authMode);

    /**
     * 根据终端id查找
     *
     * @param terminalId 终端id
     * @return 终端授权记录
     */
    TerminalAuthorizeEntity findByTerminalId(String terminalId);

    /**
     * 查询授权终端记录
     *
     * @param licenseType 证书类型
     * @param authMode    授权模式
     * @return 授权终端记录
     */
    List<TerminalAuthorizeEntity> findByLicenseTypeAndAuthMode(String licenseType, CbbTerminalPlatformEnums authMode);

    /**
     * 查询授权终端数
     *
     * @param licenseType 授权证书类型
     * @return 数量
     */
    int countByLicenseTypeContaining(String licenseType);

    /**
     * 根据terminalId计算授权条目
     *
     * @param terminalId 终端id
     * @return 终端在表中的记录数
     */
    int countByTerminalId(String terminalId);

    /**
     * 根据符合平台类型和旧授权更新成新的授权状态
     *
     * @param platform        终端平台类型
     * @param oldAuthed       旧授权
     * @param newAuthed       新授权
     * @param productTypeList 产品类型
     */
    @Transactional
    @Modifying
    @Query(value = "update t_cbb_terminal_authorize SET authed=?3, version=version+1 where exists(select 1 from t_cbb_terminal t where" +
            " t.terminal_id=terminal_id and t.platform=?1 and t.ocs_sn is null and t.product_type not in ?4) and authed=?2", nativeQuery = true)
    void updateTerminalAuthorizesByPlatformAndAuthed(String platform, Boolean oldAuthed, Boolean newAuthed, List<String> productTypeList);
}
