package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherKey;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

import com.ruijie.rcos.sk.base.annotation.NotNull;

/**
 * Description: 消息分发请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @param <T> 分发的业务数据对象
 * @author Jarman
 */
public class CbbDispatcherRequest<T> implements Request {

    @DispatcherKey
    private String dispatcherKey;

    @NotBlank
    private String terminalId;

    private String requestId;

    private T data;

    public String getDispatcherKey() {
        return dispatcherKey;
    }

    public void setDispatcherKey(String dispatcherKey) {
        this.dispatcherKey = dispatcherKey;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
