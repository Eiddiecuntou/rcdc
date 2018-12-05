package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNetworkRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.CbbTerminalEntity;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
public interface TerminalBasicInfoDAO extends SkyEngineJpaRepository<CbbTerminalEntity, UUID> {

    /**
     * 获取终端详细基本信息
     *
     * @param terminalId 终端id
     * @return 返回终端信息
     */
    CbbTerminalEntity findFirstByTerminalId(String terminalId);
    
    /**
     * 获取终端详细基本信息
     *
     * @param terminalIdList 终端集合
     * @return 返回终端信息集合
     */
    List<CbbTerminalEntity> findByTerminalIdIn(List<String> terminalIdList);

    /**
     * 删除终端数据
     *
     * @param terminalId 终端id
     * @param version    数据版本号
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("delete from CbbTerminalEntity where terminalId=:terminalId and version=:version")
    int deleteByTerminalId(String terminalId, Integer version);

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
    @Query("update CbbTerminalEntity set name=:terminalName,version=version+1 where terminalId=:terminalId and " +
            "version=:version")
    int modifyTerminalName(String terminalId, Integer version, String terminalName);

    /**
     * 修改终端网络配置
     *
     * @param terminalId 终端id
     * @param version    数据版本号
     * @param network    终端网络信息配置
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("update CbbTerminalEntity " +
            "set ip=:#{#network.ip},gateway=:#{#network.gateway},mainDns=:#{network.mainDns}," +
            "secondDns=:#{network.secondDns},getIpMode=:#{network.getIpMode.ordinal}," +
            "getDnsMode=:#{network.getDnsMode.ordinal},version=:version+1 " +
            "where terminalId=:terminalId and version=:version")
    int modifyTerminalNetworkConfig(String terminalId, Integer version, CbbTerminalNetworkRequest network);


    /**
     * 修改终端状态
     *
     * @param terminalId 终端id
     * @param version    数据版本号
     * @param state      终端状态，在线或离线
     * @return 返回影响行数
     */
    @Modifying
    @Transactional
    @Query("update CbbTerminalEntity set state=:state,version=:version+1 " +
            "where terminalId=:terminalId and version=:version")
    int modifyTerminalState(String terminalId, Integer version, Integer state);

    /**
     * 修改终端检测信息
     *
     * @param terminalId  终端id
     * @param version     数据版本号
     * @param detectTime  检测时间
     * @param detectState 检测状态
     * @return 影响行数
     */
    @Modifying
    @Transactional
    @Query("update CbbTerminalEntity set detectState=:detectState" +
            ",detectTime=:detectTime" +
            ",version=:version+1 " +
            "where terminalId=:terminalId and version=:version")
    int modifyDetectInfo(String terminalId, Integer version, Date detectTime, Integer detectState);

    /**
     * 根据检查状态查询基本信息列表
     *
     * @param state 检测状态
     * @return 基本信息列表
     */
    List<CbbTerminalEntity> findTerminalBasicInfoEntitiesByDetectState(CbbDetectStateEnums state);
}