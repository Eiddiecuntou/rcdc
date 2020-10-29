package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.AppTerminalUpdateListCacheInit;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;
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
public class AppTerminalUpgradeCacheInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTerminalUpgradeCacheInit.class);

    private static AppTerminalUpdateListCacheInit neoKylinAppTerminalUpdateListCacheInit;

    private static AppTerminalUpdateListCacheInit windowsAppTerminalUpdateListCacheInit;

    private static AppTerminalUpdateListCacheInit uosAppTerminalUpdateListCacheInit;

    static {
        neoKylinAppTerminalUpdateListCacheInit = new AppTerminalUpdateListCacheInit(CbbTerminalTypeEnums.APP_NEOKYLIN);
        windowsAppTerminalUpdateListCacheInit = new AppTerminalUpdateListCacheInit(CbbTerminalTypeEnums.APP_WINDOWS);
        uosAppTerminalUpdateListCacheInit = new AppTerminalUpdateListCacheInit(CbbTerminalTypeEnums.APP_UOS);
    }

    @Override
    public void safeInit() {
        LOGGER.info("开始中标麒麟软终端组件升级信息缓存初始化");
        neoKylinAppTerminalUpdateListCacheInit.init();
        LOGGER.info("完成中标麒麟软终端组件升级信息缓存初始化");

        LOGGER.info("开始windows软终端组件升级信息缓存初始化");
        windowsAppTerminalUpdateListCacheInit.init();
        LOGGER.info("完成windows软终端组件升级信息缓存初始化");

        LOGGER.info("开始uos软终端组件升级信息缓存初始化");
        uosAppTerminalUpdateListCacheInit.init();
        LOGGER.info("完成uos软终端组件升级信息缓存初始化");
    }
}
