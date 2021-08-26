package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/18
 *
 * @author ting
 */
public class LinuxArmComponentUpgradeHandler extends AbstractCommonComponentUpgradeHandler {

    @Override
    protected TerminalOsArchType getTerminalOsArchType() {
        return TerminalOsArchType.LINUX_ARM;
    }
}
