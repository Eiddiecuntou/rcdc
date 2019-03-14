package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.sk.base.batch.AbstractBatchTaskHandler;
import com.ruijie.rcos.sk.base.batch.BatchTaskFinishResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemStatus;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItemResult;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;

/**
 * 
 * Description: 批量重启终端任务处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月18日
 * 
 * @author nt
 */
public class RestartTerminalBatchTaskHandler extends AbstractBatchTaskHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestartTerminalBatchTaskHandler.class);

    private ProgrammaticOptLogRecorder optLogRecorder;

    private CbbTerminalOperatorAPI terminalOperatorAPI;

    private Map<UUID, String> idMap;

    public RestartTerminalBatchTaskHandler(CbbTerminalOperatorAPI terminalOperatorAPI, Map<UUID, String> idMap,
            Iterator<? extends BatchTaskItem> iterator, ProgrammaticOptLogRecorder optLogRecorder) {
        super(iterator);
        this.optLogRecorder = optLogRecorder;
        this.idMap = idMap;
        this.terminalOperatorAPI = terminalOperatorAPI;
    }

    @Override
    public BatchTaskItemResult processItem(BatchTaskItem taskItem) throws BusinessException {
        Assert.notNull(taskItem, "BatchTaskItem不能为null");
        String terminalId = idMap.get(taskItem.getItemID());
        try {
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.restart(request);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_RESTART_SUCCESS_LOG, terminalId);
            return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.SUCCESS)
                    .msgKey(BusinessKey.RCDC_TERMINAL_RESTART_SUCCESS_LOG).msgArgs(new String[] {terminalId}).build();
        } catch (Exception e) {
            LOGGER.error("重启终端失败：" + terminalId, e);
            if (e instanceof BusinessException) {
                BusinessException ex = (BusinessException) e;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_RESTART_FAIL_LOG,terminalId, ex.getI18nMessage());
                return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.FAILURE)
                        .msgKey(BusinessKey.RCDC_TERMINAL_RESTART_FAIL_LOG).msgArgs(new String[] {terminalId,ex.getI18nMessage()}).build();
            } else {
                throw new IllegalStateException("重启终端异常，terminalId为[" + terminalId + "]", e);
            }
        }
    }

    @Override
    public BatchTaskFinishResult onFinish(int successCount, int failCount) {
        return buildDefaultFinishResult(successCount, failCount, BusinessKey.RCDC_TERMINAL_RESTART_RESULT);
    }
}
