package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

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
public class SessionManagerTest {

    @Tested
    private SessionManager sessionManager;

    @Test
    public void testBindSession(@Mocked Session session) {
        String terminalId = "123";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        sessionManager.bindSession(terminalId, session);
        Assert.assertEquals(1, sessionMap.size());
        sessionMap.clear();
    }

    @Test
    public void testRemoveSession(@Mocked Session session) {
        String terminalId = "321";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        sessionMap.put(terminalId, session);
        sessionManager.removeSession(terminalId);
        Assert.assertEquals(0, sessionMap.size());
        sessionMap.clear();
    }

    @Test
    public void testGetSession(@Mocked Session session) {
        String terminalId = "123456";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        sessionMap.put(terminalId, session);
        Session sessionResult = sessionManager.getSession(terminalId);
        Assert.assertNotNull(sessionResult);
        sessionMap.clear();
    }

    @Test
    public void testGetRequestMessageSenderNormal(@Mocked Session session) {
        String terminalId = "993993";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        sessionMap.put(terminalId, session);
        DefaultRequestMessageSender sender = null;
        try {
            sender = sessionManager.getRequestMessageSender(terminalId);
        } catch (BusinessException e) {
            fail();
        }
        Assert.assertNotNull(sender);
        sessionMap.clear();
    }

    @Test
    public void testGetRequestMessageSenderAbNormal() {
        String terminalId = "993993";
        try {
            sessionManager.getRequestMessageSender(terminalId);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
    }
}