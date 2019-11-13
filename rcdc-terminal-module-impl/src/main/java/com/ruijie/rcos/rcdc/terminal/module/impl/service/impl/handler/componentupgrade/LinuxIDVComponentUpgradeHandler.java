package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade.AbstractCommonComponentUpgradeHandler;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/28
 *
 * @author hs
 */
public class LinuxIDVComponentUpgradeHandler extends AbstractCommonComponentUpgradeHandler {

    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.IDV_LINUX;
    }
}
