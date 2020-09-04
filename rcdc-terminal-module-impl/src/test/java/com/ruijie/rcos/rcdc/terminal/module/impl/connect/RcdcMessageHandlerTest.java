package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.ResponseMessageSender;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import io.netty.channel.ChannelHandlerContext;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: 连接监听测试类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class RcdcMessageHandlerTest {

    @Tested
    private RcdcMessageHandler connectEventHandler;


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

    @Injectable
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;

    /**
     * 测试第一个报文正常执行逻辑过程
     *
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveFirstMessageNormal(@Mocked Session session) throws InterruptedException {
        String terminalId = "01-1C-42-F1-2D-45";
        TerminalInfo info = new TerminalInfo(terminalId, "172.21.12.3");
        new MockUp<BaseCreateSystemLogRequest>() {
            @Mock
            public void $init(String key, String... args) {

            }
        };
        new Expectations() {
            {
                sessionManager.bindSession(terminalId, (Session) any);
                result = null;
            }
        };


        String action = ShineAction.CHECK_UPGRADE;
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
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
     * 测试第一个报文正常执行逻辑过程
     *
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveFirstMessageForOnline(@Mocked Session session) throws InterruptedException {
        String terminalId = "01-1C-42-F1-2D-45";
        TerminalInfo info = new TerminalInfo(terminalId, "172.21.12.3");
        new MockUp<BaseCreateSystemLogRequest>() {
            @Mock
            public void $init(String key, String... args) {

            }
        };
        new Expectations() {
            {
                sessionManager.getSessionByAlias(terminalId);
                result = null;
                sessionManager.bindSession(terminalId, (Session) any);
                result = null;
            }
        };


        String action = ShineAction.CHECK_UPGRADE;
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
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
     *
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveNotFirstMessage(@Mocked Session session) throws InterruptedException {
        String terminalId = "01-1C-42-F1-2D-45";
        TerminalInfo info = new TerminalInfo(terminalId, "172.21.12.3");

        try {
            String action = ShineAction.COLLECT_TERMINAL_LOG_FINISH;
            CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
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
     * 测试OnReceive，心跳报文
     *
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveHeartBeat(@Mocked Session session) throws InterruptedException {
        String action = ShineAction.HEARTBEAT;
        BaseMessage baseMessage = new BaseMessage(action, null);

        connectEventHandler.onReceive(sender, baseMessage);
        Thread.sleep(1000);
        new Verifications() {
            {
                cbbDispatcherHandlerSPI.dispatch((CbbDispatcherRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 测试OnReceive，同步服务器时间报文
     *
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveSyncServerTime(@Mocked Session session) throws InterruptedException {
        String action = ShineAction.SYNC_SERVER_TIME;
        BaseMessage baseMessage = new BaseMessage(action, null);

        connectEventHandler.onReceive(sender, baseMessage);
        Thread.sleep(1000);
        new Verifications() {
            {
                cbbDispatcherHandlerSPI.dispatch((CbbDispatcherRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 测试OnReceive，终端未绑定session
     *
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveNoBindSession(@Mocked Session session) throws InterruptedException {
        new Expectations() {
            {

            }
        };
        String action = ShineAction.TERMINAL_DETECT;
        BaseMessage baseMessage = new BaseMessage(action, "");

        connectEventHandler.onReceive(sender, baseMessage);
        Thread.sleep(1000);
        new Verifications() {
            {
                cbbDispatcherHandlerSPI.dispatch((CbbDispatcherRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 测试OnReceive，绑定session时，数据格式错误
     *
     * @param session session连接
     * @throws InterruptedException 异常
     */
    @Test
    public void testOnReceiveBindSessionDataFormatError(@Mocked Session session) throws InterruptedException {
        String action = ShineAction.CHECK_UPGRADE;
        BaseMessage baseMessage = new BaseMessage(action, "sdsd");

        connectEventHandler.onReceive(sender, baseMessage);
        Thread.sleep(1000);
        new Verifications() {
            {
                sessionManager.bindSession(anyString, session);
                times = 0;
                cbbDispatcherHandlerSPI.dispatch((CbbDispatcherRequest) any);
                times = 0;
            }
        };
    }

}
