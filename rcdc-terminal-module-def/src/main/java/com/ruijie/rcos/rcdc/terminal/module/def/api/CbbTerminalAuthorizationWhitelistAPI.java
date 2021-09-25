package com.ruijie.rcos.rcdc.terminal.module.def.api;

/**
 * Description:
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/15
 *
 * @author zhangsiming
 */
public interface CbbTerminalAuthorizationWhitelistAPI {
    /**
     * @param terminalId 终端id
     * @return 返回该终端是否为OCS默认授权
     */
    Boolean isOCSFreeAuthorization(String terminalId);
}