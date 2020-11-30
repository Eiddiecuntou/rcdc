package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author nt
 */
public class LinuxComponentUpgradeHandler extends AbstractCommonComponentUpgradeHandler {

    @Override
    protected CbbTerminalOsTypeEnums getTerminalOsType() {
        return CbbTerminalOsTypeEnums.LINUX;
    }
}
