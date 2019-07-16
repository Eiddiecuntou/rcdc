package com.ruijie.rcos.rcdc.terminal.module.def.api.request.group;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 *
 * Description: 终端分组id请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class CbbTerminalGroupIdRequest implements Request {

    /**
     * 终端id
     */
    @NotNull
    private UUID id;
    

    public CbbTerminalGroupIdRequest() {
    }

    public CbbTerminalGroupIdRequest(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }
}
