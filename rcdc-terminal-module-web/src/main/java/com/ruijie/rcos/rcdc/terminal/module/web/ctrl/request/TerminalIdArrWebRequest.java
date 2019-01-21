package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import com.ruijie.rcos.sk.base.annotation.NotEmpty;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/18
 *
 * @author Jarman
 */
public class TerminalIdArrWebRequest implements WebRequest {
    
    @NotEmpty
    private String[] idArr;

    public TerminalIdArrWebRequest() {

    }

    public TerminalIdArrWebRequest(String[] idArr) {
        this.idArr = idArr;
    }

    public String[] getIdArr() {
        return idArr;
    }

    public void setIdArr(String[] idArr) {
        this.idArr = idArr;
    }
}
