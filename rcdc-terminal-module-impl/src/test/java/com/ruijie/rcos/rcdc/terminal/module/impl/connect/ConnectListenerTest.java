package com.ruijie.rcos.rcdc.terminal.module.impl.connect;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.DispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.TerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.DispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.NoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.ReceiveTerminalEvent;
import com.ruijie.rcos.sk.commkit.base.DefaultSession;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.RequestMessageSender;
import com.ruijie.rcos.sk.commkit.base.sender.ResponseMessageSender;
import io.netty.channel.ChannelHandlerContext;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

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
    private DispatcherHandlerSPI dispatcherHandlerSPI;

    @Injectable
    private TerminalEventNoticeSPI terminalEventNoticeSPI;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private ChannelHandlerContext channelHandlerContext;

    @Injectable
    private ResponseMessageSender sender;

    @Injectable
    private TerminalBasicInfoService basicInfoService;


    /**
     * 测试第一个报文正常执行逻辑过程
     */
    @Test
    public void testOnReceiveFirstMessageNormal(@Mocked Session session) {
        String terminalId = "01-1C-42-F1-2D-45";
        new Expectations() {
            {
                dispatcherHandlerSPI.dispatch((DispatcherRequest) any);
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

        connectListener.onReceive(sender, baseMessage);
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
    public void testOnReceiveNotFirstMessage(@Mocked Session session) {
        String terminalId = "01-1C-42-F1-2D-45";
        new Expectations() {
            {
                dispatcherHandlerSPI.dispatch((DispatcherRequest) any);
                result = null;
                session.getAttribute(anyString);
                result = terminalId;
            }
        };

        String action = ReceiveTerminalEvent.NOTICE_UPLOAD_LOG_FINISH;
        ShineTerminalBasicInfo basicInfo = new ShineTerminalBasicInfo();
        basicInfo.setTerminalId(terminalId);
        String data = JSON.toJSONString(basicInfo);
        BaseMessage baseMessage = new BaseMessage(action, data);

        connectListener.onReceive(sender, baseMessage);
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
     * 测试未绑定session情况
     */
    @Test
    public void testOnReceiveNotBindSession(@Mocked Session session) {
        String terminalId = "01-1C-42-F1-2D-45";
        new Expectations() {
            {
                session.getAttribute(anyString);
                result = null;
            }
        };

        String action = ReceiveTerminalEvent.NOTICE_UPLOAD_LOG_FINISH;
        ShineTerminalBasicInfo basicInfo = new ShineTerminalBasicInfo();
        basicInfo.setTerminalId(terminalId);
        String data = JSON.toJSONString(basicInfo);
        BaseMessage baseMessage = new BaseMessage(action, data);

        try {
            connectListener.onReceive(sender, baseMessage);
            fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "session 未绑定终端");
        }
    }

    /**
     * 测试参数未传terminalId
     */
    @Test
    public void testOnReceiveTerminalIsNull() {
        String action = ReceiveTerminalEvent.CHECK_UPGRADE;
        ShineTerminalBasicInfo basicInfo = new ShineTerminalBasicInfo();
        basicInfo.setTerminalId(null);
        String data = JSON.toJSONString(basicInfo);
        BaseMessage baseMessage = new BaseMessage(action, data);

        try {
            connectListener.onReceive(sender, baseMessage);
            Assert.fail();

        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "terminalId不能为空");
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
    public void testOnConnectClosed(@Mocked Session session) {
        new Expectations() {
            {
                sessionManager.removeSession(anyString);
                result = null;
                terminalEventNoticeSPI.notify((NoticeRequest) any);
                result = null;
                session.getAttribute(anyString);
                result = "123";
            }
        };

        connectListener.onConnectClosed(session);

        try {
            new Verifications() {
                {
                    sessionManager.removeSession(anyString);
                    times = 1;
                    terminalEventNoticeSPI.notify((NoticeRequest) any);
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