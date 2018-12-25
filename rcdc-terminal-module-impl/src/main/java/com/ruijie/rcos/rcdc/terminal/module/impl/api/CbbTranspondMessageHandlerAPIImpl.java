package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.ShineResponseMessageDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultResponseMessageSender;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Description: 转发消息给终端（Shine）
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @author Jarman
 */
public class CbbTranspondMessageHandlerAPIImpl implements CbbTranspondMessageHandlerAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTranspondMessageHandlerAPIImpl.class);

    @Autowired
    private SessionManager sessionManager;

    @Override
    public DefaultResponse request(CbbShineMessageRequest request) throws BusinessException {
        Assert.notNull(request, "request参数不能为空");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("接收到request请求消息：{}", request.toString());
        }
        DefaultRequestMessageSender sender = getRequestSender(request.getTerminalId());
        sender.request(wrapMessage(request));
        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbShineMessageResponse syncRequest(CbbShineMessageRequest request) throws IOException, InterruptedException,
            BusinessException {
        Assert.notNull(request, "request参数不能为空");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("接收到syncRequest请求消息：{}", request.toString());
        }
        DefaultRequestMessageSender sender = getRequestSender(request.getTerminalId());

        BaseMessage baseMessage = sender.syncRequest(wrapMessage(request));
        CbbShineMessageResponse cbbShineMessageResponse = new CbbShineMessageResponse();
        cbbShineMessageResponse.setAction(baseMessage.getAction());
        cbbShineMessageResponse.setData(baseMessage.getData());

        return cbbShineMessageResponse;
    }

    @Override
    public DefaultResponse asyncRequest(CbbShineMessageRequest request, CbbTerminalCallback callback) throws BusinessException {
        Assert.notNull(request, "request参数不能为null");
        Assert.notNull(callback, "CbbTerminalCallback 不能为null");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("接收到asyncRequest请求消息：{}", request.toString());
        }
        DefaultRequestMessageSender sender = getRequestSender(request.getTerminalId());
        sender.asyncRequest(wrapMessage(request), new AsyncRequestCallBack(request.getTerminalId(), callback));
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse response(CbbResponseShineMessage msg) throws BusinessException {
        Assert.notNull(msg, "CbbResponseShineMessage不能为null");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("接收到response应答消息：{}", msg.toString());
        }
        Session session = sessionManager.getSession(msg.getTerminalId());
        if (session == null) {
            LOGGER.error("终端处于离线状态，消息无法发出;terminal:{}", msg.getTerminalId());
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        DefaultResponseMessageSender sender = new DefaultResponseMessageSender(msg.getRequestId(), session);
        ShineResponseMessageDTO shineResponseMessageDTO = new ShineResponseMessageDTO();
        shineResponseMessageDTO.setErrorCode(msg.getErrorCode());
        shineResponseMessageDTO.setErrorMsg(msg.getErrorMsg());
        shineResponseMessageDTO.setContent(msg.getContent());
        Message message = new Message(Constants.SYSTEM_TYPE, msg.getAction(), shineResponseMessageDTO);
        sender.response(message);
        return DefaultResponse.Builder.success();
    }

    private Message wrapMessage(CbbShineMessageRequest messageRequest) {
        return new Message(Constants.SYSTEM_TYPE, messageRequest.getAction(), messageRequest.getContent());
    }

    private DefaultRequestMessageSender getRequestSender(String terminalId) throws BusinessException {
        return sessionManager.getRequestMessageSender(terminalId);
    }
}
