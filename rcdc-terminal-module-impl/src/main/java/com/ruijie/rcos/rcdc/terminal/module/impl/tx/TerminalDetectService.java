package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalBasicInfoEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
import com.ruijie.rcos.sk.base.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    public void updateBasicInfoAndDetect(String terminalId, TerminalDetectResult detectResult) {
        Assert.hasLength(terminalId, "terminalId不能为空");
        Assert.notNull(detectResult, "TerminalDetectResult不能为null");

        //检测数据入库
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        Date now = new Date();
        entity.setTerminalId(terminalId);
        entity.setBandwidth(detectResult.getBandwidth());
        entity.setCanAccessInternet(detectResult.getCanAccessInternet());
        entity.setPacketLossRate(detectResult.getPacketLossRate());
        entity.setIpConflict(detectResult.getIpConflict());
        entity.setNetworkDelay(detectResult.getNetworkDelay());
        entity.setDetectTime(now);
        detectionDAO.save(entity);
        //更新基本信息表检测数据字段
        TerminalBasicInfoEntity basicInfoEntity = basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(terminalId);
        basicInfoDAO.modifyDetectInfo(terminalId, basicInfoEntity.getVersion(), now,
                DetectStateEnums.SUCCESS.ordinal());
    }

    /**
     * 当终端断开连接时，把状态为正在检测改为检测失败状态
     */
    public void setOfflineTerminalToFailureState() {
        List<TerminalBasicInfoEntity> basicInfoList =
                basicInfoDAO.findTerminalBasicInfoEntitiesByDetectState(DetectStateEnums.DOING);
        Date now = new Date();
        basicInfoList.forEach(entity -> {
            basicInfoDAO.modifyDetectInfo(entity.getTerminalId(), entity.getVersion(), now,
                    DetectStateEnums.FAILURE.ordinal());
        });
    }
}
