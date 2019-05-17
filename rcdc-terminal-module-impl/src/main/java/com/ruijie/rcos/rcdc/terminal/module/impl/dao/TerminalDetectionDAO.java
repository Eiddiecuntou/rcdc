package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;

/**
 * Description: 终端检测表DAO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalDetectionDAO extends SkyEngineJpaRepository<TerminalDetectionEntity, UUID> {

    /**
     * 获取终端最后检测结果
     * 
     * @param terminalId 终端id
     * @return 终端检测结果
     */
    TerminalDetectionEntity findFirstByTerminalIdOrderByDetectTimeDesc(String terminalId);

    /**
     * 获取终端时间段内的检测记录
     * 
     * @param terminalId 终端id
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 终端检测结果列表
     */
    List<TerminalDetectionEntity> findByTerminalIdAndCreateTimeBetween(String terminalId, Date startDt, Date endDt);

    /**
     * 获取指定状态的检测记录
     * 
     * @param terminalId 终端id
     * @param detectState 检测状态
     * @return 终端检测结果列表
     */
    List<TerminalDetectionEntity> findByTerminalIdAndDetectState(String terminalId, DetectStateEnums detectState);

    /**
     * 统计时间段内的ip冲突状态数量
     * 
     * @param state ip冲突状态 0: 未冲突 1: 冲突
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByIpConflictAndDetectTimeBetween(int state, Date startDt, Date endDt);

    /**
     * 统计时间段内的小于等于标准带宽的数量
     * 
     * @param bindwidthNorm 带宽标准值
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByBandwidthLessThanEqualAndDetectTimeBetween(double bindwidthNorm, Date startDt, Date endDt);

    /**
     * 统计时间段内的网络访问状态数量
     * 
     * @param state 网络访问状态 0:可访问 1 不可访问
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByAccessInternetAndDetectTimeBetween(int state, Date startDt, Date endDt);

    /**
     * 统计时间段内的丢包率大于等于标准的数量
     * 
     * @param packetLossRateNorm 丢包率标准值
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByPacketLossRateGreaterThanEqualAndDetectTimeBetween(double packetLossRateNorm, Date startDt, Date endDt);

    /**
     * 统计时间段内的时延大于等于标准的数量
     * 
     * @param delayNorm 时延标准值
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByNetworkDelayGreaterThanEqualAndDetectTimeBetween(double delayNorm, Date startDt, Date endDt);

    /**
     * 统计时间段内对应检测状态的数量
     * 
     * @param detectState 检测状态
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByDetectStateAndDetectTimeBetween(DetectStateEnums detectState, Date startDt, Date endDt);

    /**
     * 更新检测状态
     * 
     * @param fromState 待更新状态
     * @param targetState 更新后状态
     * @return 更新影响的行数
     */
    @Transactional
    @Modifying
    @Query(value = "update TerminalDetectionEntity set detectState = ?2 where detectState = ?1")
    int modifyDetectionCheckingToFail(DetectStateEnums fromState, DetectStateEnums targetState);

    /**
     * 获取指定时间之前对应状态的检测记录
     * 
     * @param state 检测状态
     * @param date 时间
     * @return 检测记录列表
     */
    List<TerminalDetectionEntity> findByDetectStateAndDetectTimeBefore(DetectStateEnums state, Date date);

    /**
     * 删除终端检测记录
     * 
     * @param terminalId 终端id
     * @return 删除影响行数
     */
    int deleteByTerminalId(String terminalId);

    /**
     * 获取终端检测记录列表
     * 
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 检测列表
     */
    List<TerminalDetectionEntity> findByCreateTimeBetween(Date startDt, Date endDt);

    /**
     * 根据状态查询第一个检测记录
     *
     * @param state 检测记录状态
     * @return 检测记录
     */
    TerminalDetectionEntity findFirstByDetectStateOrderByCreateTime(DetectStateEnums state);
}
