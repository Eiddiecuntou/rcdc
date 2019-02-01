package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 添加终端系统升级任务请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月30日
 * 
 * @author nt
 */
public class CbbAddTerminalSystemUpgradeTaskRequest implements Request {
    
    /**
     * 终端id
     */
    @NotBlank
    private String terminalId;

    /**
     * 终端类型
     */
    @NotNull
    private TerminalPlatformEnums platform;
    

    public CbbAddTerminalSystemUpgradeTaskRequest() {
    }

    public CbbAddTerminalSystemUpgradeTaskRequest(String terminalId, TerminalPlatformEnums platform) {
        Assert.hasText(terminalId, "terminalId can not be blank");
        Assert.notNull(platform, "terminalId can not be null");
        
        this.terminalId = terminalId;
        this.platform = platform;
    }

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
