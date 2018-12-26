package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import org.springframework.util.Assert;

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

    private int code;

    /**
     * 创建应答消息实体
     *
     * @param action     消息action值
     * @param terminalId 终端id
     * @param requestId  请求id
     * @return 消息实体
     */
    public static CbbResponseShineMessage create(String action, String terminalId, String requestId) {
        Assert.hasText(action, "action不能为空");
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.hasText(requestId, "requestId不能为空");
        CbbResponseShineMessage message = new CbbResponseShineMessage();
        message.action = action;
        message.terminalId = terminalId;
        message.requestId = requestId;
        return message;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CbbResponseShineMessage{" +
                "requestId='" + requestId + '\'' +
                ", code=" + code +
                ", action='" + action + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", content=" + content +
                '}';
    }
}
