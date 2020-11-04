package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalComponentUpgradeInitAPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentInitService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/19
 *
 * @author nting
 */
public class CbbTerminalComponentUpgradeInitAPIImpl implements CbbTerminalComponentUpgradeInitAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalComponentUpgradeInitAPIImpl.class);

    @Autowired
    private TerminalComponentInitService terminalComponentInitService;

    @Override
    public void initLinuxVDI() {

        LOGGER.info("开始调用初始化Linux VDI组件包");
        terminalComponentInitService.initLinuxVDI();
        LOGGER.info("完成调用初始化Linux VDI组件包");
    }

    @Override
    public void initAndroidVDI() {
        LOGGER.info("开始调用初始化Android VDI组件包");
        terminalComponentInitService.initAndroidVDI();
        LOGGER.info("完成调用初始化Android VDI组件包");
    }

    @Override
    public void initLinuxIDV() {
        LOGGER.info("开始调用初始化Linux IDV组件包");
        terminalComponentInitService.initLinuxIDV();
        LOGGER.info("完成调用初始化Linux IDV组件包");
    }
}
