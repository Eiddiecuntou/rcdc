package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description: 转发shine消息实体
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/24
 *
 * @author Jarman
 */
public class EssentialMessage<T> implements Request {

    @NotBlank
    protected String action;

    @NotBlank
    protected String terminalId;

    protected int errorCode;

    protected String errorMsg;

    protected T content;

    public String getAction() {
        return action;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
