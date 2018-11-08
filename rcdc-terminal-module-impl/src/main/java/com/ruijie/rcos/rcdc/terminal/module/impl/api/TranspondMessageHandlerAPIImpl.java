package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.TranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.ShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.ShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.TerminalCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.callback.AbstractRequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultResponseMessageSender;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @author Jarman
 */
public class TranspondMessageHandlerAPIImpl implements TranspondMessageHandlerAPI {

    @Autowired
    private SessionManager sessionManager;

    @Override
    public void request(ShineMessageRequest request) throws BusinessException {
        Assert.notNull(request, "request参数不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(request.getTerminalId());
        Message message = new Message(Constants.SYSTEM_TYPE, request.getAction(), request.getData());

        sender.request(message);
    }

    @Override
    public ShineMessageResponse syncRequest(ShineMessageRequest request) throws IOException, InterruptedException,
            BusinessException {
        Assert.notNull(request, "request参数不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(request.getTerminalId());
        Message message = new Message(Constants.SYSTEM_TYPE, request.getAction(), request.getData());

        BaseMessage baseMessage = sender.syncRequest(message);
        ShineMessageResponse shineMessageResponse = new ShineMessageResponse();
        shineMessageResponse.setAction(baseMessage.getAction());
        shineMessageResponse.setData(baseMessage.getData());

        return shineMessageResponse;
    }

    @Override
    public void asyncRequest(ShineMessageRequest request, TerminalCallback callback) throws BusinessException {
        Assert.notNull(request, "request参数不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(request.getTerminalId());
        Message message = new Message(Constants.SYSTEM_TYPE, request.getAction(), request.getData());

        sender.asyncRequest(message, new AbstractRequestCallback() {
            @Override
            public void success(BaseMessage baseMessage) {
                Assert.notNull(baseMessage, "baseMessage参数不能为空");
                Assert.notNull(baseMessage.getAction(), "action不能为空");
                ShineMessageResponse shineMessageResponse = new ShineMessageResponse();
                shineMessageResponse.setAction(baseMessage.getAction());
                shineMessageResponse.setData(baseMessage.getData());
                shineMessageResponse.setTerminalId(request.getTerminalId());
                callback.success(shineMessageResponse);
            }

            @Override
            public void timeout(Throwable throwable) {
                callback.timeout();
            }
        });
    }

    @Override
    public void response(ShineMessageRequest msg) throws BusinessException {
        Assert.notNull(msg, "ShineMessageRequest");

        Session session = sessionManager.getSession(msg.getTerminalId());
        if (session == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        DefaultResponseMessageSender sender = new DefaultResponseMessageSender(msg.getRequestId(), session);
        Message message = new Message(Constants.SYSTEM_TYPE, msg.getAction(), msg.getData());
        sender.response(message);
    }
}
