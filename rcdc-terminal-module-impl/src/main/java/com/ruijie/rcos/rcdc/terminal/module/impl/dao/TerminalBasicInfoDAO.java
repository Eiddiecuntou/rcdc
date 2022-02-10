package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 终端基本信息表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalBasicInfoDAO extends SkyEngineJpaRepository<TerminalEntity, UUID> {

    /**
     * 获取终端详细基本信息
     *
     * @param terminalId 终端id
     * @return 返回终端信息
     */

    TerminalEntity findTerminalEntityByTerminalId(String terminalId);

    /**
     * 获取符合平台类型、授权情况的终端列表
     *
     * @param authMode 终端平台类型
     * @param authed   终端是否授权
     * @return 符合平台类型、授权情况的终端列表
     */
    List<TerminalEntity> findTerminalEntitiesByAuthModeAndAuthed(CbbTerminalPlatformEnums authMode, Boolean authed);

    /**
     * 根据terminalId获取terminalName
     *
     * @param terminalId 终端id
     * @return 返回终端名称
     */
    @Query("select terminalName from TerminalEntity where terminalId=?1")
    String getTerminalNameByTerminalId(String terminalId);

    /**
     * 获取终端详细基本信息
     *
     * @param terminalIdList 终端集合
     * @return 返回终端信息集合
     */
    List<TerminalEntity> findByTerminalIdIn(List<String> terminalIdList);

    /**
     * 根据状态查询终端列表
     *
     * @param state 终端状态
     * @return 返回终端列表
     */
    List<TerminalEntity> findTerminalEntitiesByState(CbbTerminalStateEnums state);

    /**
     * 删除终端数据
     *
     * @param terminalId 终端id
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    int deleteByTerminalId(String terminalId);

    /**
     * 修改终端名称
     *
     * @param terminalId   终端id
     * @param version      数据版本号
     * @param terminalName 终端名称
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("update TerminalEntity set terminalName=?3,version=version+1 where terminalId=?1 and version=?2")
    int modifyTerminalName(String terminalId, Integer version, String terminalName);

    /**
     * 修改终端状态为离线状态
     *
     * @param state           终端状态，在线或离线
     * @param lastOfflineTime 最后离线时间
     * @param terminalId      终端id
     * @param version         数据版本号
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("update TerminalEntity set state=?1,lastOfflineTime=?2,version=version+1 where terminalId=?3 and version=?4")
    int modifyTerminalStateOffline(CbbTerminalStateEnums state, Date lastOfflineTime, String terminalId, Integer version);

    /**
     * 根据分组id查询终端
     *
     * @param groupId 分组id
     * @return 终端列表
     */
    List<TerminalEntity> findByGroupId(UUID groupId);

    /**
     * 根据分组id、平台类型、系统类型查询终端
     *
     * @param groupId  分组id
     * @param platform 平台类型
     * @param osType   系统类型
     * @return 终端列表
     */
    List<TerminalEntity> findByGroupIdAndAuthModeAndTerminalOsType(UUID groupId, CbbTerminalPlatformEnums platform, String osType);


    /**
     * 终端类型查询运行平台类型
     *
     * @param platformArr 终端类型
     * @return 运行平台类型列表
     */
    @Query("select distinct terminalOsType from TerminalEntity where platform in ?1")
    List<String> getTerminalOsTypeByPlatform(CbbTerminalPlatformEnums[] platformArr);

    /**
     * 根据授权类型、是否授权统计终端数量
     *
     * @param authMode 终端平台类型
     * @param authed   终端是否授权
     * @return 符合终端类型、授权情况的终端数量
     */
    long countByAuthModeAndAuthed(CbbTerminalPlatformEnums authMode, Boolean authed);

    /**
     * 根据终端ID获取OCS磁盘的SN，可以为空
     *
     * @param terminalId 终端id
     * @return 磁盘SN
     */
    @Query("select ocsSn from TerminalEntity where terminalId = ?1")
    String getOcsSnByTerminalId(String terminalId);


    /**
     * @param ocsSn ocs磁盘sn
     * @return 终端列表
     */
    List<TerminalEntity> findByOcsSn(String ocsSn);


    /**
     * @param authMode        终端类型枚举
     * @param oldAuthed       旧授权
     * @param newAuthed       新授权
     * @param productTypeList 产品类型
     */
    @Transactional
    @Modifying
    @Query(value = "update TerminalEntity set authed=?3,version=version+1 where authMode=?1 and authed=?2 and ocsSn is null " +
            "and productType not in ?4")
    void updateTerminalsByAuthModeAndAuthed(CbbTerminalPlatformEnums authMode, Boolean oldAuthed, Boolean newAuthed, List<String> productTypeList);

    /**
     * 获取符合证书类型的终端未授权的终端列表
     * @param licenseType 证书类型
     * @param authMode 终端类型枚举
     * @param oldAuthed 旧授权
     * @param newAuthed 新授权
     * @param productTypeList 产品类型
     * @return 更新数量
     */
    @Transactional
    @Modifying
    @Query(value = "update t_cbb_terminal set authed=?4,version=version+1 where authed=?3 and ocs_sn is null and product_type not in ?5 "
            + "and exists(select 1 from t_cbb_terminal_authorize a where a.terminal_id=t_cbb_terminal.terminal_id "
            + "and license_type=?1 and auth_mode=?2)",
            nativeQuery = true)
    int updateTerminalsByAuthModeAndAuthedJudgeByLicenseType(String licenseType, String authMode,
                                                             Boolean oldAuthed, Boolean newAuthed, List<String> productTypeList);

    /**
     * 获取符合平台类型的终端未授权的终端列表
     *
     * @param authMode 终端类型枚举
     * @param oldAuthed 旧授权
     * @param newAuthed 新授权
     * @param productTypeList 产品类型
     * @return 更新数量
     */
    @Transactional
    @Modifying
    @Query(value = "update t_cbb_terminal set authed=?3,version=version+1 where authed=?2 and ocs_sn is null and product_type not in ?4 "
            + "and exists(select 1 from t_cbb_terminal_authorize a where a.terminal_id=t_cbb_terminal.terminal_id "
            + "and license_type like CONCAT('%',?1,'%'))",
            nativeQuery = true)
    int updateTerminalsByAuthModeAndAuthedJudgeByAuthorizeRecord(String authMode, Boolean oldAuthed, Boolean newAuthed, List<String> productTypeList);


    /**
     * 获取符合平台类型的终端未授权的终端列表
     *
     * @param platform        终端平台类型
     * @param productTypeList 产品类型
     * @return 符合平台类型、授权情况的终端列表
     */
    @Query(value = "select t.* from t_cbb_terminal t where t.authed=false and t.platform=?1 and t.ocs_sn is null and t.product_type not in ?2 and " +
            "not exists(select 1 from t_cbb_terminal_authorize a where a.terminal_id=t.terminal_id)",
            nativeQuery = true)
    List<TerminalEntity> findNoAuthedTerminalEntitiesByAuthMode(String platform, List<String> productTypeList);

    /**
     * 根据终端ID获取终端是否开启代理
     *
     * @param terminalId 终端ID
     * @return 是否开启代理
     */
    @Query(value = "select enableProxy  from TerminalEntity where terminalId = :terminalId")
    Boolean obtainEnableProxyByTerminalId(@Param("terminalId") String terminalId);

    /**
     * 获取符合平台类型和授权类型的终端未授权的终端列表
     * @param authMode  终端平台类型
     * @param licenseType 终端证书类型
     * @param productTypeWhiteList 产品类型
     * @return 符合平台类型、终端证书类型、授权情况的终端列表
     */
    @Query(value = "select t.* from t_cbb_terminal t where t.authed=false and t.platform=?1 and t.ocs_sn is null and t.product_type not in ?3 and " +
            "not exists(select 1 from t_cbb_terminal_authorize a where a.terminal_id=t.terminal_id and a.license_type=?2)",
            nativeQuery = true)
    List<TerminalEntity> findNoAuthedTerminalEntitiesByAuthModeAndLicenseType(String authMode, String licenseType, List<String> productTypeWhiteList);
}
