package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.RequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.ResponseMessageSender;
import io.netty.channel.ChannelHandlerContext;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * Description: 连接监听测试类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class ConnectEventHandlerTest {

    @Tested
    private ConnectEventHandler connectEventHandler;


    @Injectable
    private CbbDispatcherHandlerSPI cbbDispatcherHandlerSPI;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private ChannelHandlerContext channelHandlerContext;

    @Injectable
    private ResponseMessageSender sender;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private CollectLogCacheManager collectLogCacheManager;

    @Injectable
    private TerminalDetectService detectService;

    @Injectable
    private CbbTerminalEventNoticeSPI cbbTerminalEventNoticeSPI;


    /**
     * 测试第一个报文正常执行逻辑过程
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveFirstMessageNormal(@Mocked Session session) throws InterruptedException {
        String terminalId = "01-1C-42-F1-2D-45";
        new Expectations() {
            {
                cbbDispatcherHandlerSPI.dispatch((CbbDispatcherRequest) any);
                result = null;
                sessionManager.bindSession(terminalId, (Session) any);
                result = null;
                session.getAttribute(anyString);
                result = terminalId;
            }
        };

        String action = ShineAction.CHECK_UPGRADE;
        ShineTerminalBasicInfo basicInfo = new ShineTerminalBasicInfo();
        basicInfo.setTerminalId(terminalId);
        String data = JSON.toJSONString(basicInfo);
        BaseMessage baseMessage = new BaseMessage(action, data);

        try {
            connectEventHandler.onReceive(sender, baseMessage);
        } catch (Exception e) {
            fail();
        }
        Thread.sleep(1000);
        try {
            new Verifications() {
                {
                    String terId;
                    sessionManager.bindSession(terId = withCapture(), (Session) any);
                    times = 1;
                    assertEquals(terminalId, terId);
                }
            };
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试不是第一个报文执行逻辑过程
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveNotFirstMessage(@Mocked Session session) throws InterruptedException {
        String terminalId = "01-1C-42-F1-2D-45";
        new Expectations() {
            {
                cbbDispatcherHandlerSPI.dispatch((CbbDispatcherRequest) any);
                result = null;
                session.getAttribute(anyString);
                result = terminalId;
            }
        };

        try {
            String action = ShineAction.COLLECT_TERMINAL_LOG_FINISH;
            ShineTerminalBasicInfo basicInfo = new ShineTerminalBasicInfo();
            basicInfo.setTerminalId(terminalId);
            String data = JSON.toJSONString(basicInfo);
            BaseMessage baseMessage = new BaseMessage(action, data);

            connectEventHandler.onReceive(sender, baseMessage);
        } catch (Exception e) {
            fail();
        }
        Thread.sleep(1000);
        try {
            new Verifications() {
                {
                    sessionManager.bindSession(anyString, (Session) any);
                    times = 0;
                }
            };
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试onReceive，收到心跳报文
     */
    @Test
    public void testOnReceiveHeartBeat() {
        
        new MockUp<SkyengineScheduledThreadPoolExecutor>() {
            @Mock
            public void execute(Runnable command) {
                Assert.notNull(command, "command can not be null");
                command.run();
            }
        };
        BaseMessage<JSONObject> message = new BaseMessage<JSONObject>("heartBeat", new JSONObject());
        connectEventHandler.onReceive(sender, message);
        
        new Verifications() {
            {
                Message message;
                sender.response(message = withCapture());
                assertEquals("heartBeat", message.getAction());
                assertEquals(Constants.SYSTEM_TYPE, message.getSystemType());
                assertNull(message.getData());
            }
        };
    }
    
    /**
     * 测试onReceive，收到未绑定且不是第一个报文
     * @param session 连接
     */
    @Test
    public void testOnReceiveNotBandAndFirstMessage(@Mocked Session session) {
        new MockUp<SkyengineScheduledThreadPoolExecutor>() {
            @Mock
            public void execute(Runnable command) {
                Assert.notNull(command, "command can not be null");
                command.run();
            }
        };
        new Expectations() {
            {
                sender.getSession();
                result = session;
                session.getAttribute(anyString);
                result = "";
            }
        };
        BaseMessage<JSONObject> message = new BaseMessage<JSONObject>("sasa", new JSONObject());
        connectEventHandler.onReceive(sender, message);
        
        new Verifications() {
            {
                sender.response((Message) any);
                times = 0;
            }
        };
    }
    
    /**
     * 测试onReceive，收到第一个报文
     * @param session 连接
     */
    @Test
    public void testOnReceiveFirstMessage(@Mocked Session session) {
        new MockUp<SkyengineScheduledThreadPoolExecutor>() {
            @Mock
            public void execute(Runnable command) {
                Assert.notNull(command, "command can not be null");
                command.run();
            }
        };
        new Expectations() {
            {
                sender.getSession();
                result = session;
            }
        };
        BaseMessage<String> message = new BaseMessage<String>("check_upgrade", "sdd");
        try {
            connectEventHandler.onReceive(sender, message);
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("接收到的报文格式错误;data:"));
        }
    }
    
    /**
     * 测试连接成功-参数为空
     */
    @Test
    public void testOnConnectSuccessParamIsNull() {
        try {
            connectEventHandler.onConnectSuccess(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "RequestMessageSender不能为null");
        }
    }
    
    /**
     * 测试连接成功
     * @param requestMessageSender mock requestMessageSender
     */
    @Test
    public void testOnConnectSuccess(@Mocked RequestMessageSender requestMessageSender) {
        try {
            connectEventHandler.onConnectSuccess(requestMessageSender);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试连接关闭 
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnConnectClosed(@Mocked Session session) throws InterruptedException {
        new Expectations() {
            {
                sessionManager.removeSession(anyString);
                result = null;
                session.getAttribute(anyString);
                result = "123";
            }
        };

        connectEventHandler.onConnectClosed(session);
        Thread.sleep(1000);
        try {
            new Verifications() {
                {
                    sessionManager.removeSession(anyString);
                    times = 1;
                }
            };
        } catch (Exception e) {
            fail();
        }

    }

    /**
     * 测试异常捕获
     */
    @Test
    public void testExceptionCaught() {
        try {
            connectEventHandler.exceptionCaught(new Throwable());
        } catch (Exception e) {
            fail();
        }

    }
}