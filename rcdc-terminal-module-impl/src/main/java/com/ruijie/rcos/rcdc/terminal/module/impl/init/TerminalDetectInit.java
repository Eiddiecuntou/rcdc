package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;

/**
 * 
 * Description: 终端检测系统初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月29日
 * 
 * @author nt
 */
@Service
public class TerminalDetectInit implements SafetySingletonInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalDetectInit.class);

    @Autowired
    private TerminalDetectionDAO detectionDAO;
    
    @Override
    public void safeInit() {
        // 更新所有检测中的记录为检测失败
        LOGGER.debug("start terminal detection init, change all checking state record to fail");
        List<TerminalDetectionEntity> checkingList = getCheckingDetectionList();
        if (CollectionUtils.isEmpty(checkingList)) {
            LOGGER.debug("no checking detection");
            return;
        }
        for (TerminalDetectionEntity checkingEntity : checkingList) {
            checkingEntity.setDetectState(DetectStateEnums.ERROR);
            detectionDAO.save(checkingEntity);
        }
        LOGGER.debug("finish terminal detection init, update {} record", checkingList.size());
    }

    private List<TerminalDetectionEntity> getCheckingDetectionList() {
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setDetectState(DetectStateEnums.CHECKING);
        Example<TerminalDetectionEntity> example = Example.of(entity);
        List<TerminalDetectionEntity> checkingList = detectionDAO.findAll(example);
        return checkingList;
    }

}
