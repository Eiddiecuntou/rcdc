package com.ruijie.rcos.rcdc.terminal.module.web.request;

import com.ruijie.rcos.sk.modulekit.api.comm.Request;

import javax.validation.constraints.NotNull;

/**
 * Description: 下载终端日志请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/12
 *
 * @author Jarman
 */
public class DownloadLogRequest implements Request {

    @NotNull
    private String terminalId;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
