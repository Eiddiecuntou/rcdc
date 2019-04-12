package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;
import org.springframework.util.Assert;
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
     * 刷机包id
     */
    @NotNull
    private UUID upgradeTaskId;


    public CbbAddTerminalSystemUpgradeTaskRequest() {
        
    }

    public CbbAddTerminalSystemUpgradeTaskRequest(String terminalId, UUID upgradeTaskId) {
        Assert.hasText(terminalId, "terminalId can not be blank");
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");

        this.terminalId = terminalId;
        this.upgradeTaskId = upgradeTaskId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

}
