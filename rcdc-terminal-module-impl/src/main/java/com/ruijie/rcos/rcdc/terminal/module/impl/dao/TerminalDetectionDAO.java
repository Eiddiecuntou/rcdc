package com.ruijie.rcos.rcdc.terminal.module.impl.dao;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.sk.modulekit.api.ds.SkyEngineJpaRepository;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
     * @param terminalId
     * @return
     */
    TerminalDetectionEntity findFirstByTerminalIdOrderByDetectTimeDesc(String terminalId);

    int countByDetectState(DetectStateEnums checking);

    /**
     * 获取终端时间段内的检测记录
     * @param terminalId 终端id
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return
     */
    List<TerminalDetectionEntity> findByTerminalIdAndDetectTimeBetween(String terminalId, Date startDt, Date endDt);

    /**
     * 获取指定状态的检测记录
     * @param terminalId
     * @param detectState
     * @return
     */
    List<TerminalDetectionEntity> findByTerminalIdAndDetectState(String terminalId, DetectStateEnums detectState);

    /**
     * 统计时间段内的ip冲突状态数量
     * @param state ip冲突状态  0: 未冲突   1: 冲突
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByIpConflictAndDetectTimeBetween(int state, Date startDt, Date endDt);

    /**
     * 统计时间段内的小于等于标准带宽的数量
     * @param bindwidthNorm 带宽标准值
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByBandwidthLessThanEqualAndDetectTimeBetween(int bindwidthNorm, Date startDt, Date endDt);

    /**
     * 统计时间段内的网络访问状态数量
     * @param state 网络访问状态  0:可访问 1 不可访问
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByAssessInternetAndDetectTimeBetween(int state, Date startDt, Date endDt);

    /**
     * 统计时间段内的丢包率大于等于标准的数量
     * @param packetLossRateNorm 丢包率标准值
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByPacketLossRateGreaterThanEqualAndDetectTimeBetween(double packetLossRateNorm, Date startDt, Date endDt);

    /**
     * 统计时间段内的时延大于等于标准的数量
     * @param delayNorm 时延标准值
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByNetworkDelayGreaterThanEqualAndDetectTimeBetween(int delayNorm, Date startDt, Date endDt);

    /**
     * 统计时间段内对应检测状态的数量
     * @param detectState 检测状态
     * @param startDt 开始时间
     * @param endDt 结束时间
     * @return 统计数量
     */
    int countByDetectStateAndDetectTimeBetween(DetectStateEnums detectState, Date startDt, Date endDt);

    /**
     * 更新所有检测中记录为检测失败
     * @param fromState 待更新状态
     * @param targetState 更新后状态
     */
    @Transactional
    @Modifying
    @Query(value = "update TerminalDetectionEntity set detectState = ?2 where detectState = ?1")
    int modifyDetectionCheckingToFail(DetectStateEnums fromState, DetectStateEnums targetState);

}
