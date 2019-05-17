package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.quartz.Quartz;
import com.ruijie.rcos.sk.base.quartz.QuartzTask;
import com.ruijie.rcos.sk.modulekit.api.isolation.GlobalUniqueBean;

/**
 * 
 * Description: 检验终端检测超时处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月18日
 * 
 * @author nt
 */
@GlobalUniqueBean("detectTimeoutQuartz")
@Quartz(cron = "0/10 * * * * ?", msgKey = BusinessKey.RCDC_TERMINAL_QUARTZ_DETECT_TIME_OUT)
public class TerminalDetectTimeoutQuartz implements QuartzTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalDetectTimeoutQuartz.class);

    @Autowired
    private TerminalDetectionDAO detectionDAO;


    @Override
    public void execute() throws Exception {
        LOGGER.debug("start to deal with timeout terminal detection...");
        // 将正在检测中的超时2分钟的记录设为失败状态
        Date now = new Date();
        int timeoutSecond = Constants.TERMINAL_DETECT_TIMEOUT;
        Date date = TerminalDateUtil.addSecond(now, -timeoutSecond);
        List<TerminalDetectionEntity> timeoutDetectList =
                detectionDAO.findByDetectStateAndDetectTimeBefore(DetectStateEnums.CHECKING, date);
        if (CollectionUtils.isEmpty(timeoutDetectList)) {
            LOGGER.debug("no timeout detection");
            return;
        }

        for (TerminalDetectionEntity entity : timeoutDetectList) {
            entity.setDetectState(DetectStateEnums.ERROR);
            detectionDAO.save(entity);
        }
        LOGGER.debug("finish to deal with timeout terminal detection, effect rows[{}]", timeoutDetectList.size());
    }

}
