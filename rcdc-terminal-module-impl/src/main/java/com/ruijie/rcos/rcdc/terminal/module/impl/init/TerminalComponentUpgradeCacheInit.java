package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.AndroidVDITerminalUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxVDIUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.WinAppTerminalUpdatelistCacheInit;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private WinAppTerminalUpdatelistCacheInit windowsAppTerminalUpdatelistCacheInit;

    @Autowired
    private AndroidVDITerminalUpdatelistCacheInit androidVDITerminalUpdatelistCacheInit;

    /**
     * 各组件的升级信息缓存初始化
     */
    public void cachesInit() {
        LOGGER.info("开始终端组件升级信息缓存初始化");
        windowsAppTerminalUpdatelistCacheInit.init();
        linuxVDIUpdatelistCacheInit.init();
        androidVDITerminalUpdatelistCacheInit.init();
        LOGGER.info("完成终端组件升级信息缓存初始化");
    }

}
