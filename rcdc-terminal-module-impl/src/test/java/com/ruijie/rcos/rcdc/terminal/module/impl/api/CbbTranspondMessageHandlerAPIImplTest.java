package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.callback.RequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultResponseMessageSender;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/2
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class CbbTranspondMessageHandlerAPIImplTest {
    @Tested
    private CbbTranspondMessageHandlerAPIImpl transpondMessageHandlerAPI;

    @Injectable
    private SessionManager sessionManager;
    @Injectable
    private DefaultRequestMessageSender sender;
    @Injectable
    private RequestCallback requestCallback;

    @Test
    public void testRequest() {

        new Expectations() {{
            try {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }};
        CbbShineMessageRequest request = CbbShineMessageRequest.create("login","223");
        try {
            transpondMessageHandlerAPI.request(request);
        } catch (BusinessException e) {
            fail();
        }
        new Verifications() {{
            sender.request((Message) any);
            times = 1;
        }};
    }

    @Test
    public void testSyncRequestSuccess() throws IOException, InterruptedException, BusinessException {
        String action = "login";
        Map<String,Object> data = new HashMap<>();
        data.put("code",100);
        data.put("content","hello");
        BaseMessage baseMessage = new BaseMessage(action, JSON.toJSONString(data));
        new Expectations() {{
            sessionManager.getRequestMessageSender(anyString);
            result = sender;
            sender.syncRequest((Message) any);
            result = baseMessage;
        }};
        CbbShineMessageRequest request = CbbShineMessageRequest.create("login","223");
        try {
            CbbShineMessageResponse messageResponse = transpondMessageHandlerAPI.syncRequest(request);
            assertEquals(messageResponse.getContent(), "hello");
        } catch (BusinessException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        } catch (IOException e) {
            fail();
        }
        new Verifications() {{
            sender.syncRequest((Message) any);
            times = 1;

        }};
    }

    @Test
    public void testAsyncRequest(@Mocked CbbTerminalCallback cbbTerminalCallback) throws BusinessException, IOException,
            InterruptedException {
        new Expectations() {{
            sessionManager.getRequestMessageSender(anyString);
            result = sender;
            sender.asyncRequest((Message) any, (RequestCallback) any);
        }};

        try {
            String action = "login";
            String terminalId = "123";
            CbbShineMessageRequest request = CbbShineMessageRequest.create(action,terminalId);
            transpondMessageHandlerAPI.asyncRequest(request, cbbTerminalCallback);
        } catch (BusinessException e) {
            fail();
        }


        new Verifications() {{
            sender.asyncRequest((Message) any, (RequestCallback) any);
            times = 1;
        }};

    }

    @Test
    public void testResponse(@Mocked Session session, @Mocked DefaultResponseMessageSender sender) {
        new Expectations() {{
            sessionManager.getSession(anyString);
            result = session;
            sender.response((Message) any);
        }};
        try {
            String action = "login";
            String terminalId = "123";
            String requestId = "333";
            CbbResponseShineMessage request = CbbResponseShineMessage.create(action,terminalId,requestId);
            transpondMessageHandlerAPI.response(request);
        } catch (Exception e) {
            fail();
        }

    }
}