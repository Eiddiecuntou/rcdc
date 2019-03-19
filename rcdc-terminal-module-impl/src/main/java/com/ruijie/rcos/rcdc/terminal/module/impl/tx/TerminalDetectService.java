package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import java.util.Date;
import java.util.List;
import java.util.UUID;
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
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectItemStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
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
public class TerminalDetectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalDetectService.class);

    @Autowired
    private TerminalDetectionDAO detectionDAO;

    private static final String DETECT_FAIL_DEFAULT_MSG = "检测失败";

    /**
     * 更新基础信息表和检测表，存在事务
     *
     * @param terminalId 终端id
     * @param detectResult 检测结果数据对象
     */
    public void updateTerminalDetect(String terminalId, TerminalDetectResult detectResult) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(detectResult, "TerminalDetectResult不能为null");

        // 获取检测记录
        List<TerminalDetectionEntity> entityList =
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
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

    /**
     * 检测失败
     * 
     * @param terminalId 终端id
     */
    public void detectFailure(String terminalId) {
        Assert.hasText(terminalId, "terminalId不能为空");

        List<TerminalDetectionEntity> entityList =
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
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

    /**
     * 保存终端检测记录
     * 
     * @param terminalId 终端id
     * @return 终端检测记录
     */
    public TerminalDetectionEntity save(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be null");

        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        Date now = new Date();
        entity.setTerminalId(terminalId);
        entity.setDetectTime(now);
        entity.setDetectState(DetectStateEnums.CHECKING);
        detectionDAO.save(entity);
        return entity;
    }

    /**
     * 删除检测记录
     * 
     * @param id 检测记录id
     */
    public void delete(UUID id) {
        Assert.notNull(id, "terminal detection id can not be null");
        detectionDAO.deleteById(id);
    }

    /**
     * 获取终端当天的检测记录
     * 
     * @param terminalId 终端id
     * @return 当天的终端检测记录，无记录返回null
     */
    public TerminalDetectionEntity findInCurrentDate(String terminalId) {
        Assert.hasText(terminalId, "terminal id can not be blank");

        Date now = new Date();
        Date startDt = TerminalDateUtil.getDayStart(now);
        Date endDt = TerminalDateUtil.getDayEnd(now);
        List<TerminalDetectionEntity> detectionList =
                detectionDAO.findByTerminalIdAndDetectTimeBetween(terminalId, startDt, endDt);
        if (CollectionUtils.isEmpty(detectionList)) {
            // 当天无记录，返回null
            return null;
        }

        return detectionList.get(0);
    }

    /**
     * 终端检测记录分页查询
     * 
     * @param request 分页查询请求参数
     * @return 分页列表
     */
    public Page<TerminalDetectionEntity> pageQuery(CbbTerminalDetectPageRequest request) {
        Assert.notNull(request, "request can not be null");

        Pageable pageable =
                PageRequest.of(request.getPage(), request.getLimit(), new Sort(Direction.DESC, "detectTime"));
        Specification<TerminalDetectionEntity> spec = new TerminalDetectSpecification(request.getDate());

        return detectionDAO.findAll(spec, pageable);
    }

    /**
     * 获取检测结果
     * 
     * @param detectDate 日期
     * @return 检测结果
     */
    public CbbTerminalDetectResultDTO getDetectResult(CbbDetectDateEnums detectDate) {
        Assert.notNull(detectDate, "detect date can not be null");

        Date date = getDetectDate(detectDate);
        Date startDt = TerminalDateUtil.getDayStart(date);
        Date endDt = TerminalDateUtil.getDayEnd(date);

        int ipConflict = detectionDAO.countByIpConflictAndDetectTimeBetween(DetectItemStateEnums.TRUE.getState(),
                startDt, endDt);
        int bandwidth = detectionDAO.countByBandwidthLessThanEqualAndDetectTimeBetween(
                Constants.TERMINAL_DETECT_BINDWIDTH_NORM, startDt, endDt);
        int accessInternet = detectionDAO
                .countByAccessInternetAndDetectTimeBetween(DetectItemStateEnums.FALSE.getState(), startDt, endDt);
        int packetLossRate = detectionDAO.countByPacketLossRateGreaterThanEqualAndDetectTimeBetween(
                Constants.TERMINAL_DETECT_PACKET_LOSS_RATE, startDt, endDt);
        int delay = detectionDAO.countByNetworkDelayGreaterThanEqualAndDetectTimeBetween(
                Constants.TERMINAL_DETECT_DELAY_NORM, startDt, endDt);
        int checking = detectionDAO.countByDetectStateAndDetectTimeBetween(DetectStateEnums.CHECKING, startDt, endDt);

        List<TerminalDetectionEntity> detectList = detectionDAO.findByDetectTimeBetween(startDt, endDt);
        int totalAbnormalNum = getAllAbnormalNum(detectList);

        // 构建检测结果dto
        CbbTerminalDetectResultDTO result = new CbbTerminalDetectResultDTO();
        result.setAccessInternet(accessInternet);
        result.setBandwidth(bandwidth);
        result.setDelay(delay);
        result.setIpConflict(ipConflict);
        result.setPacketLossRate(packetLossRate);
        result.setChecking(checking);
        result.setAll(totalAbnormalNum);

        return result;
    }

    /**
     * 获取总异常终端数
     * 
     * @param detectList 检测记录列表
     * @return 总异常数
     */
    private int getAllAbnormalNum(List<TerminalDetectionEntity> detectList) {
        if (CollectionUtils.isEmpty(detectList)) {
            return 0;
        }

        int totalNum = 0;
        for (TerminalDetectionEntity detectEntity : detectList) {
            if (isDetectAbnormal(detectEntity)) {
                totalNum++;
            }
        }
        return totalNum;
    }

    /**
     * 判断终端检测记录是否异常
     * 
     * @param detectEntity 检测记录
     * @return 检测结果是否异常
     */
    private boolean isDetectAbnormal(TerminalDetectionEntity detectEntity) {
        if (detectEntity.getDetectState() == DetectStateEnums.CHECKING) {
            return false;
        }
        // ip冲突
        if (isIpConflict(detectEntity.getIpConflict())) {
            return true;
        }
        // 带宽异常
        if (isBandWidthAbnormal(detectEntity.getBandwidth())) {
            return true;
        }
        // 网络访问异常
        if (isAccessInternetAbnormal(detectEntity.getAccessInternet())) {
            return true;
        }
        // 丢包率异常
        if (isPackageLossRateAbnormal(detectEntity.getPacketLossRate())) {
            return true;
        }
        // 时延异常
        if (isNetworkDelayAbnormal(detectEntity.getNetworkDelay())) {
            return true;
        }

        return false;
    }

    private boolean isNetworkDelayAbnormal(Integer networkDelay) {
        return networkDelay == null || networkDelay >= Constants.TERMINAL_DETECT_DELAY_NORM;
    }

    private boolean isPackageLossRateAbnormal(Double packetLossRate) {
        return packetLossRate == null || packetLossRate >= Constants.TERMINAL_DETECT_PACKET_LOSS_RATE;
    }

    private boolean isAccessInternetAbnormal(Integer accessInternet) {
        return accessInternet == null || accessInternet == DetectItemStateEnums.FALSE.getState();
    }

    private boolean isBandWidthAbnormal(Double bandwidth) {
        return bandwidth == null || bandwidth <= Constants.TERMINAL_DETECT_BINDWIDTH_NORM;
    }

    private boolean isIpConflict(Integer ipConflict) {
        return ipConflict == null || ipConflict == DetectItemStateEnums.TRUE.getState();
    }

    /**
     * 获取最近的终端检测记录
     * 
     * @param terminalId 终端id
     * @return 终端检测记录
     */
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
