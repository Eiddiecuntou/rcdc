package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectItemStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectSpecification;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: 终端检测数据处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@Service
public class TerminalDetectServiceImpl implements TerminalDetectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalDetectServiceImpl.class);

    @Autowired
    private TerminalDetectionDAO detectionDAO;

    private static final String DETECT_FAIL_DEFAULT_MSG = "检测失败";

    @Override
    public void updateTerminalDetect(String terminalId, TerminalDetectResult detectResult) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(detectResult, "TerminalDetectResult不能为null");

        // 获取检测记录
        List<TerminalDetectionEntity> entityList = detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
        if (CollectionUtils.isEmpty(entityList)) {
            LOGGER.debug("no checking detection record, terminal id[{}]", terminalId);
            return;
        }

        for (TerminalDetectionEntity entity : entityList) {
            // 更新检测记录
            detectSuccess(detectResult, entity);
        }

    }

    private void detectSuccess(TerminalDetectResult result, TerminalDetectionEntity entity) {
        entity.setBandwidth(result.getBandwidth());
        entity.setAccessInternet(result.getAccessInternet());
        entity.setPacketLossRate(result.getPacketLossRate());
        entity.setIpConflict(result.getIpConflict());
        entity.setIpConflictMac(result.getIpConflictMac());
        entity.setNetworkDelay(result.getDelay());
        entity.setDetectState(DetectStateEnums.SUCCESS);
        detectionDAO.save(entity);
    }

    @Override
    public void detectFailure(String terminalId) {
        Assert.hasText(terminalId, "terminalId不能为空");

        List<TerminalDetectionEntity> entityList = detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
        if (CollectionUtils.isEmpty(entityList)) {
            LOGGER.debug("no checking detection record, terminal id[{}]", terminalId);
            return;
        }

        for (TerminalDetectionEntity entity : entityList) {
            // 更新检测状态
            entity.setDetectState(DetectStateEnums.ERROR);
            entity.setDetectFailMsg(DETECT_FAIL_DEFAULT_MSG);
            detectionDAO.save(entity);
        }

    }

    @Override
    public TerminalDetectionEntity save(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be null");

        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        Date now = new Date();
        entity.setTerminalId(terminalId);
        entity.setCreateTime(now);
        entity.setDetectState(DetectStateEnums.WAIT);
        detectionDAO.save(entity);
        return entity;
    }

    @Override
    public void delete(UUID id) {
        Assert.notNull(id, "terminal detection id can not be null");
        detectionDAO.deleteById(id);
    }

    @Override
    public TerminalDetectionEntity findInCurrentDate(String terminalId) {
        Assert.hasText(terminalId, "terminal id can not be blank");

        Date now = new Date();
        Date startDt = TerminalDateUtil.getDayStart(now);
        Date endDt = TerminalDateUtil.getDayEnd(now);
        List<TerminalDetectionEntity> detectionList = detectionDAO.findByTerminalIdAndCreateTimeBetween(terminalId, startDt, endDt);
        if (CollectionUtils.isEmpty(detectionList)) {
            // 当天无记录，返回null
            return null;
        }

        return detectionList.get(0);
    }

    @Override
    public Page<TerminalDetectionEntity> pageQuery(CbbTerminalDetectPageRequest request) {
        Assert.notNull(request, "request can not be null");

        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit(), new Sort(Direction.DESC, "createTime"));
        Specification<TerminalDetectionEntity> spec = new TerminalDetectSpecification(request);

        return detectionDAO.findAll(spec, pageable);
    }

    @Override
    public CbbTerminalDetectStatisticsDTO getDetectResult(CbbDetectDateEnums detectDate) {
        Assert.notNull(detectDate, "detect date can not be null");

        Date date = getDetectDate(detectDate);
        Date startDt = TerminalDateUtil.getDayStart(date);
        Date endDt = TerminalDateUtil.getDayEnd(date);
        List<TerminalDetectionEntity> detectList = detectionDAO.findByCreateTimeBetween(startDt, endDt);

        return buildDetectResultDTO(detectList);
    }

    private CbbTerminalDetectStatisticsDTO buildDetectResultDTO(List<TerminalDetectionEntity> detectList) {
        if (CollectionUtils.isEmpty(detectList)) {
            return new CbbTerminalDetectStatisticsDTO();
        }

        int ipConflict = 0;
        int bandwidth = 0;
        int accessInternet = 0;
        int packetLossRate = 0;
        int delay = 0;
        int totalAbnormalNum = 0;
        int checking = 0;
        boolean isDetectAbnormal;
        for (TerminalDetectionEntity detectEntity : detectList) {
            isDetectAbnormal = false;
            DetectStateEnums detectState = detectEntity.getDetectState();
            if (detectState == DetectStateEnums.CHECKING || detectState == DetectStateEnums.WAIT) {
                // 检测中的记录不统计异常数量
                checking++;
                continue;
            }

            if (detectState == DetectStateEnums.ERROR) {
                // 检测失败的记录加入总异常数量统计，不计入具体异常统计
                totalAbnormalNum++;
                continue;
            }

            if (isIpConflict(detectEntity.getIpConflict())) {
                ipConflict++;
                isDetectAbnormal = true;
            }

            if (isBandWidthAbnormal(detectEntity.getBandwidth())) {
                bandwidth++;
                isDetectAbnormal = true;
            }

            if (isAccessInternetAbnormal(detectEntity.getAccessInternet())) {
                accessInternet++;
                isDetectAbnormal = true;
            }

            if (isPackageLossRateAbnormal(detectEntity.getPacketLossRate())) {
                packetLossRate++;
                isDetectAbnormal = true;
            }

            if (isNetworkDelayAbnormal(detectEntity.getNetworkDelay())) {
                delay++;
                isDetectAbnormal = true;
            }

            if (isDetectAbnormal) {
                totalAbnormalNum++;
            }
        }

        CbbTerminalDetectStatisticsDTO result = new CbbTerminalDetectStatisticsDTO();
        result.setAccessInternet(accessInternet);
        result.setBandwidth(bandwidth);
        result.setDelay(delay);
        result.setIpConflict(ipConflict);
        result.setPacketLossRate(packetLossRate);
        result.setChecking(checking);
        result.setAll(totalAbnormalNum);

        return result;
    }

    private boolean isNetworkDelayAbnormal(Double networkDelay) {
        return networkDelay == null || networkDelay == Constants.TERMINAL_DETECT_ABNORMAL_COMMON_CODE
                || networkDelay >= Constants.TERMINAL_DETECT_DELAY_NORM;
    }

    private boolean isPackageLossRateAbnormal(Double packetLossRate) {
        return packetLossRate == null || packetLossRate == Constants.TERMINAL_DETECT_ABNORMAL_COMMON_CODE
                || packetLossRate >= Constants.TERMINAL_DETECT_PACKET_LOSS_RATE;
    }

    private boolean isAccessInternetAbnormal(Integer accessInternet) {
        return accessInternet == null || accessInternet == Constants.TERMINAL_DETECT_ABNORMAL_COMMON_CODE
                || accessInternet == DetectItemStateEnums.FALSE.getState();
    }

    private boolean isBandWidthAbnormal(Double bandwidth) {
        return bandwidth == null || bandwidth == Constants.TERMINAL_DETECT_ABNORMAL_COMMON_CODE
                || bandwidth <= Constants.TERMINAL_DETECT_BINDWIDTH_NORM;
    }

    private boolean isIpConflict(Integer ipConflict) {
        return ipConflict == null || ipConflict == Constants.TERMINAL_DETECT_ABNORMAL_COMMON_CODE
                || ipConflict == DetectItemStateEnums.TRUE.getState();
    }

    @Override
    public CbbTerminalDetectDTO getRecentDetect(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be null");

        TerminalDetectionEntity recentDetect = detectionDAO.findFirstByTerminalIdOrderByDetectTimeDesc(terminalId);
        if (recentDetect == null) {
            // 无终端检测信息
            return null;
        }
        CbbTerminalDetectDTO detectDTO = new CbbTerminalDetectDTO();
        recentDetect.convertTo(detectDTO);
        return detectDTO;
    }

    /**
     * 获取检测日期
     * 
     * @param detectDate 检测日期枚举
     * @return 检测日期
     */
    private Date getDetectDate(CbbDetectDateEnums detectDate) {
        Date date;
        switch (detectDate) {
            case TODAY:
                date = new Date();
                break;
            case YESTERDAY:
                date = TerminalDateUtil.addDay(new Date(), -1);
                break;
            default:
                date = new Date();
                break;
        }
        return date;
    }

}