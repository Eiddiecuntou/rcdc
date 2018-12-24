package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;

/**
 * Description: 向shine请求消息实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @author Jarman
 */
public class CbbResponseShineMessage extends EssentialMessage {

    @NotBlank
    private String requestId;

    public static CbbResponseShineMessage create(String action, String terminalId,String requestId) {
        CbbResponseShineMessage message = new CbbResponseShineMessage();
        message.action = action;
        message.terminalId = terminalId;
        message.requestId = requestId;
        return message;
    }

    @Override
    public String toString() {
        return "CbbResponseShineMessage{" +
                "requestId='" + requestId + '\'' +
                ", action='" + action + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", content=" + content +
                '}';
    }

    public String getRequestId() {
        return requestId;
    }
}
