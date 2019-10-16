package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.WinAppTerminalUpdatelistCacheInit;
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
public class WinAppTerminalUpgradeCacheInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinAppTerminalUpgradeCacheInit.class);

    @Autowired
    private WinAppTerminalUpdatelistCacheInit windowsAppTerminalUpdatelistCacheInit;

    @Override
    public void safeInit() {
        LOGGER.info("开始windows软终端组件升级信息缓存初始化");
        windowsAppTerminalUpdatelistCacheInit.init();
        LOGGER.info("完成windows软终端组件升级信息缓存初始化");
    }
}
