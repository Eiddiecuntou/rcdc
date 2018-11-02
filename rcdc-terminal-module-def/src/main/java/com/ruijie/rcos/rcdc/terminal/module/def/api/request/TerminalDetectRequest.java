package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.DetectItemEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

import javax.validation.constraints.NotNull;

/**
 * Description: 终端检测请求参数对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class TerminalDetectRequest implements Request {
    @NotNull
    private String terminalId;
    private DetectItemEnums[] items;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public DetectItemEnums[] getItems() {
        return items;
    }

    public void setItems(DetectItemEnums[] items) {
        this.items = items;
    }
}
