package com.ruijie.rcos.rcdc.terminal.module.web.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 系统升级包列表请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月21日
 * 
 * @author nt
 */
public class ListTerminalSystemUpgradePackageRequest implements WebRequest {
    
    /**
     * 终端类型
     */
    private CbbTerminalTypeEnums terminalType;

    public CbbTerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(CbbTerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }

    
    
}
