package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

/**
 * Description: 向shine请求消息实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/24
 *
 * @author Jarman
 */
public class CbbShineMessageRequest extends EssentialMessage {

    public static CbbShineMessageRequest create(String action, String terminalId) {
        CbbShineMessageRequest message = new CbbShineMessageRequest();
        message.action = action;
        message.terminalId = terminalId;
        return message;
    }

    @Override
    public String toString() {
        return "CbbShineMessageRequest{" +
                "action='" + action + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", content=" + content +
                '}';
    }
}
