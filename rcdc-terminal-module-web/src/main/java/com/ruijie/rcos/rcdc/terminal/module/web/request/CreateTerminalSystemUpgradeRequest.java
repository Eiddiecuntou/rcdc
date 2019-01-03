package com.ruijie.rcos.rcdc.terminal.module.web.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 批量添加系统升级任务请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月22日
 * 
 * @author nt
 */
public class CreateTerminalSystemUpgradeRequest implements WebRequest {

    /**
     * 终端id
     */
    @NotBlank
    private String[] terminalIdArr;

    /**
     * 终端升级类型
     */
    @NotNull
    private CbbTerminalTypeEnums terminalType;


    public String[] getTerminalIdArr() {
        return terminalIdArr;
    }

    public void setTerminalIdArr(String[] terminalIdArr) {
        this.terminalIdArr = terminalIdArr;
    }

    public CbbTerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(CbbTerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }

}
