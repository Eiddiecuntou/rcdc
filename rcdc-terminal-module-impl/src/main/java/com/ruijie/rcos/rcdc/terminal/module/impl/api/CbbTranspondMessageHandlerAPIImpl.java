package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.compatible.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.codec.compatible.base.sender.DefaultResponseMessageSender;
import com.ruijie.rcos.rcdc.codec.compatible.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.compatible.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.ShineResponseMessageDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.api.callback.AsyncRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import org.apache.commons.lang3.StringUtils;
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
    public void request(CbbShineMessageRequest request) throws BusinessException {
        Assert.notNull(request, "request参数不能为空");
        LOGGER.info("接收到request请求消息：{}", request.toString());

        DefaultRequestMessageSender sender = getRequestSender(request.getTerminalId());
        sender.request(wrapMessage(request));
    }

    @Override
    public CbbShineMessageResponse syncRequest(CbbShineMessageRequest request) throws IOException, InterruptedException, BusinessException {
        Assert.notNull(request, "request参数不能为空");
        LOGGER.info("接收到syncRequest请求消息：{}", request.toString());

        DefaultRequestMessageSender sender = getRequestSender(request.getTerminalId());
        BaseMessage baseMessage = sender.syncRequest(wrapMessage(request));
        Object data = baseMessage.getData();
        if (data == null || StringUtils.isBlank(data.toString())) {
            throw new IllegalArgumentException("执行syncRequest方法后shine返回的应答消息不能为空。data:" + data);
        }
        CbbShineMessageResponse cbbShineMessageResponse = JSON.parseObject(data.toString(), CbbShineMessageResponse.class);
        return cbbShineMessageResponse;
    }

    @Override
    public void asyncRequest(CbbShineMessageRequest request, CbbTerminalCallback callback) throws BusinessException {
        Assert.notNull(request, "request参数不能为null");
        Assert.notNull(callback, "CbbTerminalCallback 不能为null");

        LOGGER.debug("接收到asyncRequest请求消息：{}", request.toString());

        DefaultRequestMessageSender sender = getRequestSender(request.getTerminalId());
        sender.asyncRequest(wrapMessage(request), new AsyncRequestCallBack(request.getTerminalId(), callback));
    }

    @Override
    public void response(CbbResponseShineMessage msg) {
        Assert.notNull(msg, "CbbResponseShineMessage不能为null");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RCDC应答消息：{}", msg.toString());
        }
        Session session = sessionManager.getSessionByAlias(msg.getTerminalId());
        if (session == null) {
            throw new IllegalStateException("终端处于离线状态，消息无法发出;terminal:" + msg.getTerminalId());
        }
        DefaultResponseMessageSender sender = new DefaultResponseMessageSender(msg.getRequestId(), session);
        ShineResponseMessageDTO shineResponseMessageDTO = new ShineResponseMessageDTO();
        shineResponseMessageDTO.setCode(msg.getCode());
        shineResponseMessageDTO.setContent(msg.getContent());
        Message message = new Message(Constants.SYSTEM_TYPE, msg.getAction(), shineResponseMessageDTO);
        sender.response(message);
    }

    private Message wrapMessage(CbbShineMessageRequest messageRequest) {
        return new Message(Constants.SYSTEM_TYPE, messageRequest.getAction(), messageRequest.getContent());
    }

    private DefaultRequestMessageSender getRequestSender(String terminalId) throws BusinessException {
        return sessionManager.getRequestMessageSender(terminalId);
    }
}
