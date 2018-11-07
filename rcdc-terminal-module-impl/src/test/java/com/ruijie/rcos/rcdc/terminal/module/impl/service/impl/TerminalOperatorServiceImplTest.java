package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

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
public class TerminalOperatorServiceImplTest {

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private GatherLogCacheManager gatherLogCacheManager;

    @Injectable
    DefaultRequestMessageSender sender;

    @Tested
    private TerminalOperatorServiceImpl operatorService;

    @Test
    public void shutdownSuccess() {
        new Expectations() {{
            try {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }};
        String terminalId = "123";

        try {
            operatorService.shutdown(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {{
            Message message;
            sender.request(message = withCapture());
            assertEquals(message.getAction(), SendTerminalEventEnums.SHUTDOWN_TERMINAL.getName());
        }};
    }

    @Test
    public void restart() {
        new Expectations() {{
            try {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }};
        String terminalId = "123";

        try {
            operatorService.restart(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {{
            Message message;
            sender.request(message = withCapture());
            assertEquals(message.getAction(), SendTerminalEventEnums.RESTART_TERMINAL.getName());
        }};
    }

    @Test
    public void changePassword() {
        new Expectations() {{
            try {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }};
        String terminalId = "123";
        String password = "newpassword";
        try {
            operatorService.changePassword(terminalId, password);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {{
            Message message;
            sender.request(message = withCapture());
            assertEquals(message.getAction(), SendTerminalEventEnums.CHANGE_TERMINAL_PASSWORD.getName());
            assertEquals(String.valueOf(message.getData()), password);
        }};
    }
}