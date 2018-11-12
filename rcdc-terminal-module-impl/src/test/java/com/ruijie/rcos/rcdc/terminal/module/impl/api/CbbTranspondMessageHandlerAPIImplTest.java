package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
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

import java.io.IOException;

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
        CbbShineMessageRequest request = new CbbShineMessageRequest();
        request.setAction("login");
        request.setTerminalId("123");
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
        String data = "test";
        BaseMessage baseMessage = new BaseMessage(action, data);
        new Expectations() {{
            sessionManager.getRequestMessageSender(anyString);
            result = sender;
            sender.syncRequest((Message) any);
            result = baseMessage;
        }};
        CbbShineMessageRequest request = new CbbShineMessageRequest();
        request.setAction("login");
        request.setTerminalId("123");
        try {
            CbbShineMessageResponse messageResponse = transpondMessageHandlerAPI.syncRequest(request);
            assertEquals(messageResponse.getAction(), action);
            assertEquals(messageResponse.getData(), data);
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
    public void testAsyncRequest() throws BusinessException, IOException, InterruptedException {
        new Expectations() {{
            sessionManager.getRequestMessageSender(anyString);
            result = sender;
            sender.asyncRequest((Message) any, (RequestCallback) any);
        }};

        try {
            CbbShineMessageRequest request = new CbbShineMessageRequest();
            request.setAction("login");
            transpondMessageHandlerAPI.asyncRequest(request, null);
        } catch (BusinessException e) {
            fail();
        }


        new Verifications() {{
            sender.asyncRequest((Message) any, (RequestCallback) any);
            times = 1;
        }};

    }

    @Test
    public void testResponse(@Mocked Session session, @Mocked DefaultResponseMessageSender sender) throws BusinessException {
        new Expectations() {{
            sessionManager.getSession(anyString);
            result = session;
            sender.response((Message) any);
        }};
        try {
            CbbShineMessageRequest request = new CbbShineMessageRequest();
            request.setAction("login");
            request.setRequestId("123");
            transpondMessageHandlerAPI.response(request);
        } catch (BusinessException e) {
            fail();
        }

    }
}