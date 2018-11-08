package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.callback.RequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

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
public class TerminalOperatorServiceImplTest {

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private GatherLogCacheManager gatherLogCacheManager;

    @Injectable
    DefaultRequestMessageSender sender;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Tested
    private TerminalOperatorServiceImpl operatorService;

    @Test
    public void testShutdownSuccess() {
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
    public void testRestart() {
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
    public void testChangePassword() {
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

    @Test
    public void testGatherLogIsDoing() {
        String terminalId = "12334";
        GatherLogCache gatherLogCache = new GatherLogCache();
        gatherLogCache.setState(GatherLogStateEnums.DOING);
        new Expectations() {{
            gatherLogCacheManager.getCache(terminalId);
            result = gatherLogCache;
        }};

        try {
            operatorService.gatherLog(terminalId);
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_GATHER_LOG_DOING);
        }
    }

    @Test
    public void testGatherLogNoExistsAndIsDoing() {
        String terminalId = "123";
        GatherLogCache logCache = new GatherLogCache();
        logCache.setState(GatherLogStateEnums.DOING);
        new Expectations() {{
            gatherLogCacheManager.getCache(terminalId);
            result = null;
            gatherLogCacheManager.addCache(terminalId);
            result = logCache;
        }};

        try {
            operatorService.gatherLog(terminalId);
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_GATHER_LOG_DOING);
        }
    }

    @Test
    public void testGatherLogSend() throws BusinessException {
        GatherLogCache logCache = new GatherLogCache();
        logCache.setState(GatherLogStateEnums.DONE);
        new Expectations() {{
            gatherLogCacheManager.getCache(anyString);
            result = logCache;
            sessionManager.getRequestMessageSender(anyString);
            result = sender;
            sender.asyncRequest((Message) any, (RequestCallback) any);

        }};

        try {
            String terminalId = "123";
            operatorService.gatherLog(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {{
            sender.asyncRequest((Message) any, (RequestCallback) any);
            times = 1;
        }};
    }

    @Test
    public void testDetect() {
        String terminalId = "123";
        try {
            operatorService.detect(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {{
            basicInfoDAO.modifyDetectInfo(anyString, anyInt, (Date) any, anyInt);
            times = 1;
        }};
    }

    @Test
    public void testDetectArr() {
        String[] terminalIdArr = {"1", "2", "3"};
        try {
            operatorService.detect(terminalIdArr);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {{
            basicInfoDAO.modifyDetectInfo(anyString, anyInt, (Date) any, anyInt);
            times = 3;
        }};
    }
}