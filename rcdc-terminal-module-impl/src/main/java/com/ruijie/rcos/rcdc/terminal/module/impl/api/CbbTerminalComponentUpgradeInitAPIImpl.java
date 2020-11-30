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
    public void initLinux() {

        LOGGER.info("开始调用初始化Linux 组件包");
        terminalComponentInitService.initLinux();
        LOGGER.info("完成调用初始化Linux 组件包");
    }

    @Override
    public void initAndroid() {
        LOGGER.info("开始调用初始化Android 组件包");
        terminalComponentInitService.initAndroid();
        LOGGER.info("完成调用初始化Android 组件包");
    }
}
