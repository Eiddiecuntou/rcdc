package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import static org.junit.Assert.*;
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
public class CloseTerminalBatchTaskHandlerTest {

    @Injectable
    private ProgrammaticOptLogRecorder optLogRecorder;

    @Injectable
    private CbbTerminalOperatorAPI terminalOperatorAPI;

    @Injectable
    private Iterator<? extends BatchTaskItem> iterator;

    /**
     * 测试processItem,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testProcessItemArgumentIsNull() throws Exception {
        Map<UUID, String> idMap = new HashMap<>();
        CloseTerminalBatchTaskHandler handler = new CloseTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.processItem(null), "BatchTaskItem不能为null");
        assertTrue(true);
    }

    /**
     * 测试processItem,
     * 
     * @param taskItem mock taskItem
     * @throws BusinessException 异常
     */
    @Test
    public void testProcessItem(@Mocked BatchTaskItem taskItem) throws BusinessException {
        Map<UUID, String> idMap = new HashMap<>();
        CloseTerminalBatchTaskHandler handler = new CloseTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        BatchTaskItemResult result = handler.processItem(taskItem);
        assertEquals(BatchTaskItemStatus.SUCCESS, result.getItemStatus());
        new Verifications() {
            {
                terminalOperatorAPI.shutdown((CbbTerminalIdRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(anyString, anyString);
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
        CloseTerminalBatchTaskHandler handler = new CloseTerminalBatchTaskHandler(terminalOperatorAPI, idMap, iterator, optLogRecorder);
        try {
            handler.onFinish(2, 0);
        } catch (Exception e) {
            fail();
        }
    }

}
