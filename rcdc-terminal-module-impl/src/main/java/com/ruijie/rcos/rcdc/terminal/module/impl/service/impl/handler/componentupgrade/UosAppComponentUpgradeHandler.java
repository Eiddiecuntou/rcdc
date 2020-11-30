package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/7/23 14:05
 *
 * @author conghaifeng
 */
public class UosAppComponentUpgradeHandler extends AbstractAppComponentUpgradeHandler {

    /**
     * 获取组件升级的终端系统类型
     *
     * @return 终端系统类型
     */
    @Override
    protected CbbTerminalOsTypeEnums getTerminalOsType() {
        return CbbTerminalOsTypeEnums.UOS;
    }

}
