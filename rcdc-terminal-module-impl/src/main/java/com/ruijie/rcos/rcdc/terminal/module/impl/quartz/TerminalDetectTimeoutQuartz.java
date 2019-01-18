package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
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
@Quartz(cron = "0/10 * *  * * ?")
public class TerminalDetectTimeoutQuartz implements QuartzTask{

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalDetectTimeoutQuartz.class);
    
    @Autowired
    private TerminalDetectService terminaldetectService;
    
    @Autowired
    private TerminalDetectionDAO detectionDAO;
    
    @Override
    public void execute() throws Exception {
        LOGGER.debug("start to deal with timeout terminal detection...");
        // 获取检测开始时间超过两分钟、状态为正在检测中终端检测记录
//        detectionDAO.findBy
        
        LOGGER.debug("finish to deal with timeout terminal detection...");
    }

}
