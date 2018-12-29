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
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectItemStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResponse;
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
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalDetectionDAO detectionDAO;

    /**
     * 更新基础信息表和检测表，存在事务
     *
     * @param terminalId 终端id
     * @param detectResult 检测结果数据对象
     */
    public void updateTerminalDetect(String terminalId, TerminalDetectResponse detectResult) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(detectResult, "TerminalDetectResult不能为null");
        TerminalDetectResponse.DetectResult result = detectResult.getResult();
        // 获取检测记录
        List<TerminalDetectionEntity> entityList = detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
        if (CollectionUtils.isEmpty(entityList)) {
            LOGGER.debug("no checking detection record, terminal id[{}]", terminalId);
            return;
        }

        for (TerminalDetectionEntity entity : entityList) {
            if (StateEnums.FAILURE == detectResult.getErrorCode()) {
                // TODO 检测失败
                detectFailure(entity);
                return;
            }
            // 更新检测记录
            detectSuccess(result, entity);
        }

    }

    private void detectSuccess(TerminalDetectResponse.DetectResult result, TerminalDetectionEntity entity) {
        entity.setBandwidth(result.getBandwidth());
        entity.setAccessInternet(result.getAccessInternet());
        entity.setPacketLossRate(result.getPacketLossRate());
        entity.setIpConflict(result.getIpConflict());
        entity.setIpConflictMac(result.getIpConflictMac());
        entity.setNetworkDelay(result.getDelay());
        entity.setDetectState(DetectStateEnums.SUCCESS);
        detectionDAO.save(entity);
    }

    private void detectFailure(TerminalDetectionEntity entity) {
        entity.setDetectState(DetectStateEnums.ERROR);
        entity.setDetectFailMsg("");
        detectionDAO.save(entity);
    }

    /**
     * 保存检测记录
     * 
     * @param terminalId 终端id
     * @param batch 批次
     */
    public void save(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be null");

        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        Date now = new Date();
        entity.setTerminalId(terminalId);
        entity.setDetectTime(now);
        entity.setDetectState(DetectStateEnums.CHECKING);
        detectionDAO.save(entity);
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
        List<TerminalDetectionEntity> detectionList = detectionDAO.findByTerminalIdAndDetectTimeBetween(terminalId, startDt, endDt);
        if (CollectionUtils.isEmpty(detectionList)) {
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

        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit(), new Sort(Direction.DESC, "detectTime"));
        Specification<TerminalDetectionEntity> spec = new TerminalDetectSpecification(request.getDate());

        return detectionDAO.findAll(spec, pageable);
    }

    /**
     * 获取检测结果
     * @param detectDate 日期
     * @return 检测结果
     */
    public CbbTerminalDetectResultDTO getDetectResult(CbbDetectDateEnums detectDate) {
        Assert.notNull(detectDate, "detect date can not be null");

        Date date = getDetectDate(detectDate);
        Date startDt = TerminalDateUtil.getDayStart(date);
        Date endDt = TerminalDateUtil.getDayEnd(date);

        int ipConflict = detectionDAO.countByIpConflictAndDetectTimeBetween(DetectItemStateEnums.TRUE.getState(), startDt, endDt);
        int bandwidth = detectionDAO.countByBandwidthLessThanEqualAndDetectTimeBetween(Constants.TERMINAL_DETECT_BINDWIDTH_NORM, startDt, endDt);
        int accessInternet = detectionDAO.countByAssessInternetAndDetectTimeBetween(DetectItemStateEnums.FALSE.getState(), startDt, endDt);
        int packetLossRate =
                detectionDAO.countByPacketLossRateGreaterThanEqualAndDetectTimeBetween(Constants.TERMINAL_DETECT_PACKET_LOSS_RATE, startDt, endDt);
        int delay = detectionDAO.countByNetworkDelayGreaterThanEqualAndDetectTimeBetween(Constants.TERMINAL_DETECT_DELAY_NORM, startDt, endDt);
        int checking = detectionDAO.countByDetectStateAndDetectTimeBetween(DetectStateEnums.CHECKING, startDt, endDt);
        CbbTerminalDetectResultDTO result = new CbbTerminalDetectResultDTO();
        result.setAccessInternet(accessInternet);
        result.setBandwidth(bandwidth);
        result.setDelay(delay);
        result.setIpConflict(ipConflict);
        result.setPacketLossRate(packetLossRate);
        result.setChecking(checking);

        return result;
    }

    /**
     * 获取检测日期
     * @param detectDate 检测日期枚举
     * @return 检测日期
     */
    private Date getDetectDate(CbbDetectDateEnums detectDate) {
        Date date = null;
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
