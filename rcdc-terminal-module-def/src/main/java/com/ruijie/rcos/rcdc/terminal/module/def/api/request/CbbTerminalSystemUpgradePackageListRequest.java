package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 终端系统升级包列表请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月30日
 * 
 * @author nt
 */
public class CbbTerminalSystemUpgradePackageListRequest implements Request {

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
