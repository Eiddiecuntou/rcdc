package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import org.springframework.util.Assert;

/**
 * Description: 向shine请求消息实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/24
 *
 * @param <T> 消息体
 * @author Jarman
 */
public class CbbShineMessageRequest<T> implements Request {

    @NotBlank
    protected String action;

    @NotBlank
    protected String terminalId;

    protected T content;


    private CbbShineMessageRequest() {

    }

    /**
     * 创建消息实体
     *
     * @param action     消息action值
     * @param terminalId 终端id
     * @return 消息实体
     */
    public static CbbShineMessageRequest create(String action, String terminalId) {
        Assert.hasText(action, "action不能为空");
        Assert.hasText(terminalId, "terminalId不能为空");
        CbbShineMessageRequest message = new CbbShineMessageRequest();
        message.action = action;
        message.terminalId = terminalId;
        return message;
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
        return "CbbShineMessageRequest{" +
                "action='" + action + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", content=" + content +
                '}';
    }
}
