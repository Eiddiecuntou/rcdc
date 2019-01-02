package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Description: 向shine应答消息实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @param <T> 消息体
 * @author Jarman
 */
public class CbbResponseShineMessage<T> implements Request {

    @NotBlank
    private String requestId;

    @NotBlank
    protected String action;

    @NotBlank
    protected String terminalId;

    @NotNull
    private Integer code;

    @Nullable
    protected T content;

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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

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

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
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
