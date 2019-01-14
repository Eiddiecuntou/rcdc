package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeTerminalPasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.callback.RequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import mockit.Expectations;
import mockit.Injectable;
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
public class TerminalOperatorServiceImplTest {

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private CollectLogCacheManager collectLogCacheManager;

    @Injectable
    DefaultRequestMessageSender sender;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Tested
    private TerminalOperatorServiceImpl operatorService;
    
    @Injectable
    private TerminalDetectService terminalDetectService;

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
            ChangeTerminalPasswordRequest data = (ChangeTerminalPasswordRequest)message.getData();
            assertEquals(String.valueOf(data.getPassword()), password);
        }};
    }

    @Test
    public void testCollectLogIsDoing() {
        String terminalId = "12334";
        CollectLogCache collectLogCache = new CollectLogCache();
        collectLogCache.setState(CollectLogStateEnums.DOING);
        new Expectations() {{
            collectLogCacheManager.getCache(terminalId);
            result = collectLogCache;
        }};

        try {
            operatorService.collectLog(terminalId);
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_COLLECT_LOG_DOING);
        }
    }

    @Test
    public void testCollectLogNoExistsAndIsDoing() {
        String terminalId = "123";
        CollectLogCache logCache = new CollectLogCache();
        logCache.setState(CollectLogStateEnums.DOING);
        new Expectations() {{
            collectLogCacheManager.getCache(terminalId);
            result = null;
            collectLogCacheManager.addCache(terminalId);
            result = logCache;
        }};

        try {
            operatorService.collectLog(terminalId);
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_COLLECT_LOG_DOING);
        }
    }

    @Test
    public void testCollectLogSend() throws BusinessException {
        CollectLogCache logCache = new CollectLogCache();
        logCache.setState(CollectLogStateEnums.DONE);
        new Expectations() {{
            collectLogCacheManager.getCache(anyString);
            result = logCache;
            sessionManager.getRequestMessageSender(anyString);
            result = sender;
            sender.asyncRequest((Message) any, (RequestCallback) any);

        }};

        try {
            String terminalId = "123";
            operatorService.collectLog(terminalId);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {{
            sender.asyncRequest((Message) any, (RequestCallback) any);
            times = 1;
        }};
    }
    
    @Test
    public void testDetect() throws BusinessException {
        String terminalId = "123";
        
        new Expectations() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                result = new TerminalDetectionEntity();
            }
        };
        //TODO 未完成
        operatorService.detect(terminalId);
        
        new Verifications() {
            {
                terminalDetectService.findInCurrentDate(anyString);
                times = 1;
            }
        };
    }

}