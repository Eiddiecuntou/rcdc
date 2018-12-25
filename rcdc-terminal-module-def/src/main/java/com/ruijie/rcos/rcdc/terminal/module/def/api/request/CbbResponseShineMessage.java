package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;

/**
 * Description: 向shine应答消息实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @author Jarman
 */
public class CbbResponseShineMessage extends EssentialMessage {

    @NotBlank
    private String requestId;

    private int errorCode;

    private String errorMsg;

    public static CbbResponseShineMessage create(String action, String terminalId, String requestId) {
        CbbResponseShineMessage message = new CbbResponseShineMessage();
        message.action = action;
        message.terminalId = terminalId;
        message.requestId = requestId;
        return message;
    }

    public String getRequestId() {
        return requestId;
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

    @Override
    public String toString() {
        return "CbbResponseShineMessage{" +
                "requestId='" + requestId + '\'' +
                ", errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", action='" + action + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", content=" + content +
                '}';
    }
}
