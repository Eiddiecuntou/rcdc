package com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.TextShort;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 编辑终端请求参数
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class EditTerminalRequest implements Request {
    
    @NotBlank
    private String id;

    @NotBlank
    @TextShort
    private String terminalName;

    @NotNull
    private UUID groupId;
    
    public EditTerminalRequest() {
    }

    public EditTerminalRequest(String id, String terminalName, UUID groupId) {
        this.id = id;
        this.terminalName = terminalName;
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
