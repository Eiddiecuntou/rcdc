
package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalAuthorizationWhitelistAPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalAuthorizationWhitelistService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description:
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/16
 *
 * @author zhangsiming
 */
public class CbbTerminalAuthorizationWhitelistAPIImpl implements CbbTerminalAuthorizationWhitelistAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalAuthorizationWhitelistAPIImpl.class);

    @Autowired
    private TerminalAuthorizationWhitelistService terminalAuthorizationWhitelistService;

    @Override
    public Boolean isOCSFreeAuthorization(String terminalId) {
        return terminalAuthorizationWhitelistService.isOCSFreeAuthorization(terminalId);
    }
}
