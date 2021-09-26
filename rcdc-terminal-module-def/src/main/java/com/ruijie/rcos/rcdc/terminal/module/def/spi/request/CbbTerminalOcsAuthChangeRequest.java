package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

/**
 * Description:
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/26
 *
 * @author zhangsiming
 */
public class CbbTerminalOcsAuthChangeRequest {
    private String terminalId;
    private Boolean ocsAuthed;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Boolean getOcsAuthed() {
        return ocsAuthed;
    }

    public void setOcsAuthed(Boolean ocsAuthed) {
        this.ocsAuthed = ocsAuthed;
    }
}
