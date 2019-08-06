package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

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

    TerminalVersionResultDTO getVersion(GetVersionRequest request);
}
