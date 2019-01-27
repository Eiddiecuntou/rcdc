package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemStatus;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class StartDetectBatchtaskHandlerTest {

    @Injectable
    private ProgrammaticOptLogRecorder optLogRecorder;

    @Injectable
    private CbbTerminalOperatorAPI terminalOperatorAPI;
    
    @Injectable
    private Iterator<? extends BatchTaskItem> iterator;
    
    /**
     * 测试processItem,参数为空
     * @throws Exception 异常
     */
    @Test
    public void testProcessItemArgumentIsNull() throws Exception {
        Map<UUID, String> idMap = new HashMap<>();
        TerminalDetectController controller = new TerminalDetectController();
        TerminalDetectController.StartDetectBatchtaskHandler handler = 
                controller.new StartDetectBatchtaskHandler(idMap, terminalOperatorAPI, iterator, optLogRecorder);
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.processItem(null), "taskItem can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试processItem,
     * @param taskItem mock taskItem
     * @throws BusinessException 异常
     */
    @Test
    public void testProcessItem(@Mocked BatchTaskItem taskItem) throws BusinessException {
        Map<UUID, String> idMap = new HashMap<>();
        TerminalDetectController controller = new TerminalDetectController();
        TerminalDetectController.StartDetectBatchtaskHandler handler = 
                controller.new StartDetectBatchtaskHandler(idMap, terminalOperatorAPI, iterator, optLogRecorder);
        BatchTaskItemResult result = handler.processItem(taskItem);
        assertEquals(BatchTaskItemStatus.SUCCESS, result.getItemStatus());
        assertEquals(BusinessKey.RCDC_TERMINAL_START_DETECT_SUCCESS_LOG, result.getMsgKey());
        new Verifications() {
            {
                terminalOperatorAPI.getTerminalBaiscInfo((CbbTerminalIdRequest) any);
                times = 1;
                terminalOperatorAPI.detect((CbbTerminalDetectRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(anyString, anyString, anyString);
                times = 1;
            }
        };
    }
    
    /**
     * 测试processItem,检测失败
     * @param taskItem mock taskItem
     * @throws BusinessException 异常
     */
    @Test
    public void testProcessItemDetectFail(@Mocked BatchTaskItem taskItem) throws BusinessException {
        new Expectations() {
            {
                terminalOperatorAPI.detect((CbbTerminalDetectRequest) any);
                result = new BusinessException("key");
            }
        };
        
        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "message";
            }
        };
        Map<UUID, String> idMap = new HashMap<>();
        TerminalDetectController controller = new TerminalDetectController();
        TerminalDetectController.StartDetectBatchtaskHandler handler = 
                controller.new StartDetectBatchtaskHandler(idMap, terminalOperatorAPI, iterator, optLogRecorder);
        try {
            handler.processItem(taskItem);
            fail();
        } catch (BusinessException e) {
            assertEquals("key", e.getKey());
        }
        new Verifications() {
            {
                terminalOperatorAPI.getTerminalBaiscInfo((CbbTerminalIdRequest) any);
                times = 1;
                terminalOperatorAPI.detect((CbbTerminalDetectRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_FAIL_LOG, "message");
                times = 1;
            }
        };
    }
    
    /**
     * 测试onFinish
     */
    @Test
    public void testOnFinish() {
        Map<UUID, String> idMap = new HashMap<>();
        TerminalDetectController controller = new TerminalDetectController();
        TerminalDetectController.StartDetectBatchtaskHandler handler = 
                controller.new StartDetectBatchtaskHandler(idMap, terminalOperatorAPI, iterator, optLogRecorder);
        try {
            handler.onFinish(2, 0);
        } catch (Exception e) {
            fail();
        }
    }
}
