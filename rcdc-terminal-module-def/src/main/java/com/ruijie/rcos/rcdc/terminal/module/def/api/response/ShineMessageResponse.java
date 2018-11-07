package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: 向终端应答的数据
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 * @param <T> 应答的业务数据对象
 * @author Jarman
 */
public class ShineMessageResponse<T> extends DefaultResponse {

    private String action;

    private String terminalId;

    private String responseId;

    private T data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
