package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbObtainUpgradeableTerminalListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.response.CbbObtainUpgradeableTerminalListResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

/**
 * Description: 获取可刷机终端列表
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/6/17
 *
 * @author nt
 */
public interface CbbObtainUpgradeableTerminalListSPI {

    /**
     *  获取可刷机终端列表
     *
     * @param request 请求参数
     * @return 可刷机终端列表
     */
    @NoRollback
    CbbObtainUpgradeableTerminalListResponse obtainUpgradeableTerminal(CbbObtainUpgradeableTerminalListRequest request);
}
