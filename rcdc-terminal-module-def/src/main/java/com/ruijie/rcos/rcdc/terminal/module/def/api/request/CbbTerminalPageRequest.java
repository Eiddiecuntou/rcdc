package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageRequest;

/**
 * 
 * Description: 终端列表分页请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月21日
 * 
 * @author nt
 */
public class CbbTerminalPageRequest extends DefaultPageRequest {

    /**
     * 终端类型  idv vdi
     */
    private CbbTerminalTypeEnums terminalType;
    
    /**
     * 终端系统版本
     */
    private String terminalSystemVersion;


    public CbbTerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(CbbTerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalSystemVersion() {
        return terminalSystemVersion;
    }

    public void setTerminalSystemVersion(String terminalSystemVersion) {
        this.terminalSystemVersion = terminalSystemVersion;
    }
    
}
