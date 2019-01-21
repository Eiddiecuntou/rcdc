package com.ruijie.rcos.rcdc.terminal.module.web.request;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
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
public class AddTerminalSystemUpgradeRequest implements WebRequest {

    /**
     * 终端id
     */
    @NotBlank
    private String terminalId;
    
    /**
     * 终端升级类型
     */
    @NotNull
    private TerminalPlatformEnums platform;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public TerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(TerminalPlatformEnums platform) {
        this.platform = platform;
    }

}
