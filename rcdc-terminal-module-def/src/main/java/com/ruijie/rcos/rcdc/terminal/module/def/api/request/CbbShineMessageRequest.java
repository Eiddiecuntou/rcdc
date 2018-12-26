package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import org.springframework.util.Assert;

/**
 * Description: 向shine请求消息实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/24
 *
 * @author Jarman
 */
public class CbbShineMessageRequest extends EssentialMessage {

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

    @Override
    public String toString() {
        return "CbbShineMessageRequest{" +
                "action='" + action + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", content=" + content +
                '}';
    }
}
