package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.codec.compatible.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

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

    @Injectable
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;

    /**
     *
     * 测试绑定终端session
     *
     * @param session 连接会话
     */
    @Test
    public void testBindSession(@Mocked Session session) {
        String terminalId = "123";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        Map<String, Session> sessionAliseMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");
        new Expectations() {
            {
                session.getId();
                result = "123";
            }
        };
        sessionManager.bindSession(terminalId, session);
        Assert.assertEquals(1, sessionMap.size());
        sessionMap.clear();
        sessionAliseMap.clear();
    }

    /**
     *
     * 测试移除session
     *
     * @param session 连接会话
     */
    @Test
    public void testRemoveSession(@Mocked Session session) {
        String terminalId = "321";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        Map<String, Session> sessionAliseMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");
        new MockUp<BaseCreateSystemLogRequest>() {
            @Mock
            public void $init(String key, String... args) {

            }
        };
        new Expectations() {
            {
                session.getId();
                result = "123";
            }
        };
        sessionAliseMap.put(terminalId, session);
        sessionManager.removeSession(terminalId, session);
        Assert.assertEquals(0, sessionAliseMap.size());
        sessionAliseMap.clear();
    }

    /**
     *
     * 测试移除session
     *
     * @param session 连接会话
     */
    @Test
    public void testRemoveSessionFail(@Mocked Session session) {
        String terminalId = "321";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        Map<String, Session> sessionAliseMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");

        new MockUp<ConcurrentHashMap>() {
            @Mock
            public boolean remove(Object key, Object value) {
                return false;
            }

        };

        new Expectations() {
            {
                session.getId();
                result = "123";

            }
        };
        new MockUp<BaseCreateSystemLogRequest>() {
            @Mock
            public void $init(String key, String... args) {

            }
        };
        sessionAliseMap.put(terminalId, session);
        boolean isSuccess = sessionManager.removeSession(terminalId, session);
        assertFalse(isSuccess);
        Assert.assertEquals(1, sessionAliseMap.size());
        sessionAliseMap.clear();
    }

    /**
     * 测试获取session
     *
     * @param session 连接会话
     */
    @Test
    public void testGetSession(@Mocked Session session) {
        String terminalId = "123456";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        Map<String, Session> sessionAliasMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");

        sessionAliasMap.put(terminalId, session);
        Session sessionResult = sessionManager.getSessionByAlias(terminalId);
        Assert.assertNotNull(sessionResult);
        sessionAliasMap.clear();
    }

    /**
     * 测试获取连接通道的发送对象
     *
     * @param session 连接
     */
    @Test
    public void testGetRequestMessageSenderNormal(@Mocked Session session) {
        String terminalId = "993993";
        Map<String, Session> sessionAliasMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");
        sessionAliasMap.put(terminalId, session);
        DefaultRequestMessageSender sender = null;
        try {
            sender = sessionManager.getRequestMessageSender(terminalId);
        } catch (BusinessException e) {
            fail();
        }
        Assert.assertNotNull(sender);
        sessionAliasMap.clear();
    }

    /**
     * 测试获取连接通道的发送对象失败
     */
    @Test
    public void testGetRequestMessageSenderAbNormal() {
        String terminalId = "993993";
        try {
            sessionManager.getRequestMessageSender(terminalId);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
    }

    /**
     * 测试getOnlineTerminalId
     *
     * @param session 连接
     */
    @Test
    public void testGetOnlineTerminalId(@Mocked Session session) {
        String terminalId = "12";
        Map<String, Session> sessionMap = Deencapsulation.getField(sessionManager, "SESSION_MAP");
        sessionMap.put(terminalId, session);
        List<String> terminalIdList = sessionManager.getOnlineTerminalId();
        assertEquals(1, terminalIdList.size());
        assertEquals("12", terminalIdList.get(0));
        sessionMap.clear();
    }
}
