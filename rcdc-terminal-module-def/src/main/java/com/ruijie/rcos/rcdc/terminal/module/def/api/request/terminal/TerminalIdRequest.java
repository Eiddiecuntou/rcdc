package com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal;


import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public class TerminalIdRequest implements Request {

    @NotBlank
    private String id;
    
    
    public TerminalIdRequest() {
    }

    public TerminalIdRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String uuid) {
        this.id = uuid;
    }
}
