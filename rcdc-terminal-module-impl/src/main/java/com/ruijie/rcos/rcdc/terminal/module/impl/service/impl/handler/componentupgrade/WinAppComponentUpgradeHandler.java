package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author nt
 */
public class WinAppComponentUpgradeHandler extends AbstractAppComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinAppComponentUpgradeHandler.class);

    @Override
    protected TerminalOsArchType getTerminalOsArchType() {
        return TerminalOsArchType.WINDOWS_X86;
    }

}
