package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
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
        LOGGER.warn("start terminal detection init, change all checking state record to fail");
        int effectRows = detectionDAO.modifyDetectionCheckingToFail(DetectStateEnums.CHECKING, DetectStateEnums.ERROR);
        LOGGER.warn("finish terminal detection init, update {} record", effectRows);
    }

}
