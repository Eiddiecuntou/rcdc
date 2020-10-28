package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalUpgradeInitAPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOtaInitService;
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
public class CbbTerminalUpgradeInitAPIImpl implements CbbTerminalUpgradeInitAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalUpgradeInitAPIImpl.class);

    @Autowired
    private TerminalOtaInitService terminalOtaInitService;

    @Override
    public void initAndroidOTA() {

        LOGGER.info("开始初始化安卓OTA");
        terminalOtaInitService.initAndroidOta();
        LOGGER.info("完成初始化安卓OTA");
    }
}
