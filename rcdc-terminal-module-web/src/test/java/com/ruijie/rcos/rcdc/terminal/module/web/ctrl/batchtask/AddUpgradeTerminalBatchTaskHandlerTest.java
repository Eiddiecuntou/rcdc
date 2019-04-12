package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.sk.base.batch.BatchTaskFinishResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemStatus;
import com.ruijie.rcos.sk.base.batch.BatchTaskStatus;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月7日
 * 
 * @author ls
 */
public class AddUpgradeTerminalBatchTaskHandlerTest {

    @Mocked
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    @Mocked
    private Map<UUID, String> idMap;

    @Mocked
    private Iterator<? extends BatchTaskItem> iterator;

    @Mocked
    private ProgrammaticOptLogRecorder optLogRecorder;

    /**
     * 测试processItem,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testProcessItemArgumentIsNull() throws Exception {

        AddUpgradeTerminalBatchTaskHandler handler = getHander();

        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.processItem(null), "BatchTaskItem不能为null");
        assertTrue(true);
    }

    /**
     * 测试processItem,添加升级终端失败
     * 
     * @throws Exception 异常
     */
    @Test
    public void testProcessItemFail() throws Exception {
        final UUID randomUUID = UUID.randomUUID();
        BatchTaskItem taskItem = new TerminalUpgradeBatchTaskItem(randomUUID, "dsd", randomUUID);
        AddUpgradeTerminalBatchTaskHandler handler = getHander();
        new Expectations() {
            {
                cbbTerminalUpgradeAPI.addSystemUpgradeTerminal((CbbAddTerminalSystemUpgradeTaskRequest) any);
                result = new BusinessException("key");
                idMap.get(randomUUID);
                result = "123";

            }
        };
        new MockUp<BusinessException>() {
            @Mock
            public String getI18nMessage() {
                return "message";
            }
        };
        BatchTaskItemResult result = handler.processItem(taskItem);
        assertEquals(BatchTaskItemStatus.FAILURE, result.getItemStatus());
        assertEquals(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_RESULT_FAIL, result.getMsgKey());
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.addSystemUpgradeTerminal((CbbAddTerminalSystemUpgradeTaskRequest) any);
                times = 1;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_SUCCESS_LOG, anyString);
                times = 0;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_FAIL_LOG, "123", "message");
                times = 1;
            }
        };
    }

    /**
     * 测试processItem,添加升级终端成功
     * 
     * @throws Exception 异常
     */
    @Test
    public void testProcessItemSuccess() throws Exception {
        BatchTaskItem taskItem = new TerminalUpgradeBatchTaskItem(UUID.randomUUID(), "dsd", UUID.randomUUID());
        AddUpgradeTerminalBatchTaskHandler handler = getHander();

        BatchTaskItemResult result = handler.processItem(taskItem);
        assertEquals(BatchTaskItemStatus.SUCCESS, result.getItemStatus());
        assertEquals(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_RESULT_SUCCESS, result.getMsgKey());
        new Verifications() {
            {
                cbbTerminalUpgradeAPI.addSystemUpgradeTerminal((CbbAddTerminalSystemUpgradeTaskRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 测试onFinish
     */
    @Test
    public void testOnFinish() {
        AddUpgradeTerminalBatchTaskHandler handler = getHander();
        BatchTaskFinishResult result = handler.onFinish(1, 0);
        assertEquals(BatchTaskStatus.SUCCESS, result.getStatus());
        assertEquals(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_RESULT, result.getMsgKey());
    }

    private AddUpgradeTerminalBatchTaskHandler getHander() {
        return new AddUpgradeTerminalBatchTaskHandler(cbbTerminalUpgradeAPI, idMap, iterator, optLogRecorder);
    }

}
