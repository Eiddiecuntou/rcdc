package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/10
 *
 * @author nt
 */
public class CbbModifyTerminalRequest implements Request {

    @NotBlank
    private String cbbTerminalId;

    @NotBlank
    private String terminalName;

    @NotNull
    private UUID groupId;

    public String getCbbTerminalId() {
        return cbbTerminalId;
    }

    public void setCbbTerminalId(String cbbTerminalId) {
        this.cbbTerminalId = cbbTerminalId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
}
