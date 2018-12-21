package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultResponseMessageSender;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: 转发消息给终端（Shine）
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @author Jarman
 */
public class CbbTranspondMessageHandlerAPIImpl implements CbbTranspondMessageHandlerAPI {

    @Autowired
    private SessionManager sessionManager;

    @Override
    public DefaultResponse request(CbbShineMessageRequest request) throws BusinessException {
        Assert.notNull(request, "request参数不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(request.getTerminalId());
        Message message = new Message(Constants.SYSTEM_TYPE, request.getAction(), request.getData());

        sender.request(message);
        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbShineMessageResponse syncRequest(CbbShineMessageRequest request) throws IOException, InterruptedException,
            BusinessException {
        Assert.notNull(request, "request参数不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(request.getTerminalId());
        Message message = new Message(Constants.SYSTEM_TYPE, request.getAction(), request.getData());

        BaseMessage baseMessage = sender.syncRequest(message);
        CbbShineMessageResponse cbbShineMessageResponse = new CbbShineMessageResponse();
        cbbShineMessageResponse.setAction(baseMessage.getAction());
        cbbShineMessageResponse.setData(baseMessage.getData());

        return cbbShineMessageResponse;
    }

    @Override
    public DefaultResponse asyncRequest(CbbShineMessageRequest request, CbbTerminalCallback callback) throws BusinessException {
        Assert.notNull(request, "request参数不能为null");
        Assert.notNull(callback, "CbbTerminalCallback 不能为null");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(request.getTerminalId());
        Message message = new Message(Constants.SYSTEM_TYPE, request.getAction(), request.getData());
        sender.asyncRequest(message, new AsyncRequestCallBack(request.getTerminalId(), callback));
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse response(CbbResponseShineMessage msg) throws BusinessException {
        Assert.notNull(msg, "CbbResponseShineMessage不能为null");

        Session session = sessionManager.getSession(msg.getTerminalId());
        if (session == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        DefaultResponseMessageSender sender = new DefaultResponseMessageSender(msg.getRequestId(), session);
        Message message = new Message(Constants.SYSTEM_TYPE, msg.getAction(), msg.getData());
        sender.response(message);
        return DefaultResponse.Builder.success();
    }
}
