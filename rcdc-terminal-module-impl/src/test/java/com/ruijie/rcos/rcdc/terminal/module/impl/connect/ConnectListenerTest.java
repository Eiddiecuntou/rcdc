package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.ReceiveTerminalEvent;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.ResponseMessageSender;
import io.netty.channel.ChannelHandlerContext;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
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
public class ConnectListenerTest {

    @Tested
    private ConnectListener connectListener;


    @Injectable
    private CbbDispatcherHandlerSPI cbbDispatcherHandlerSPI;

    @Injectable
    private CbbTerminalEventNoticeSPI cbbTerminalEventNoticeSPI;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private ChannelHandlerContext channelHandlerContext;

    @Injectable
    private ResponseMessageSender sender;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private GatherLogCacheManager gatherLogCacheManager;

    @Injectable
    private TerminalDetectService detectService;


    /**
     * 测试第一个报文正常执行逻辑过程
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

        String action = ReceiveTerminalEvent.CHECK_UPGRADE;
        ShineTerminalBasicInfo basicInfo = new ShineTerminalBasicInfo();
        basicInfo.setTerminalId(terminalId);
        String data = JSON.toJSONString(basicInfo);
        BaseMessage baseMessage = new BaseMessage(action, data);

        try {
            connectListener.onReceive(sender, baseMessage);
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
            String action = ReceiveTerminalEvent.NOTICE_UPLOAD_LOG_FINISH;
            ShineTerminalBasicInfo basicInfo = new ShineTerminalBasicInfo();
            basicInfo.setTerminalId(terminalId);
            String data = JSON.toJSONString(basicInfo);
            BaseMessage baseMessage = new BaseMessage(action, data);

            connectListener.onReceive(sender, baseMessage);
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

    @Test
    public void testOnConnectSuccessParamIsNull() {
        try {
            connectListener.onConnectSuccess(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "RequestMessageSender不能为null");
        }
    }

    @Test
    public void testOnConnectClosed(@Mocked Session session) throws InterruptedException {
        new Expectations() {
            {
                sessionManager.removeSession(anyString);
                result = null;
                cbbTerminalEventNoticeSPI.notify((CbbNoticeRequest) any);
                result = null;
                session.getAttribute(anyString);
                result = "123";
                gatherLogCacheManager.removeCache(anyString);
            }
        };

        connectListener.onConnectClosed(session);
        Thread.sleep(1000);
        try {
            new Verifications() {
                {
                    sessionManager.removeSession(anyString);
                    times = 1;
                    cbbTerminalEventNoticeSPI.notify((CbbNoticeRequest) any);
                    times = 1;
                }
            };
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testExceptionCaught() {
        try {
            connectListener.exceptionCaught();
        } catch (Exception e) {
            fail();
        }

    }
}