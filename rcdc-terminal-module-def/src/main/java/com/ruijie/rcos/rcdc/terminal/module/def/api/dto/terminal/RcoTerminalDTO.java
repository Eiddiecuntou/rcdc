package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal;

import java.util.UUID;

/**
 * 
 * Description: 终端信息DTO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月18日
 * 
 * @author nt
 */
public class RcoTerminalDTO {
    
    private UUID id;
    
    private String terminalId;

    private String terminalName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }
    
}
