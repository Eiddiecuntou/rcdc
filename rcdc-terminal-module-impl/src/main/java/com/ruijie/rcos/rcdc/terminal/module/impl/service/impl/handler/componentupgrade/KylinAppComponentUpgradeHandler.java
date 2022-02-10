package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;

/**
 * Description: 新麒麟软终端组件升级
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/4/20 13:16
 *
 * @author juan_chen
 */
public class KylinAppComponentUpgradeHandler extends AbstractAppComponentUpgradeHandler {

    /**
     * 获取组件升级的终端系统类型
     *
     * @return 终端系统类型
     */
    @Override
    protected TerminalOsArchType getTerminalOsArchType() {
        return TerminalOsArchType.KYLIN_X86;
    }
}
