package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/5
 *
 * @author nt
 */
public interface TerminalComponentUpgradeHandler {

    /**
     *  获取终端组件升级信息
     * @param request 请求参数
     * @return 升级信息
     */
    TerminalVersionResultDTO getVersion(GetVersionDTO request);
}
