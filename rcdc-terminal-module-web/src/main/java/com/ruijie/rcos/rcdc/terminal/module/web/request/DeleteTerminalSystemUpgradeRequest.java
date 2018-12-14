package com.ruijie.rcos.rcdc.terminal.module.web.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 批量添加系统升级任务请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月22日
 *
 * @author nt
 */
public class DeleteTerminalSystemUpgradeRequest implements WebRequest {

    /**
     * 终端id
     */
    @NotNull
    @Size(min = 1)
    private String terminalId;
    

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

}
