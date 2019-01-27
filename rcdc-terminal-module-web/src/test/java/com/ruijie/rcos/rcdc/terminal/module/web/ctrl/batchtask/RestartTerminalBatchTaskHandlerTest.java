package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

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
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemStatus;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
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
public class RestartTerminalBatchTaskHandlerTest {

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
        RestartTerminalBatchTaskHandler handler = new RestartTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.processItem(null), "BatchTaskItem不能为null");
        assertTrue(true);
    }
    
    /**
     * 测试processItem,参数为空
     * @param taskItem mock taskItem
     * @throws BusinessException 异常
     */
    @Test
    public void testProcessItem(@Mocked BatchTaskItem taskItem) throws BusinessException {
        Map<UUID, String> idMap = new HashMap<>();
        RestartTerminalBatchTaskHandler handler = new RestartTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        BatchTaskItemResult result = handler.processItem(taskItem);
        assertEquals(BatchTaskItemStatus.SUCCESS, result.getItemStatus());
        assertEquals(BusinessKey.RCDC_TERMINAL_RESTART_RESULT_SUCCESS, result.getMsgKey());
        new Verifications() {
            {
                terminalOperatorAPI.restart((CbbTerminalIdRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(anyString, anyString, anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试afterException,参数为空
     * @param taskItem mock taskItem
     * @throws Exception 异常
     */
    @Test
    public void testAfterExceptionArgumentIsNull(@Mocked BatchTaskItem taskItem) throws Exception {
        Map<UUID, String> idMap = new HashMap<>();
        RestartTerminalBatchTaskHandler handler = new RestartTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.afterException(null, new RuntimeException()), "item is not null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.afterException(taskItem, null), "exception is not null");
        assertTrue(true);
    }
    
    /**
     * 测试afterException,不是BusinessException
     * @param taskItem mock taskItem
     * @throws Exception 异常
     */
    @Test
    public void testAfterExceptionNotBusinessException(@Mocked BatchTaskItem taskItem) {
        Map<UUID, String> idMap = new HashMap<>();
        RestartTerminalBatchTaskHandler handler = new RestartTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        try {
            handler.afterException(taskItem, new RuntimeException());
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("重启终端异常，terminalId为"));
        }
        new Verifications() {
            {
                optLogRecorder.saveOptLog(anyString, anyString);
                times = 0;
            }
        };
    }
    
    /**
     * 测试afterException,是BusinessException
     * @param taskItem mock taskItem
     * @throws Exception 异常
     */
    @Test
    public void testAfterExceptionIsBusinessException(@Mocked BatchTaskItem taskItem) {
        Map<UUID, String> idMap = new HashMap<>();
        RestartTerminalBatchTaskHandler handler = new RestartTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        
        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "xxxxx";
            }
        };
        try {
            handler.afterException(taskItem, new BusinessException("key"));
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_RESTART_FAIL_LOG, "xxxxx");
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
        RestartTerminalBatchTaskHandler handler = new RestartTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        try {
            handler.onFinish(2, 0);
        } catch (Exception e) {
            fail();
        }
    }
}
