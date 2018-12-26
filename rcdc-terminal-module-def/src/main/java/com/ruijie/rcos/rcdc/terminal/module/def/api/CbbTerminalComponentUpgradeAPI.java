package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalVersionRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalVersionResponse;

/**
 * 
 * Description: 终端组件升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月14日
 * 
 * @author nt
 */
public interface CbbTerminalComponentUpgradeAPI {
    
    
    /**
     * 获取终端组件升级版本信息
     * @param request
     * @return
     */
    CbbTerminalVersionResponse getVersion(CbbTerminalVersionRequest request);

}
