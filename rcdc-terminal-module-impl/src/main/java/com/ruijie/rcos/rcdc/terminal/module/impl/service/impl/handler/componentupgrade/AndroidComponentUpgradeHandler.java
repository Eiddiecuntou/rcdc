package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;

/**
 * Description: Android终端组件升级处理
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author XiaoJiaXin
 */
public class AndroidComponentUpgradeHandler extends AbstractCommonComponentUpgradeHandler {

    @Override
    protected TerminalOsArchType getTerminalOsArchType() {
        return TerminalOsArchType.ANDROID_ARM;
    }
}
