package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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
     * 测试绑定终端session
     *
     * @param session 连接会话
     */
    @Test
    public void testBindSession(@Mocked Session session) {
        String terminalId = "123";
        Map<String, String> map1 = Deencapsulation.getField(sessionManager, "SESSIONID_TERMINALID_MAPPING_MAP");
        Map<String, String> map2 = Deencapsulation.getField(sessionManager, "TERMINALID_SESSIONID_MAPPING_MAP");
        Map<String, Session> sessionAliseMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");
        new Expectations() {
            {
                session.getId();
                result = "aaa";
            }
        };
        sessionManager.bindSession(terminalId, session);
        Assert.assertEquals(1, sessionAliseMap.size());
        Assert.assertEquals(1, map1.size());
        Assert.assertEquals(1, map2.size());
        map1.clear();
        map2.clear();
        sessionAliseMap.clear();
    }

    /**
     * 测试移除session
     *
     * @param session 连接会话
     */
    @Test
    public void testRemoveSession(@Mocked Session session) {
        String terminalId = "321";
        String sessionId = "aaa";
        Map<String, String> map1 = Deencapsulation.getField(sessionManager, "SESSIONID_TERMINALID_MAPPING_MAP");
        Map<String, String> map2 = Deencapsulation.getField(sessionManager, "TERMINALID_SESSIONID_MAPPING_MAP");
        Map<String, Session> sessionAliseMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");

        new Expectations() {
            {
                session.getId();
                result = "aaa";

            }
        };
        new MockUp<BaseCreateSystemLogRequest>() {
            @Mock
            public void $init(String key, String... args) {

            }
        };

        map1.put(sessionId, terminalId);
        map2.put(terminalId, sessionId);
        sessionAliseMap.put(terminalId, session);
        boolean isSuccess = sessionManager.removeSession(session.getId());
        Assert.assertTrue(isSuccess);
        Assert.assertEquals(0, sessionAliseMap.size());
        sessionAliseMap.clear();
        map1.clear();
        map2.clear();
    }

    /**
     * 测试移除session
     *
     * @param session 连接会话
     */
    @Test
    public void testRemoveSessionFail(@Mocked Session session) {
        String terminalId = "321";
        String sessionId = "aaa";
        Map<String, String> map1 = Deencapsulation.getField(sessionManager, "SESSIONID_TERMINALID_MAPPING_MAP");
        Map<String, String> map2 = Deencapsulation.getField(sessionManager, "TERMINALID_SESSIONID_MAPPING_MAP");
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

        map1.put(sessionId, terminalId);
        map2.put(terminalId, sessionId);
        sessionAliseMap.put(terminalId, session);
        boolean isSuccess = sessionManager.removeSession(session.getId());
        assertFalse(isSuccess);
        Assert.assertEquals(1, sessionAliseMap.size());
        sessionAliseMap.clear();
        map1.clear();
        map2.clear();
    }

    /**
     * testRemoveSessionSessionHasChanged
     *
     */
    @Test
    public void testRemoveSessionSessionHasChanged() {
        String terminalId = "321";
        String sessionId = "aaa";
        Map<String, String> map1 = Deencapsulation.getField(sessionManager, "SESSIONID_TERMINALID_MAPPING_MAP");
        Map<String, String> map2 = Deencapsulation.getField(sessionManager, "TERMINALID_SESSIONID_MAPPING_MAP");


        map1.put(sessionId, terminalId);
        map2.put(terminalId, "bbb");
        boolean isSuccess = sessionManager.removeSession(sessionId);
        assertFalse(isSuccess);
        map1.clear();
        map2.clear();
    }

    /**
     * 测试获取session
     *
     * @param session 连接会话
     */
    @Test
    public void testGetSession(@Mocked Session session) {
        String terminalId = "123456";
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
        Map<String, Session> sessionAliasMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");
        sessionAliasMap.put(terminalId, session);
        List<String> terminalIdList = sessionManager.getOnlineTerminalId();
        assertEquals(1, terminalIdList.size());
        assertEquals("12", terminalIdList.get(0));
        sessionAliasMap.clear();
    }

    /**
     * 测试getSessionById
     */
    @Test
    public void testGetSessionById(@Mocked Session session) {
        final String sessionId = "sessionId";
        final Map<String, String> sessionidTerminalidMappingMap = Deencapsulation.getField(sessionManager, "SESSIONID_TERMINALID_MAPPING_MAP");
        final Map<String, Session> sessionAliasMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");
        sessionidTerminalidMappingMap.put(sessionId, "123");
        sessionAliasMap.put("123", session);

        new Expectations() {
            {
                session.getId();
                result = sessionId;
            }
        };

        assertEquals(session, sessionManager.getSessionById(sessionId));
        sessionidTerminalidMappingMap.clear();
        sessionAliasMap.clear();
    }

    /**
     * testGetSessionByIdTerminalIdIsBlank
     */
    @Test
    public void testGetSessionByIdTerminalIdIsBlank(@Mocked Session session) {
        final String sessionId = "sessionId";

        assertEquals(null, sessionManager.getSessionById(sessionId));
    }

    /**
     * testGetSessionByIdSessionChanged
     */
    @Test
    public void testGetSessionByIdSessionChanged(@Mocked Session session) {
        final String sessionId = "sessionId";
        final Map<String, String> sessionidTerminalidMappingMap = Deencapsulation.getField(sessionManager, "SESSIONID_TERMINALID_MAPPING_MAP");
        final Map<String, Session> sessionAliasMap = Deencapsulation.getField(sessionManager, "SESSION_ALIAS_MAP");
        sessionidTerminalidMappingMap.put(sessionId, "123");
        sessionAliasMap.put("123", session);

        new MockUp<Session>() {
            @Mock
            public String getId() {
                return "aaa";
            }
        };

        assertEquals(null, sessionManager.getSessionById(sessionId));
        sessionidTerminalidMappingMap.clear();
        sessionAliasMap.clear();
    }

    /**
     * testGetTerminalIdBySessionId
     */
    @Test
    public void testGetTerminalIdBySessionId() {
        String terminalId = "123";
        String sessionId = "aaa";
        final Map<String, String> map1 = Deencapsulation.getField(sessionManager, "SESSIONID_TERMINALID_MAPPING_MAP");
        map1.put(sessionId, terminalId);

        String result = sessionManager.getTerminalIdBySessionId(sessionId);
        assertEquals(terminalId, result);
        map1.clear();
    }
}
