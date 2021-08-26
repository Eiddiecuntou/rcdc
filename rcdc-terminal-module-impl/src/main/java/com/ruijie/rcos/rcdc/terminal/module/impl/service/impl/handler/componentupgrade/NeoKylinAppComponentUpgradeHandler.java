package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/7/23 14:06
 *
 * @author conghaifeng
 */
public class NeoKylinAppComponentUpgradeHandler extends AbstractAppComponentUpgradeHandler {

    /**
     * 获取组件升级的终端系统架构类型
     *
     * @return 终端系统架构类型
     */
    @Override
    protected TerminalOsArchType getTerminalOsArchType() {
        return TerminalOsArchType.NEOKYLIN_X86;
    }

}
