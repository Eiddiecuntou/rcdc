package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

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

    /**
     * 测试异步请求
     */
    @Test
    public void testRequest() {

        new Expectations() {
            {
                try {
                    sessionManager.getRequestMessageSender(anyString);
                    result = sender;
                } catch (BusinessException e) {
                    result = sender;
                }
            }
        };
        CbbShineMessageRequest request = CbbShineMessageRequest.create("login", "223");
        try {
            transpondMessageHandlerAPI.request(request);
        } catch (BusinessException e) {
            fail();
        }
        new Verifications() {
            {
                sender.request((Message) any);
                times = 1;
            }
        };
    }

    /**
     * 测试同步发送成功
     * @throws IOException io异常
     * @throws InterruptedException 中断异常
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSyncRequestSuccess() throws IOException, InterruptedException, BusinessException {
        String action = "login";
        Map<String, Object> data = new HashMap<>();
        data.put("code", 100);
        data.put("content", "hello");
        BaseMessage baseMessage = new BaseMessage(action, JSON.toJSONString(data));
        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.syncRequest((Message) any);
                result = baseMessage;
            }
        };
        CbbShineMessageRequest request = CbbShineMessageRequest.create("login", "223");
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
        new Verifications() {
            {
                sender.syncRequest((Message) any);
                times = 1;

            }
        };
    }

    /**
     * 测试异步请求
     * @param cbbTerminalCallback 回调
     * @throws BusinessException 业务异常
     * @throws IOException io异常
     * @throws InterruptedException 中断异常
     */
    @Test
    public void testAsyncRequest(@Mocked CbbTerminalCallback cbbTerminalCallback)
            throws BusinessException, IOException, InterruptedException {
        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.asyncRequest((Message) any, (RequestCallback) any);
            }
        };

        try {
            String action = "login";
            String terminalId = "123";
            CbbShineMessageRequest request = CbbShineMessageRequest.create(action, terminalId);
            transpondMessageHandlerAPI.asyncRequest(request, cbbTerminalCallback);
        } catch (BusinessException e) {
            fail();
        }


        new Verifications() {
            {
                sender.asyncRequest((Message) any, (RequestCallback) any);
                times = 1;
            }
        };

    }

    /**
     * 测试终端响应
     * @param session session
     * @param sender 发送对象
     */
    @Test
    public void testResponse(@Mocked Session session, @Mocked DefaultResponseMessageSender sender) {
        new Expectations() {
            {
                sessionManager.getSession(anyString);
                result = session;
                sender.response((Message) any);
            }
        };
        try {
            String action = "login";
            String terminalId = "123";
            String requestId = "333";
            CbbResponseShineMessage request = CbbResponseShineMessage.create(action, terminalId, requestId);
            transpondMessageHandlerAPI.response(request);
        } catch (Exception e) {
            fail();
        }

    }
}
