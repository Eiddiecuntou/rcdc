package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

/**
 * Description: session中绑定的终端信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/2/22
 *
 * @author Jarman
 */
public class TerminalInfo {

    private String terminalId;

    private String terminalIp;

    public TerminalInfo() {
        
    }

    public TerminalInfo(String terminalId, String terminalIp) {
        this.terminalId = terminalId;
        this.terminalIp = terminalIp;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalIp() {
        return terminalIp;
    }

    public void setTerminalIp(String terminalIp) {
        this.terminalIp = terminalIp;
    }
}
