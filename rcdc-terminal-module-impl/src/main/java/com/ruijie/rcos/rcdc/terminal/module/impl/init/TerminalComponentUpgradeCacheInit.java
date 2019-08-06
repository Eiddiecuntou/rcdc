package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxVDIUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.WindowsSoftTerminalUpdatelistCacheInit;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 终端系统升级任务初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月15日
 * 
 * @author nt
 */
@Service
public class TerminalComponentUpgradeCacheInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalComponentUpgradeCacheInit.class);
    @Autowired
    private LinuxVDIUpdatelistCacheInit linuxVDIUpdatelistCacheInit;

    @Autowired
    private WindowsSoftTerminalUpdatelistCacheInit windowsSoftTerminalUpdatelistCacheInit;

    public void safeInit() {
        LOGGER.info("开始初始化linuxVDI终端组件升级updatelist");
        linuxVDIUpdatelistCacheInit.init();
//        windowsSoftTerminalUpdatelistCacheInit.init();
    }

}
