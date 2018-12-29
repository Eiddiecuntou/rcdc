package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import java.util.Date;

import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResponse;

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
    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalDetectionDAO detectionDAO;

    /**
     * 更新基础信息表和检测表，存在事务
     *
     * @param terminalId   终端id
     * @param detectResult 检测结果数据对象
     */
    public void updateTerminalDetect(String terminalId, TerminalDetectResponse detectResult) {
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(detectResult, "TerminalDetectResult不能为null");
        TerminalDetectResponse.DetectResult result = detectResult.getResult();
        if (StateEnums.FAILURE == detectResult.getErrorCode()) {
            //TODO 检测失败

            return;
        }

        //检测数据入库
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        Date now = new Date();
        entity.setTerminalId(terminalId);
        entity.setBandwidth(result.getBandwidth());
        entity.setCanAccessInternet(result.getCanAccessInternet());
        entity.setPacketLossRate(result.getPacketLossRate());
        entity.setIpConflict(result.getIpConflict());
        entity.setNetworkDelay(result.getNetworkDelay());
        entity.setDetectTime(now);
        detectionDAO.save(entity);
    }
}
